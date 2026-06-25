package com.bluepilot.remote.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothHidDevice
import android.bluetooth.BluetoothHidDeviceAppConfiguration
import android.bluetooth.BluetoothHidDeviceCallback
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.os.Build
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.bluepilot.remote.model.*
import java.util.UUID

/**
 * Manages Bluetooth HID device operations
 * Handles registration, connection, and HID report sending
 */
class BluetoothHidManager(private val context: Context) {

    companion object {
        private const val TAG = "BluetoothHidManager"
        
        // HID Descriptor for Keyboard
        private val KEYBOARD_DESCRIPTOR = byteArrayOf(
            0x05.toByte(), 0x01.toByte(), 0x09.toByte(), 0x06.toByte(), 0xA1.toByte(), 0x01.toByte(),
            0x05.toByte(), 0x07.toByte(), 0x19.toByte(), 0xE0.toByte(), 0x29.toByte(), 0xE7.toByte(),
            0x15.toByte(), 0x00.toByte(), 0x25.toByte(), 0x01.toByte(), 0x75.toByte(), 0x01.toByte(),
            0x95.toByte(), 0x08.toByte(), 0x81.toByte(), 0x02.toByte(), 0x95.toByte(), 0x01.toByte(),
            0x75.toByte(), 0x08.toByte(), 0x81.toByte(), 0x01.toByte(), 0x95.toByte(), 0x05.toByte(),
            0x75.toByte(), 0x01.toByte(), 0x05.toByte(), 0x08.toByte(), 0x19.toByte(), 0x01.toByte(),
            0x29.toByte(), 0x05.toByte(), 0x91.toByte(), 0x02.toByte(), 0x95.toByte(), 0x01.toByte(),
            0x75.toByte(), 0x03.toByte(), 0x91.toByte(), 0x03.toByte(), 0x95.toByte(), 0x06.toByte(),
            0x75.toByte(), 0x08.toByte(), 0x15.toByte(), 0x00.toByte(), 0x25.toByte(), 0x65.toByte(),
            0x05.toByte(), 0x07.toByte(), 0x19.toByte(), 0x00.toByte(), 0x29.toByte(), 0x65.toByte(),
            0x81.toByte(), 0x00.toByte(), 0xC0.toByte()
        )

        // HID Descriptor for Mouse
        private val MOUSE_DESCRIPTOR = byteArrayOf(
            0x05.toByte(), 0x01.toByte(), 0x09.toByte(), 0x02.toByte(), 0xA1.toByte(), 0x01.toByte(),
            0x09.toByte(), 0x01.toByte(), 0xA1.toByte(), 0x00.toByte(), 0x05.toByte(), 0x09.toByte(),
            0x19.toByte(), 0x01.toByte(), 0x29.toByte(), 0x03.toByte(), 0x15.toByte(), 0x00.toByte(),
            0x25.toByte(), 0x01.toByte(), 0x95.toByte(), 0x03.toByte(), 0x75.toByte(), 0x01.toByte(),
            0x81.toByte(), 0x02.toByte(), 0x95.toByte(), 0x01.toByte(), 0x75.toByte(), 0x05.toByte(),
            0x81.toByte(), 0x03.toByte(), 0x05.toByte(), 0x01.toByte(), 0x09.toByte(), 0x30.toByte(),
            0x09.toByte(), 0x31.toByte(), 0x15.toByte(), 0x81.toByte(), 0x25.toByte(), 0x7F.toByte(),
            0x75.toByte(), 0x08.toByte(), 0x95.toByte(), 0x02.toByte(), 0x81.toByte(), 0x06.toByte(),
            0x09.toByte(), 0x38.toByte(), 0x15.toByte(), 0x81.toByte(), 0x25.toByte(), 0x07.toByte(),
            0x75.toByte(), 0x08.toByte(), 0x95.toByte(), 0x01.toByte(), 0x81.toByte(), 0x06.toByte(),
            0xC0.toByte(), 0xC0.toByte()
        )

        // HID Descriptor for Consumer/Media Controls
        private val CONSUMER_DESCRIPTOR = byteArrayOf(
            0x05.toByte(), 0x0C.toByte(), 0x09.toByte(), 0x01.toByte(), 0xA1.toByte(), 0x01.toByte(),
            0x85.toByte(), 0x01.toByte(), 0x15.toByte(), 0x00.toByte(), 0x25.toByte(), 0x01.toByte(),
            0x75.toByte(), 0x01.toByte(), 0x95.toByte(), 0x01.toByte(), 0x0A.toByte(), 0x83.toByte(),
            0x01.toByte(), 0x81.toByte(), 0x02.toByte(), 0x0A.toByte(), 0x82.toByte(), 0x01.toByte(),
            0x81.toByte(), 0x02.toByte(), 0x0A.toByte(), 0x94.toByte(), 0x01.toByte(), 0x81.toByte(),
            0x02.toByte(), 0x0A.toByte(), 0xB5.toByte(), 0x01.toByte(), 0x81.toByte(), 0x02.toByte(),
            0x0A.toByte(), 0xB6.toByte(), 0x01.toByte(), 0x81.toByte(), 0x02.toByte(), 0x0A.toByte(),
            0xCD.toByte(), 0x01.toByte(), 0x81.toByte(), 0x02.toByte(), 0x0A.toByte(), 0xE2.toByte(),
            0x01.toByte(), 0x81.toByte(), 0x02.toByte(), 0x0A.toByte(), 0xE9.toByte(), 0x01.toByte(),
            0x81.toByte(), 0x02.toByte(), 0x0A.toByte(), 0xEA.toByte(), 0x01.toByte(), 0x81.toByte(),
            0x02.toByte(), 0xC0.toByte()
        )

        // HID Descriptor for Gamepad (XInput-style)
        private val GAMEPAD_DESCRIPTOR = byteArrayOf(
            0x05.toByte(), 0x01.toByte(), 0x09.toByte(), 0x05.toByte(), 0xA1.toByte(), 0x01.toByte(),
            0x05.toByte(), 0x09.toByte(), 0x19.toByte(), 0x01.toByte(), 0x29.toByte(), 0x10.toByte(),
            0x15.toByte(), 0x00.toByte(), 0x25.toByte(), 0x01.toByte(), 0x95.toByte(), 0x10.toByte(),
            0x75.toByte(), 0x01.toByte(), 0x81.toByte(), 0x02.toByte(), 0x05.toByte(), 0x01.toByte(),
            0x09.toByte(), 0x30.toByte(), 0x09.toByte(), 0x31.toByte(), 0x15.toByte(), 0x00.toByte(),
            0x25.toByte(), 0x0F.toByte(), 0x75.toByte(), 0x04.toByte(), 0x95.toByte(), 0x02.toByte(),
            0x81.toByte(), 0x02.toByte(), 0xC0.toByte()
        )
    }

    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private var hidDevice: BluetoothHidDevice? = null
    private var appConfig: BluetoothHidDeviceAppConfiguration? = null
    private var connectedDevice: BluetoothDevice? = null

