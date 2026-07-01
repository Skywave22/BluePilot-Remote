package com.bluepilot.remote.viewmodel

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bluepilot.remote.bluetooth.BluetoothHidManager
import com.bluepilot.remote.model.RemoteDevice
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Real connection-state ViewModel for the new connection screen.
 * Handles classic Bluetooth discovery, paired-device listing, discoverability state, and HID connect calls.
 */
@HiltViewModel
class ConnectionViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val hidManager: BluetoothHidManager
) : ViewModel() {

    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private var receiverRegistered = false

    private val _uiState = MutableStateFlow(ConnectionUiState())
    val uiState: StateFlow<ConnectionUiState> = _uiState.asStateFlow()

    private val discoveryReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    val device: BluetoothDevice? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE, BluetoothDevice::class.java)
                    } else {
                        @Suppress("DEPRECATION")
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    }
                    device?.let { addDevice(it, paired = false) }
                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    _uiState.value = _uiState.value.copy(
                        isScanning = false,
                        statusMessage = if (_uiState.value.devices.isEmpty()) {
                            "No devices found. Make sure the other device is discoverable/pairing."
                        } else {
                            "Scan finished. Tap a device to connect."
                        }
                    )
                }
            }
        }
    }

    init {
        loadPairedDevices()
        hidManager.initialize()
    }

    fun startScan() {
        val adapter = bluetoothAdapter
        if (adapter == null) {
            _uiState.value = _uiState.value.copy(statusMessage = "Bluetooth is not available on this phone.")
            return
        }
        if (!adapter.isEnabled) {
            _uiState.value = _uiState.value.copy(statusMessage = "Bluetooth is off. Turn on Bluetooth first.")
            return
        }
        if (!hasBluetoothPermissions()) {
            _uiState.value = _uiState.value.copy(statusMessage = "Bluetooth permissions are missing. Open permissions/settings and allow Bluetooth.")
            return
        }

        try {
            hidManager.initialize()
            registerReceiverIfNeeded()
            loadPairedDevices()
            if (adapter.isDiscovering) adapter.cancelDiscovery()
            val started = adapter.startDiscovery()
            _uiState.value = _uiState.value.copy(
                isScanning = started,
                statusMessage = if (started) "Searching for nearby Bluetooth devices..." else "Could not start Bluetooth scan. Try again."
            )
        } catch (e: SecurityException) {
            _uiState.value = _uiState.value.copy(isScanning = false, statusMessage = "Bluetooth permission denied: ${e.message}")
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(isScanning = false, statusMessage = "Scan error: ${e.message}")
        }
    }

    fun stopScan() {
        try {
            bluetoothAdapter?.takeIf { it.isDiscovering }?.cancelDiscovery()
        } catch (_: SecurityException) {
        }
        _uiState.value = _uiState.value.copy(isScanning = false, statusMessage = "Search cancelled.")
    }

    fun markDiscoverabilityRequested() {
        _uiState.value = _uiState.value.copy(
            isDiscoverable = true,
            statusMessage = "Discoverability request opened. Confirm it in Android system dialog."
        )
    }

    fun connectToAddress(address: String) {
        val adapter = bluetoothAdapter
        if (adapter == null) {
            _uiState.value = _uiState.value.copy(statusMessage = "Bluetooth is not available.")
            return
        }
        if (!BluetoothAdapter.checkBluetoothAddress(address)) {
            _uiState.value = _uiState.value.copy(statusMessage = "Invalid Bluetooth address. Use format XX:XX:XX:XX:XX:XX")
            return
        }
        try {
            val device = adapter.getRemoteDevice(address)
            connect(device)
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(statusMessage = "Could not use this address: ${e.message}")
        }
    }

    fun connectToDevice(device: RemoteDevice) {
        connectToAddress(device.address)
    }

    @SuppressLint("MissingPermission")
    private fun connect(device: BluetoothDevice) {
        viewModelScope.launch {
            try {
                bluetoothAdapter?.takeIf { it.isDiscovering }?.cancelDiscovery()
                _uiState.value = _uiState.value.copy(
                    isScanning = false,
                    statusMessage = "Connecting to ${safeName(device)}..."
                )
                hidManager.initialize()
                hidManager.registerHidApp()
                val started = hidManager.connect(device)
                _uiState.value = _uiState.value.copy(
                    statusMessage = if (started) {
                        "Connection request sent to ${safeName(device)}. If it does not connect, pair the device in Android Bluetooth settings first."
                    } else {
                        "Connection could not start. Android HID mode may require the other device to initiate pairing/connection."
                    }
                )
            } catch (e: SecurityException) {
                _uiState.value = _uiState.value.copy(statusMessage = "Bluetooth permission denied: ${e.message}")
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(statusMessage = "Connection error: ${e.message}")
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun loadPairedDevices() {
        val adapter = bluetoothAdapter ?: return
        if (!hasBluetoothPermissions()) return
        try {
            val paired = adapter.bondedDevices.orEmpty().map { it.toRemoteDeviceSafe(paired = true) }
            _uiState.value = _uiState.value.copy(
                devices = mergeDevices(paired, _uiState.value.devices),
                statusMessage = if (paired.isNotEmpty()) "Paired devices loaded. You can scan for more." else _uiState.value.statusMessage
            )
        } catch (_: SecurityException) {
        }
    }

    private fun addDevice(device: BluetoothDevice, paired: Boolean) {
        val remote = device.toRemoteDeviceSafe(paired)
        _uiState.value = _uiState.value.copy(
            devices = mergeDevices(_uiState.value.devices, listOf(remote)),
            statusMessage = "Found ${remote.name}"
        )
    }

    @SuppressLint("MissingPermission")
    private fun BluetoothDevice.toRemoteDeviceSafe(paired: Boolean): RemoteDevice = RemoteDevice(
        address = address ?: "",
        name = safeName(this),
        isPaired = paired || bondState == BluetoothDevice.BOND_BONDED
    )

    @SuppressLint("MissingPermission")
    private fun safeName(device: BluetoothDevice): String = runCatching {
        device.name ?: "Unknown device"
    }.getOrDefault("Unknown device")

    private fun mergeDevices(a: List<RemoteDevice>, b: List<RemoteDevice>): List<RemoteDevice> =
        (a + b).filter { it.address.isNotBlank() }.distinctBy { it.address }.sortedWith(
            compareByDescending<RemoteDevice> { it.isPaired }.thenBy { it.name.lowercase() }
        )

    private fun registerReceiverIfNeeded() {
        if (receiverRegistered) return
        val filter = IntentFilter().apply {
            addAction(BluetoothDevice.ACTION_FOUND)
            addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(discoveryReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            context.registerReceiver(discoveryReceiver, filter)
        }
        receiverRegistered = true
    }

    private fun hasBluetoothPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        }
    }

    override fun onCleared() {
        stopScan()
        if (receiverRegistered) {
            runCatching { context.unregisterReceiver(discoveryReceiver) }
            receiverRegistered = false
        }
        super.onCleared()
    }
}

data class ConnectionUiState(
    val isScanning: Boolean = false,
    val isDiscoverable: Boolean = false,
    val statusMessage: String = "Ready. Search, discover, or connect by address.",
    val devices: List<RemoteDevice> = emptyList()
)
