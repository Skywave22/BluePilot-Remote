package com.bluepilot.remote.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothHidDevice
import android.bluetooth.BluetoothHidDeviceAppQosSettings
import android.bluetooth.BluetoothHidDeviceAppSdpSettings
import android.bluetooth.BluetoothProfile
import android.content.ActivityNotFoundException
import android.content.Intent
import android.provider.Settings
import android.content.Context
import android.os.Build
import android.util.Log
import com.bluepilot.remote.model.ConnectionState
import com.bluepilot.remote.model.ConnectionStateData
import com.bluepilot.remote.model.ConsumerControlCodes
import com.bluepilot.remote.model.GamepadState
import com.bluepilot.remote.model.KeyboardKeyCodes
import com.bluepilot.remote.model.MouseButton
import com.bluepilot.remote.model.RemoteDevice
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.Executors
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Compile-safe Bluetooth HID manager using Android's public BluetoothHidDevice API.
 *
 * Important: Android exposes the HID app configuration as an opaque object returned in
 * Callback.onAppStatusChanged(), not as a public constructor. This class stores that object
 * and uses sendReport()/connect()/unregisterApp() with the real Android signatures.
 */
@Singleton
@SuppressLint("MissingPermission")
class BluetoothHidManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        private const val TAG = "BluetoothHidManager"
        private const val REPORT_ID_KEYBOARD = 1
        private const val REPORT_ID_MOUSE = 2
        private const val REPORT_ID_CONSUMER = 3
        private const val REPORT_ID_SYSTEM = 4
        private const val REPORT_ID_GAMEPAD = 5
        private const val HID_PROFILE_VERSION = 0x0111
        private const val HID_COUNTRY_CODE = 0x00
        private const val HID_VIRTUAL_CABLE = 0x02
        private const val HID_RECONNECT_INITIATE = 0x04
        private const val HID_NORMALLY_CONNECTABLE = 0x0D

        private val KEYBOARD_DESCRIPTOR = byteArrayOf(
            0x05, 0x01, 0x09, 0x06, 0xA1.toByte(), 0x01, 0x85.toByte(), REPORT_ID_KEYBOARD.toByte(),
            0x05, 0x07, 0x19, 0xE0.toByte(), 0x29, 0xE7.toByte(), 0x15, 0x00,
            0x25, 0x01, 0x75, 0x01, 0x95.toByte(), 0x08, 0x81.toByte(), 0x02,
            0x95.toByte(), 0x01, 0x75, 0x08, 0x81.toByte(), 0x01, 0x95.toByte(), 0x05,
            0x75, 0x01, 0x05, 0x08, 0x19, 0x01, 0x29, 0x05,
            0x91.toByte(), 0x02, 0x95.toByte(), 0x01, 0x75, 0x03, 0x91.toByte(), 0x03,
            0x95.toByte(), 0x06, 0x75, 0x08, 0x15, 0x00, 0x25, 0x65,
            0x05, 0x07, 0x19, 0x00, 0x29, 0x65, 0x81.toByte(), 0x00,
            0xC0.toByte()
        )

        private val MOUSE_DESCRIPTOR = byteArrayOf(
            0x05, 0x01, 0x09, 0x02, 0xA1.toByte(), 0x01, 0x85.toByte(), REPORT_ID_MOUSE.toByte(),
            0x09, 0x01, 0xA1.toByte(), 0x00, 0x05, 0x09, 0x19, 0x01,
            0x29, 0x03, 0x15, 0x00, 0x25, 0x01, 0x95.toByte(), 0x03,
            0x75, 0x01, 0x81.toByte(), 0x02, 0x95.toByte(), 0x01, 0x75, 0x05,
            0x81.toByte(), 0x03, 0x05, 0x01, 0x09, 0x30, 0x09, 0x31,
            0x15, 0x81.toByte(), 0x25, 0x7F, 0x75, 0x08, 0x95.toByte(), 0x02,
            0x81.toByte(), 0x06, 0x09, 0x38, 0x15, 0x81.toByte(), 0x25, 0x07,
            0x75, 0x08, 0x95.toByte(), 0x01, 0x81.toByte(), 0x06, 0xC0.toByte(), 0xC0.toByte()
        )

        private val CONSUMER_DESCRIPTOR = byteArrayOf(
            0x05, 0x0C, 0x09, 0x01, 0xA1.toByte(), 0x01, 0x85.toByte(), REPORT_ID_CONSUMER.toByte(),
            0x15, 0x00, 0x26, 0xFF.toByte(), 0x03, 0x19, 0x00, 0x2A, 0xFF.toByte(), 0x03,
            0x75, 0x10, 0x95.toByte(), 0x01, 0x81.toByte(), 0x00, 0xC0.toByte()
        )

        private val SYSTEM_DESCRIPTOR = byteArrayOf(
            0x05, 0x01, 0x09, 0x80.toByte(), 0xA1.toByte(), 0x01, 0x85.toByte(), REPORT_ID_SYSTEM.toByte(),
            0x19, 0x81.toByte(), 0x29, 0x83.toByte(), 0x15, 0x00, 0x25, 0x01,
            0x75, 0x01, 0x95.toByte(), 0x03, 0x81.toByte(), 0x02,
            0x95.toByte(), 0x01, 0x75, 0x05, 0x81.toByte(), 0x03, 0xC0.toByte()
        )

        private val GAMEPAD_DESCRIPTOR = byteArrayOf(
            0x05, 0x01, 0x09, 0x05, 0xA1.toByte(), 0x01, 0x85.toByte(), REPORT_ID_GAMEPAD.toByte(),
            0x05, 0x09, 0x19, 0x01, 0x29, 0x10, 0x15, 0x00,
            0x25, 0x01, 0x95.toByte(), 0x10, 0x75, 0x01, 0x81.toByte(), 0x02,
            0x05, 0x01, 0x09, 0x39, 0x15, 0x00, 0x25, 0x08,
            0x95.toByte(), 0x01, 0x75, 0x08, 0x81.toByte(), 0x02,
            0x05, 0x01, 0x09, 0x30, 0x09, 0x31, 0x09, 0x32,
            0x09, 0x35, 0x15, 0x00, 0x26, 0xFF.toByte(), 0x00, 0x75, 0x08,
            0x95.toByte(), 0x04, 0x81.toByte(), 0x02, 0xC0.toByte()
        )

        private val COMBINED_DESCRIPTOR: ByteArray =
            KEYBOARD_DESCRIPTOR + MOUSE_DESCRIPTOR + CONSUMER_DESCRIPTOR + SYSTEM_DESCRIPTOR + GAMEPAD_DESCRIPTOR
    }

    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private val executor = Executors.newSingleThreadExecutor()
    private var hidDevice: BluetoothHidDevice? = null
    private var connectedDevice: BluetoothDevice? = null
    private var pendingConnectDevice: BluetoothDevice? = null
    private var proxyRequested = false
    private var appRegistrationRequested = false
    private var appRegistered = false

    private val _connectionState = MutableStateFlow(ConnectionStateData(ConnectionState.DISCONNECTED))
    val connectionState: StateFlow<ConnectionStateData> = _connectionState.asStateFlow()

    private val _isHidSupported = MutableStateFlow(false)
    val isHidSupported: StateFlow<Boolean> = _isHidSupported.asStateFlow()

    private val serviceListener = object : BluetoothProfile.ServiceListener {
        override fun onServiceConnected(profile: Int, proxy: BluetoothProfile) {
            if (profile == BluetoothProfile.HID_DEVICE) {
                hidDevice = proxy as BluetoothHidDevice
                proxyRequested = false
                _isHidSupported.value = true
                registerHidApp()
            }
        }

        override fun onServiceDisconnected(profile: Int) {
            if (profile == BluetoothProfile.HID_DEVICE) {
                hidDevice = null
                connectedDevice = null
                proxyRequested = false
                appRegistrationRequested = false
                appRegistered = false
                _isHidSupported.value = false
                _connectionState.value = ConnectionStateData(ConnectionState.DISCONNECTED)
            }
        }
    }

    private val hidCallback = object : BluetoothHidDevice.Callback() {
        override fun onAppStatusChanged(pluggedDevice: BluetoothDevice?, registered: Boolean) {
            Log.d(TAG, "onAppStatusChanged registered=$registered device=$pluggedDevice")
            appRegistered = registered
            appRegistrationRequested = false
            _isHidSupported.value = registered
            if (!registered) {
                connectedDevice = null
                pendingConnectDevice = null
                _connectionState.value = ConnectionStateData(ConnectionState.HID_UNSUPPORTED, errorMessage = "Android refused HID registration. This phone/ROM may not support Bluetooth HID Device mode.")
                return
            }
            if (registered) {
                pendingConnectDevice?.let { device ->
                    pendingConnectDevice = null
                    connect(device)
                }
            }
        }

        override fun onConnectionStateChanged(device: BluetoothDevice?, state: Int) {
            Log.d(TAG, "onConnectionStateChanged device=$device state=$state")
            when (state) {
                BluetoothProfile.STATE_CONNECTED -> {
                    connectedDevice = device
                    _connectionState.value = ConnectionStateData(ConnectionState.CONNECTED, device?.let { RemoteDevice.fromBluetoothDevice(it) })
                }
                BluetoothProfile.STATE_CONNECTING -> {
                    _connectionState.value = ConnectionStateData(ConnectionState.CONNECTING, device?.let { RemoteDevice.fromBluetoothDevice(it) })
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    connectedDevice = null
                    _connectionState.value = ConnectionStateData(ConnectionState.DISCONNECTED)
                }
                BluetoothProfile.STATE_DISCONNECTING -> {
                    _connectionState.value = ConnectionStateData(ConnectionState.DISCONNECTED, device?.let { RemoteDevice.fromBluetoothDevice(it) })
                }
            }
        }

        override fun onGetReport(device: BluetoothDevice?, type: Byte, id: Byte, bufferSize: Int) {
            Log.d(TAG, "onGetReport type=$type id=$id bufferSize=$bufferSize")
        }

        override fun onSetReport(device: BluetoothDevice?, type: Byte, id: Byte, data: ByteArray?) {
            Log.d(TAG, "onSetReport type=$type id=$id size=${data?.size ?: 0}")
        }

        override fun onInterruptData(device: BluetoothDevice?, reportId: Byte, data: ByteArray?) {
            Log.d(TAG, "onInterruptData reportId=$reportId size=${data?.size ?: 0}")
        }

        override fun onVirtualCableUnplug(device: BluetoothDevice?) {
            Log.d(TAG, "onVirtualCableUnplug device=$device")
            connectedDevice = null
            _connectionState.value = ConnectionStateData(ConnectionState.DISCONNECTED)
        }
    }

    fun initialize(): Boolean {
        if (hidDevice != null || proxyRequested) return true
        if (bluetoothAdapter == null) {
            _connectionState.value = ConnectionStateData(ConnectionState.BLUETOOTH_DISABLED, errorMessage = "Bluetooth not available")
            return false
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            _connectionState.value = ConnectionStateData(ConnectionState.HID_UNSUPPORTED, errorMessage = "Bluetooth HID requires Android 9+")
            return false
        }
        proxyRequested = bluetoothAdapter.getProfileProxy(context, serviceListener, BluetoothProfile.HID_DEVICE)
        return proxyRequested
    }

    fun registerHidApp(): Boolean {
        if (appRegistered || appRegistrationRequested) return true
        val device = hidDevice ?: return false
        val sdp = BluetoothHidDeviceAppSdpSettings(
            "BluePilot Remote",
            "Bluetooth HID Remote Control",
            "BluePilot",
            BluetoothHidDevice.SUBCLASS1_COMBO,
            COMBINED_DESCRIPTOR
        )
        return try {
            val result = device.registerApp(
                sdp,
                null as BluetoothHidDeviceAppQosSettings?,
                null as BluetoothHidDeviceAppQosSettings?,
                executor,
                hidCallback
            )
            appRegistrationRequested = result
            if (!result) {
                _connectionState.value = ConnectionStateData(ConnectionState.HID_UNSUPPORTED, errorMessage = "Failed to register HID app")
            }
            result
        } catch (e: Exception) {
            Log.e(TAG, "registerHidApp failed", e)
            _connectionState.value = ConnectionStateData(ConnectionState.ERROR, errorMessage = e.message)
            false
        }
    }

    fun unregisterHidApp() {
        try {
            hidDevice?.unregisterApp()
        } catch (e: Exception) {
            Log.e(TAG, "unregisterApp failed", e)
        }
    }

    fun retryHidRegistration() {
        try {
            appRegistrationRequested = false
            appRegistered = false
            hidDevice?.unregisterApp()
        } catch (_: Exception) {
        }
        registerHidApp()
    }

    fun connect(device: BluetoothDevice): Boolean {
        return try {
            _connectionState.value = ConnectionStateData(ConnectionState.CONNECTING, RemoteDevice.fromBluetoothDevice(device))
            if (hidDevice == null) {
                pendingConnectDevice = device
                initialize()
                return true
            }
            if (!appRegistered) {
                pendingConnectDevice = device
                registerHidApp()
                return true
            }
            val started = hidDevice?.connect(device) ?: false
            if (!started) {
                pendingConnectDevice = device
                retryHidRegistration()
                true
            } else {
                true
            }
        } catch (e: Exception) {
            Log.e(TAG, "connect failed", e)
            _connectionState.value = ConnectionStateData(ConnectionState.ERROR, errorMessage = e.message)
            false
        }
    }

    fun disconnect() {
        connectedDevice?.let { hidDevice?.disconnect(it) }
    }

    fun sendKeyboardKey(keyCode: Byte, modifiers: Byte = 0) {
        sendKeyDown(keyCode, modifiers)
        sendKeyUp(keyCode)
    }

    fun sendKeyDown(keyCode: Byte, modifiers: Byte = 0) {
        val report = ByteArray(8)
        report[0] = modifiers
        report[2] = keyCode
        sendReport(REPORT_ID_KEYBOARD, report)
    }

    fun sendKeyUp(keyCode: Byte) {
        sendReport(REPORT_ID_KEYBOARD, ByteArray(8))
    }

    fun sendText(text: String) {
        text.forEach { char ->
            charToKeyStroke(char)?.let { (code, modifiers) -> sendKeyboardKey(code, modifiers) }
        }
    }

    private fun charToKeyStroke(char: Char): Pair<Byte, Byte>? = when (char) {
        'a' -> Pair(KeyboardKeyCodes.KEY_A, 0.toByte())
        'b' -> Pair(KeyboardKeyCodes.KEY_B, 0.toByte())
        'c' -> Pair(KeyboardKeyCodes.KEY_C, 0.toByte())
        'd' -> Pair(KeyboardKeyCodes.KEY_D, 0.toByte())
        'e' -> Pair(KeyboardKeyCodes.KEY_E, 0.toByte())
        'f' -> Pair(KeyboardKeyCodes.KEY_F, 0.toByte())
        'g' -> Pair(KeyboardKeyCodes.KEY_G, 0.toByte())
        'h' -> Pair(KeyboardKeyCodes.KEY_H, 0.toByte())
        'i' -> Pair(KeyboardKeyCodes.KEY_I, 0.toByte())
        'j' -> Pair(KeyboardKeyCodes.KEY_J, 0.toByte())
        'k' -> Pair(KeyboardKeyCodes.KEY_K, 0.toByte())
        'l' -> Pair(KeyboardKeyCodes.KEY_L, 0.toByte())
        'm' -> Pair(KeyboardKeyCodes.KEY_M, 0.toByte())
        'n' -> Pair(KeyboardKeyCodes.KEY_N, 0.toByte())
        'o' -> Pair(KeyboardKeyCodes.KEY_O, 0.toByte())
        'p' -> Pair(KeyboardKeyCodes.KEY_P, 0.toByte())
        'q' -> Pair(KeyboardKeyCodes.KEY_Q, 0.toByte())
        'r' -> Pair(KeyboardKeyCodes.KEY_R, 0.toByte())
        's' -> Pair(KeyboardKeyCodes.KEY_S, 0.toByte())
        't' -> Pair(KeyboardKeyCodes.KEY_T, 0.toByte())
        'u' -> Pair(KeyboardKeyCodes.KEY_U, 0.toByte())
        'v' -> Pair(KeyboardKeyCodes.KEY_V, 0.toByte())
        'w' -> Pair(KeyboardKeyCodes.KEY_W, 0.toByte())
        'x' -> Pair(KeyboardKeyCodes.KEY_X, 0.toByte())
        'y' -> Pair(KeyboardKeyCodes.KEY_Y, 0.toByte())
        'z' -> Pair(KeyboardKeyCodes.KEY_Z, 0.toByte())
        'A' -> Pair(KeyboardKeyCodes.KEY_A, 2.toByte())
        'B' -> Pair(KeyboardKeyCodes.KEY_B, 2.toByte())
        'C' -> Pair(KeyboardKeyCodes.KEY_C, 2.toByte())
        'D' -> Pair(KeyboardKeyCodes.KEY_D, 2.toByte())
        'E' -> Pair(KeyboardKeyCodes.KEY_E, 2.toByte())
        'F' -> Pair(KeyboardKeyCodes.KEY_F, 2.toByte())
        'G' -> Pair(KeyboardKeyCodes.KEY_G, 2.toByte())
        'H' -> Pair(KeyboardKeyCodes.KEY_H, 2.toByte())
        'I' -> Pair(KeyboardKeyCodes.KEY_I, 2.toByte())
        'J' -> Pair(KeyboardKeyCodes.KEY_J, 2.toByte())
        'K' -> Pair(KeyboardKeyCodes.KEY_K, 2.toByte())
        'L' -> Pair(KeyboardKeyCodes.KEY_L, 2.toByte())
        'M' -> Pair(KeyboardKeyCodes.KEY_M, 2.toByte())
        'N' -> Pair(KeyboardKeyCodes.KEY_N, 2.toByte())
        'O' -> Pair(KeyboardKeyCodes.KEY_O, 2.toByte())
        'P' -> Pair(KeyboardKeyCodes.KEY_P, 2.toByte())
        'Q' -> Pair(KeyboardKeyCodes.KEY_Q, 2.toByte())
        'R' -> Pair(KeyboardKeyCodes.KEY_R, 2.toByte())
        'S' -> Pair(KeyboardKeyCodes.KEY_S, 2.toByte())
        'T' -> Pair(KeyboardKeyCodes.KEY_T, 2.toByte())
        'U' -> Pair(KeyboardKeyCodes.KEY_U, 2.toByte())
        'V' -> Pair(KeyboardKeyCodes.KEY_V, 2.toByte())
        'W' -> Pair(KeyboardKeyCodes.KEY_W, 2.toByte())
        'X' -> Pair(KeyboardKeyCodes.KEY_X, 2.toByte())
        'Y' -> Pair(KeyboardKeyCodes.KEY_Y, 2.toByte())
        'Z' -> Pair(KeyboardKeyCodes.KEY_Z, 2.toByte())
        '0' -> Pair(KeyboardKeyCodes.KEY_0, 0.toByte())
        '1' -> Pair(KeyboardKeyCodes.KEY_1, 0.toByte())
        '2' -> Pair(KeyboardKeyCodes.KEY_2, 0.toByte())
        '3' -> Pair(KeyboardKeyCodes.KEY_3, 0.toByte())
        '4' -> Pair(KeyboardKeyCodes.KEY_4, 0.toByte())
        '5' -> Pair(KeyboardKeyCodes.KEY_5, 0.toByte())
        '6' -> Pair(KeyboardKeyCodes.KEY_6, 0.toByte())
        '7' -> Pair(KeyboardKeyCodes.KEY_7, 0.toByte())
        '8' -> Pair(KeyboardKeyCodes.KEY_8, 0.toByte())
        '9' -> Pair(KeyboardKeyCodes.KEY_9, 0.toByte())
        '!' -> Pair(KeyboardKeyCodes.KEY_1, 2.toByte())
        '@' -> Pair(KeyboardKeyCodes.KEY_2, 2.toByte())
        '#' -> Pair(KeyboardKeyCodes.KEY_3, 2.toByte())
        '$' -> Pair(KeyboardKeyCodes.KEY_4, 2.toByte())
        '%' -> Pair(KeyboardKeyCodes.KEY_5, 2.toByte())
        '^' -> Pair(KeyboardKeyCodes.KEY_6, 2.toByte())
        '&' -> Pair(KeyboardKeyCodes.KEY_7, 2.toByte())
        '*' -> Pair(KeyboardKeyCodes.KEY_8, 2.toByte())
        '(' -> Pair(KeyboardKeyCodes.KEY_9, 2.toByte())
        ')' -> Pair(KeyboardKeyCodes.KEY_0, 2.toByte())
        ' ' -> Pair(KeyboardKeyCodes.KEY_SPACE, 0.toByte())
        '\n' -> Pair(KeyboardKeyCodes.KEY_ENTER, 0.toByte())
        '\t' -> Pair(KeyboardKeyCodes.KEY_TAB, 0.toByte())
        '-' -> Pair(KeyboardKeyCodes.KEY_MINUS, 0.toByte())
        '_' -> Pair(KeyboardKeyCodes.KEY_MINUS, 2.toByte())
        '=' -> Pair(KeyboardKeyCodes.KEY_EQUAL, 0.toByte())
        '+' -> Pair(KeyboardKeyCodes.KEY_EQUAL, 2.toByte())
        '.' -> Pair(KeyboardKeyCodes.KEY_PERIOD, 0.toByte())
        ',' -> Pair(KeyboardKeyCodes.KEY_COMMA, 0.toByte())
        '/' -> Pair(KeyboardKeyCodes.KEY_SLASH, 0.toByte())
        '?' -> Pair(KeyboardKeyCodes.KEY_SLASH, 2.toByte())
        else -> null
    }

    fun sendMouseMove(dx: Byte, dy: Byte) {
        sendReport(REPORT_ID_MOUSE, byteArrayOf(0, dx, dy, 0))
    }

    fun sendMouseClick(button: MouseButton) {
        sendMouseButtonDown(button)
        sendMouseButtonUp(button)
    }

    fun sendMouseButtonDown(button: MouseButton) {
        sendReport(REPORT_ID_MOUSE, byteArrayOf(button.value, 0, 0, 0))
    }

    fun sendMouseButtonUp(button: MouseButton) {
        sendReport(REPORT_ID_MOUSE, byteArrayOf(0, 0, 0, 0.toByte()))
    }

    fun sendMouseScroll(amount: Byte) {
        sendReport(REPORT_ID_MOUSE, byteArrayOf(0, 0, 0, amount))
    }

    fun sendConsumerControl(code: Short) {
        val report = byteArrayOf((code.toInt() and 0xFF).toByte(), ((code.toInt() shr 8) and 0xFF).toByte())
        sendReport(REPORT_ID_CONSUMER, report)
        sendReport(REPORT_ID_CONSUMER, byteArrayOf(0, 0))
    }

    fun sendSystemControl(code: Byte) {
        val mask = when (code.toInt()) {
            0x01 -> 0x01 // System Power Down
            0x02 -> 0x02 // System Sleep
            0x03 -> 0x04 // System Wake Up
            else -> 0x00
        }.toByte()
        sendReport(REPORT_ID_SYSTEM, byteArrayOf(mask))
        sendReport(REPORT_ID_SYSTEM, byteArrayOf(0))
    }

    fun sendGamepadReport(gamepadState: GamepadState) {
        val report = ByteArray(7)
        val buttons = gamepadState.pressedButtons
        report[0] = (buttons and 0xFF).toByte()
        report[1] = ((buttons shr 8) and 0xFF).toByte()
        report[2] = gamepadState.dpadDirection.value
        report[3] = gamepadState.leftStickX
        report[4] = gamepadState.leftStickY
        report[5] = gamepadState.rightStickX
        report[6] = gamepadState.rightStickY
        sendReport(REPORT_ID_GAMEPAD, report)
    }

    private fun sendReport(id: Int, data: ByteArray) {
        val device = connectedDevice ?: return
        try {
            hidDevice?.sendReport(device, id, data)
        } catch (e: Exception) {
            Log.e(TAG, "sendReport failed", e)
        }
    }

    fun isHidSupported(): Boolean = _isHidSupported.value

    fun openBluetoothSettings() {
        val intent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            context.startActivity(Intent(Settings.ACTION_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }
    }

    fun currentConnectionState(): ConnectionStateData = _connectionState.value

    fun cleanup() {
        unregisterHidApp()
        bluetoothAdapter?.closeProfileProxy(BluetoothProfile.HID_DEVICE, hidDevice)
        hidDevice = null
        connectedDevice = null
        pendingConnectDevice = null
        proxyRequested = false
        appRegistrationRequested = false
        appRegistered = false
        _isHidSupported.value = false
        _connectionState.value = ConnectionStateData(ConnectionState.DISCONNECTED)
    }
}
