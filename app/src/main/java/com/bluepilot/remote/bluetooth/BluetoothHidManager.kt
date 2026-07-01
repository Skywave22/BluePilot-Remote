package com.bluepilot.remote.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothHidDevice
import android.bluetooth.BluetoothHidDeviceAppQosSettings
import android.bluetooth.BluetoothHidDeviceAppSdpSettings
import android.bluetooth.BluetoothProfile
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

/**
 * Compile-safe Bluetooth HID manager using Android's public BluetoothHidDevice API.
 *
 * Important: Android exposes the HID app configuration as an opaque object returned in
 * Callback.onAppStatusChanged(), not as a public constructor. This class stores that object
 * and uses sendReport()/connect()/unregisterApp() with the real Android signatures.
 */
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

        private val GAMEPAD_DESCRIPTOR = byteArrayOf(
            0x05, 0x01, 0x09, 0x05, 0xA1.toByte(), 0x01, 0x85.toByte(), REPORT_ID_GAMEPAD.toByte(),
            0x05, 0x09, 0x19, 0x01, 0x29, 0x10, 0x15, 0x00,
            0x25, 0x01, 0x95.toByte(), 0x10, 0x75, 0x01, 0x81.toByte(), 0x02,
            0x05, 0x01, 0x09, 0x30, 0x09, 0x31, 0x09, 0x32,
            0x09, 0x35, 0x15, 0x00, 0x26, 0xFF.toByte(), 0x00, 0x75, 0x08,
            0x95.toByte(), 0x04, 0x81.toByte(), 0x02, 0xC0.toByte()
        )

        private val COMBINED_DESCRIPTOR: ByteArray =
            KEYBOARD_DESCRIPTOR + MOUSE_DESCRIPTOR + CONSUMER_DESCRIPTOR + GAMEPAD_DESCRIPTOR
    }

    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private val executor = Executors.newSingleThreadExecutor()
    private var hidDevice: BluetoothHidDevice? = null
    private var connectedDevice: BluetoothDevice? = null

    private val _connectionState = MutableStateFlow(ConnectionStateData(ConnectionState.DISCONNECTED))
    val connectionState: StateFlow<ConnectionStateData> = _connectionState.asStateFlow()

    private val _isHidSupported = MutableStateFlow(false)
    val isHidSupported: StateFlow<Boolean> = _isHidSupported.asStateFlow()

    private val serviceListener = object : BluetoothProfile.ServiceListener {
        override fun onServiceConnected(profile: Int, proxy: BluetoothProfile) {
            if (profile == BluetoothProfile.HID_DEVICE) {
                hidDevice = proxy as BluetoothHidDevice
                _isHidSupported.value = true
                registerHidApp()
            }
        }

        override fun onServiceDisconnected(profile: Int) {
            if (profile == BluetoothProfile.HID_DEVICE) {
                hidDevice = null
                connectedDevice = null
                _isHidSupported.value = false
                _connectionState.value = ConnectionStateData(ConnectionState.DISCONNECTED)
            }
        }
    }

    private val hidCallback = object : BluetoothHidDevice.Callback() {
        override fun onAppStatusChanged(pluggedDevice: BluetoothDevice?, registered: Boolean) {
            Log.d(TAG, "onAppStatusChanged registered=$registered device=$pluggedDevice")
            _isHidSupported.value = registered
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
        if (bluetoothAdapter == null) {
            _connectionState.value = ConnectionStateData(ConnectionState.BLUETOOTH_DISABLED, errorMessage = "Bluetooth not available")
            return false
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            _connectionState.value = ConnectionStateData(ConnectionState.HID_UNSUPPORTED, errorMessage = "Bluetooth HID requires Android 9+")
            return false
        }
        return bluetoothAdapter.getProfileProxy(context, serviceListener, BluetoothProfile.HID_DEVICE)
    }

    fun registerHidApp(): Boolean {
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

    fun connect(device: BluetoothDevice): Boolean {
        return try {
            _connectionState.value = ConnectionStateData(ConnectionState.CONNECTING, RemoteDevice.fromBluetoothDevice(device))
            hidDevice?.connect(device) ?: false
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
        text.forEach { char -> charToKeyCode(char)?.let { sendKeyboardKey(it) } }
    }

    private fun charToKeyCode(char: Char): Byte? = when (char) {
        'a', 'A' -> KeyboardKeyCodes.KEY_A
        'b', 'B' -> KeyboardKeyCodes.KEY_B
        'c', 'C' -> KeyboardKeyCodes.KEY_C
        'd', 'D' -> KeyboardKeyCodes.KEY_D
        'e', 'E' -> KeyboardKeyCodes.KEY_E
        'f', 'F' -> KeyboardKeyCodes.KEY_F
        'g', 'G' -> KeyboardKeyCodes.KEY_G
        'h', 'H' -> KeyboardKeyCodes.KEY_H
        'i', 'I' -> KeyboardKeyCodes.KEY_I
        'j', 'J' -> KeyboardKeyCodes.KEY_J
        'k', 'K' -> KeyboardKeyCodes.KEY_K
        'l', 'L' -> KeyboardKeyCodes.KEY_L
        'm', 'M' -> KeyboardKeyCodes.KEY_M
        'n', 'N' -> KeyboardKeyCodes.KEY_N
        'o', 'O' -> KeyboardKeyCodes.KEY_O
        'p', 'P' -> KeyboardKeyCodes.KEY_P
        'q', 'Q' -> KeyboardKeyCodes.KEY_Q
        'r', 'R' -> KeyboardKeyCodes.KEY_R
        's', 'S' -> KeyboardKeyCodes.KEY_S
        't', 'T' -> KeyboardKeyCodes.KEY_T
        'u', 'U' -> KeyboardKeyCodes.KEY_U
        'v', 'V' -> KeyboardKeyCodes.KEY_V
        'w', 'W' -> KeyboardKeyCodes.KEY_W
        'x', 'X' -> KeyboardKeyCodes.KEY_X
        'y', 'Y' -> KeyboardKeyCodes.KEY_Y
        'z', 'Z' -> KeyboardKeyCodes.KEY_Z
        '0' -> KeyboardKeyCodes.KEY_0
        '1' -> KeyboardKeyCodes.KEY_1
        '2' -> KeyboardKeyCodes.KEY_2
        '3' -> KeyboardKeyCodes.KEY_3
        '4' -> KeyboardKeyCodes.KEY_4
        '5' -> KeyboardKeyCodes.KEY_5
        '6' -> KeyboardKeyCodes.KEY_6
        '7' -> KeyboardKeyCodes.KEY_7
        '8' -> KeyboardKeyCodes.KEY_8
        '9' -> KeyboardKeyCodes.KEY_9
        ' ' -> KeyboardKeyCodes.KEY_SPACE
        '\n' -> KeyboardKeyCodes.KEY_ENTER
        '\t' -> KeyboardKeyCodes.KEY_TAB
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
        sendReport(REPORT_ID_MOUSE, byteArrayOf(0, 0, 0, 0))
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
        sendReport(REPORT_ID_SYSTEM, byteArrayOf(code))
        sendReport(REPORT_ID_SYSTEM, byteArrayOf(0))
    }

    fun sendGamepadReport(gamepadState: GamepadState) {
        val report = ByteArray(8)
        report[0] = gamepadState.dpadDirection.value
        report[1] = gamepadState.pressedButtons.toByte()
        report[2] = gamepadState.leftStickX
        report[3] = gamepadState.leftStickY
        report[4] = gamepadState.rightStickX
        report[5] = gamepadState.rightStickY
        report[6] = gamepadState.leftTrigger
        report[7] = gamepadState.rightTrigger
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

    fun currentConnectionState(): ConnectionStateData = _connectionState.value

    fun cleanup() {
        unregisterHidApp()
        bluetoothAdapter?.closeProfileProxy(BluetoothProfile.HID_DEVICE, hidDevice)
        hidDevice = null
        executor.shutdownNow()
    }
}