    private val _connectionState = MutableStateFlow<ConnectionStateData>(
        ConnectionStateData(ConnectionState.DISCONNECTED)
    )
    val connectionState: StateFlow<ConnectionStateData> = _connectionState.asStateFlow()

    private val _isHidSupported = MutableStateFlow(false)
    val isHidSupported: StateFlow<Boolean> = _isHidSupported.asStateFlow()

    private val hidCallback = object : BluetoothHidDeviceCallback() {
        override fun onAppStatusChanged(pluggedDevice: BluetoothDevice?, registered: Boolean) {
            Log.d(TAG, "onAppStatusChanged: device=$pluggedDevice, registered=$registered")
            if (registered) {
                _isHidSupported.value = true
            }
        }

        override fun onConnectionStateChanged(device: BluetoothDevice?, state: Int) {
            Log.d(TAG, "onConnectionStateChanged: device=$device, state=$state")
            when (state) {
                BluetoothProfile.STATE_CONNECTED -> {
                    connectedDevice = device
                    _connectionState.value = ConnectionStateData(
                        ConnectionState.CONNECTED,
                        device?.let { RemoteDevice.fromBluetoothDevice(it) }
                    )
                }
                BluetoothProfile.STATE_CONNECTING -> {
                    _connectionState.value = ConnectionStateData(
                        ConnectionState.CONNECTING,
                        device?.let { RemoteDevice.fromBluetoothDevice(it) }
                    )
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    connectedDevice = null
                    _connectionState.value = ConnectionStateData(ConnectionState.DISCONNECTED)
                }
                BluetoothProfile.STATE_DISCONNECTING -> {
                    _connectionState.value = ConnectionStateData(
                        ConnectionState.DISCONNECTED,
                        device?.let { RemoteDevice.fromBluetoothDevice(it) }
                    )
                }
            }
        }

        override fun onGetReport(device: BluetoothDevice?, type: Byte, id: Byte, bufferSize: Int) {
            Log.d(TAG, "onGetReport: device=$device, type=$type, id=$id")
        }

        override fun onSetReport(device: BluetoothDevice?, type: Byte, id: Byte, data: ByteArray?) {
            Log.d(TAG, "onSetReport: device=$device, type=$type, id=$id")
        }

        override fun onInterruptData(device: BluetoothDevice?, reportId: Byte, data: ByteArray?) {
            Log.d(TAG, "onInterruptData: device=$device, reportId=$reportId")
        }

        override fun onVirtualUnplug(device: BluetoothDevice?, status: Int) {
            Log.d(TAG, "onVirtualUnplug: device=$device, status=$status")
            connectedDevice = null
            _connectionState.value = ConnectionStateData(ConnectionState.DISCONNECTED)
        }
    }

