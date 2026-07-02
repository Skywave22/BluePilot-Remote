package com.bluepilot.remote.model

/**
 * Represents the current Bluetooth HID connection state
 */
enum class ConnectionState {
    DISCONNECTED,
    PAIRING,
    CONNECTING,
    CONNECTED,
    BLUETOOTH_DISABLED,
    PERMISSION_MISSING,
    HID_UNSUPPORTED,
    ERROR
}

/**
 * Data class representing a remote Bluetooth device
 */
data class RemoteDevice(
    val address: String,
    val name: String,
    val isConnected: Boolean = false,
    val isPaired: Boolean = false,
    val lastConnected: Long = 0L
) {
    companion object {
        fun fromBluetoothDevice(device: android.bluetooth.BluetoothDevice): RemoteDevice {
            return RemoteDevice(
                address = device.address,
                name = device.name ?: "Unknown Device",
                isConnected = false,
                isPaired = device.bondState == android.bluetooth.BluetoothDevice.BOND_BONDED
            )
        }
    }
}

/**
 * Connection state with additional metadata
 */
data class ConnectionStateData(
    val state: ConnectionState,
    val device: RemoteDevice? = null,
    val errorMessage: String? = null
)