    /**
     * Initialize the HID manager
     */
    fun initialize(): Boolean {
        if (bluetoothAdapter == null) {
            _connectionState.value = ConnectionStateData(
                ConnectionState.BLUETOOTH_DISABLED,
                errorMessage = "Bluetooth not available"
            )
            return false
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            _connectionState.value = ConnectionStateData(
                ConnectionState.HID_UNSUPPORTED,
                errorMessage = "Bluetooth HID requires Android 9+"
            )
            return false
        }

        hidDevice = bluetoothAdapter.getProfileProxy(context, hidCallback, BluetoothProfile.HID_DEVICE)
        return hidDevice != null
    }

    /**
     * Register HID application with descriptors
     */
    fun registerHidApp(): Boolean {
        if (hidDevice == null) {
            Log.e(TAG, "HID device not initialized")
            return false
        }

        val name = "BluePilot Remote"
        val description = "Bluetooth HID Remote Control"
        val provider = "BluePilot"
        val subclass = 0xC0 // Keyboard/Mouse combo

        try {
            appConfig = BluetoothHidDeviceAppConfiguration(
                name,
                description,
                provider,
                subclass,
                listOf(KEYBOARD_DESCRIPTOR, MOUSE_DESCRIPTOR, CONSUMER_DESCRIPTOR, GAMEPAD_DESCRIPTOR)
            )

            val result = hidDevice?.registerApp(
                appConfig!!,
                false, // Needs to be false for security
                { runnable -> runnable.run() }
            )

            if (result == true) {
                Log.d(TAG, "HID app registered successfully")
                return true
            } else {
                Log.e(TAG, "Failed to register HID app")
                _connectionState.value = ConnectionStateData(
                    ConnectionState.HID_UNSUPPORTED,
                    errorMessage = "Failed to register HID app"
                )
                return false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error registering HID app", e)
            _connectionState.value = ConnectionStateData(
                ConnectionState.ERROR,
                errorMessage = e.message
            )
            return false
        }
    }

    /**
     * Unregister HID application
     */
    fun unregisterHidApp() {
        appConfig?.let {
            hidDevice?.unregisterApp(it)
            appConfig = null
        }
    }

    /**
     * Connect to a Bluetooth device
     */
    @SuppressLint("MissingPermission")
    fun connect(device: BluetoothDevice): Boolean {
        if (hidDevice == null || appConfig == null) {
            Log.e(TAG, "HID device or app config not initialized")
            return false
        }

        return try {
            _connectionState.value = ConnectionStateData(
                ConnectionState.CONNECTING,
                RemoteDevice.fromBluetoothDevice(device)
            )
            hidDevice?.connect(device, appConfig!!) ?: false
        } catch (e: Exception) {
            Log.e(TAG, "Error connecting to device", e)
            _connectionState.value = ConnectionStateData(
                ConnectionState.ERROR,
                errorMessage = e.message
            )
            false
        }
    }

    /**
     * Disconnect from current device
     */
    fun disconnect() {
        connectedDevice?.let {
            hidDevice?.disconnect(it)
        }
    }

    /**
     * Send keyboard key press
     */
    fun sendKeyboardKey(keyCode: Byte, modifiers: Byte = 0) {
        sendKeyDown(keyCode, modifiers)
        sendKeyUp(keyCode)
    }

    /**
     * Send key down event
     */
    fun sendKeyDown(keyCode: Byte, modifiers: Byte = 0) {
        val report = ByteArray(8)
        report[0] = modifiers
        report[2] = keyCode
        sendReport(1, report)
    }

    /**
     * Send key up event
     */
    fun sendKeyUp(keyCode: Byte) {
        val report = ByteArray(8)
        report[0] = 0
        sendReport(1, report)
    }

    /**
     * Send text as keyboard input
     */
    fun sendText(text: String) {
        text.forEach { char ->
            val keyCode = charToKeyCode(char)
            if (keyCode != null) {
                sendKeyboardKey(keyCode)
            }
        }
    }

    /**
     * Convert character to HID key code
     */
    private fun charToKeyCode(char: Char): Byte? {
        return when (char) {
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
    }

    /**
     * Send mouse movement
     */
    fun sendMouseMove(dx: Byte, dy: Byte) {
        val report = ByteArray(4)
        report[0] = 0
        report[1] = dx
        report[2] = dy
        report[3] = 0
        sendReport(2, report)
    }

    /**
     * Send mouse click
     */
    fun sendMouseClick(button: MouseButton) {
        sendMouseButtonDown(button)
        sendMouseButtonUp(button)
    }

    /**
     * Send mouse button down
     */
    fun sendMouseButtonDown(button: MouseButton) {
        val report = ByteArray(4)
        report[0] = button.value
        sendReport(2, report)
    }

    /**
     * Send mouse button up
     */
    fun sendMouseButtonUp(button: MouseButton) {
        val report = ByteArray(4)
        report[0] = 0
        sendReport(2, report)
    }

    /**
     * Send mouse scroll
     */
    fun sendMouseScroll(amount: Byte) {
        val report = ByteArray(4)
        report[0] = 0
        report[1] = 0
        report[2] = 0
        report[3] = amount
        sendReport(2, report)
    }

    /**
     * Send consumer control (media keys)
     */
    fun sendConsumerControl(code: Short) {
        val report = ByteArray(2)
        report[0] = (code.toInt() and 0xFF).toByte()
        report[1] = ((code.toInt() shr 8) and 0xFF).toByte()
        sendReport(3, report)
    }

    /**
     * Send system control (power, sleep)
     */
    fun sendSystemControl(code: Byte) {
        val report = ByteArray(1)
        report[0] = code
        sendReport(4, report)
    }

    /**
     * Send gamepad report
     */
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
        sendReport(5, report)
    }

    /**
     * Send HID report
     */
    private fun sendReport(id: Int, data: ByteArray) {
        connectedDevice?.let { device ->
            try {
                hidDevice?.setReport(device, BluetoothHidDevice.OUTPUT_REPORT, id.toByte(), data)
            } catch (e: Exception) {
                Log.e(TAG, "Error sending report", e)
            }
        }
    }

    /**
     * Check if HID is supported on this device
     */
    fun isHidSupported(): Boolean {
        return _isHidSupported.value
    }

    /**
     * Get current connection state
     */
    fun currentConnectionState(): ConnectionStateData {
        return _connectionState.value
    }

    /**
     * Cleanup resources
     */
    fun cleanup() {
        unregisterHidApp()
        bluetoothAdapter?.closeProfileProxy(BluetoothProfile.HID_DEVICE, hidDevice)
        hidDevice = null
    }
}
