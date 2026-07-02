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
import com.bluepilot.remote.model.ConnectionState
import com.bluepilot.remote.model.RemoteDevice
import com.bluepilot.remote.service.HidService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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
    private var pendingPairConnectAddress: String? = null

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
                BluetoothDevice.ACTION_BOND_STATE_CHANGED -> {
                    val device: BluetoothDevice? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE, BluetoothDevice::class.java)
                    } else {
                        @Suppress("DEPRECATION")
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    }
                    device?.let {
                        addDevice(it, paired = it.bondState == BluetoothDevice.BOND_BONDED)
                        when (it.bondState) {
                            BluetoothDevice.BOND_BONDED -> {
                                _uiState.value = _uiState.value.copy(statusMessage = "Paired with ${safeName(it)}. Starting HID connection now...")
                                if (pendingPairConnectAddress == null || pendingPairConnectAddress == it.address) {
                                    pendingPairConnectAddress = null
                                    connect(it)
                                }
                            }
                            BluetoothDevice.BOND_BONDING -> {
                                _uiState.value = _uiState.value.copy(statusMessage = "Pairing with ${safeName(it)}... Accept the code/dialog on both devices.")
                            }
                            else -> {
                                if (pendingPairConnectAddress == it.address) pendingPairConnectAddress = null
                                _uiState.value = _uiState.value.copy(statusMessage = "Pairing ended for ${safeName(it)}. If Windows kept an old BluePilot entry, remove it and pair again.")
                            }
                        }
                    }
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
        registerReceiverIfNeeded()
        loadPairedDevices()
        startHidForegroundService()
        hidManager.initialize()
        hidManager.connectionState.onEach { state ->
            val deviceName = state.device?.name ?: "device"
            val message = when (state.state) {
                com.bluepilot.remote.model.ConnectionState.CONNECTED -> "Connected to $deviceName. You can use mouse, keyboard, media and gamepad controls now."
                com.bluepilot.remote.model.ConnectionState.CONNECTING -> "Connecting to $deviceName... Accept any pairing/connect dialog on the PC."
                com.bluepilot.remote.model.ConnectionState.DISCONNECTED -> if (_uiState.value.statusMessage.startsWith("Connecting")) "Disconnected. On Windows, remove old BluePilot pairing, pair again, then click Connect." else _uiState.value.statusMessage
                com.bluepilot.remote.model.ConnectionState.HID_UNSUPPORTED -> state.errorMessage ?: "This phone/ROM does not support Bluetooth HID Device mode."
                com.bluepilot.remote.model.ConnectionState.BLUETOOTH_DISABLED -> "Bluetooth is off. Turn it on first."
                com.bluepilot.remote.model.ConnectionState.ERROR -> state.errorMessage ?: "Bluetooth connection error."
                else -> _uiState.value.statusMessage
            }
            val updatedDevices = _uiState.value.devices.map { device ->
                if (state.device?.address == device.address) {
                    device.copy(isConnected = state.state == com.bluepilot.remote.model.ConnectionState.CONNECTED)
                } else {
                    device.copy(isConnected = false)
                }
            }
            _uiState.value = _uiState.value.copy(statusMessage = message, devices = updatedDevices, connectionState = state.state)
        }.launchIn(viewModelScope)
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

    fun prepareHostMode() {
        if (!hasBluetoothPermissions()) {
            _uiState.value = _uiState.value.copy(
                statusMessage = "Bluetooth permissions are missing. Allow Nearby devices/Bluetooth permissions first, then tap Prepare PC connection again."
            )
            return
        }
        startHidForegroundService()
        hidManager.initialize()
        hidManager.registerHidApp()
        _uiState.value = _uiState.value.copy(
            statusMessage = "Preparing HID host mode. Now make this phone discoverable, then pair from the PC Bluetooth screen."
        )
    }

    fun markDiscoverabilityRequested() {
        if (!hasBluetoothPermissions()) {
            _uiState.value = _uiState.value.copy(statusMessage = "Bluetooth permissions are missing. Grant permissions and try again.")
            return
        }
        startHidForegroundService()
        hidManager.initialize()
        hidManager.registerHidApp()
        _uiState.value = _uiState.value.copy(
            isDiscoverable = true,
            statusMessage = "HID host mode is ready. On the PC: remove old BluePilot entry, open Bluetooth, add device, select this phone, accept pairing, then wait for Connected."
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
        if (!device.isPaired) {
            pendingPairConnectAddress = device.address
            _uiState.value = _uiState.value.copy(statusMessage = "Pairing request sent to ${device.name}. Accept it on the PC; BluePilot will connect automatically after pairing.")
            pairDevice(device)
            return
        }
        connectToAddress(device.address)
    }

    fun openBluetoothSettings() = hidManager.openBluetoothSettings()

    fun pairDevice(device: RemoteDevice) {
        val adapter = bluetoothAdapter ?: return
        if (!BluetoothAdapter.checkBluetoothAddress(device.address)) return
        try {
            bluetoothAdapter?.takeIf { it.isDiscovering }?.cancelDiscovery()
            val bluetoothDevice = adapter.getRemoteDevice(device.address)
            if (bluetoothDevice.bondState == BluetoothDevice.BOND_BONDED) {
                connect(bluetoothDevice)
            } else {
                bluetoothDevice.createBond()
            }
        } catch (e: SecurityException) {
            _uiState.value = _uiState.value.copy(statusMessage = "Bluetooth permission denied: ${e.message}")
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(statusMessage = "Pairing error: ${e.message}")
        }
    }

    @SuppressLint("MissingPermission")
    private fun connect(device: BluetoothDevice) {
        viewModelScope.launch {
            try {
                bluetoothAdapter?.takeIf { it.isDiscovering }?.cancelDiscovery()
                startHidForegroundService()
                _uiState.value = _uiState.value.copy(
                    isScanning = false,
                    statusMessage = "Connecting to ${safeName(device)}..."
                )
                val initialized = hidManager.initialize()
                if (!initialized) {
                    _uiState.value = _uiState.value.copy(statusMessage = "Could not open Android HID service. This phone may not support Bluetooth HID Device mode.")
                    return@launch
                }
                hidManager.registerHidApp()
                val started = hidManager.connect(device)
                _uiState.value = _uiState.value.copy(
                    statusMessage = if (started) {
                        "Connecting to ${safeName(device)}... If the PC shows a pairing/request dialog, accept it. If it stays stuck, remove old BluePilot from Windows Bluetooth settings and pair again."
                    } else {
                        "Connection could not start. Many PCs require removing old pairing and pairing again from Windows Bluetooth settings."
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
        (a + b)
            .filter { it.address.isNotBlank() }
            .groupBy { it.address }
            .map { (_, devices) ->
                val best = devices.sortedWith(
                    compareByDescending<RemoteDevice> { it.isConnected }
                        .thenByDescending { it.isPaired }
                        .thenByDescending { it.name != "Unknown device" }
                ).first()
                best.copy(
                    isConnected = devices.any { it.isConnected },
                    isPaired = devices.any { it.isPaired }
                )
            }
            .sortedWith(compareByDescending<RemoteDevice> { it.isConnected }.thenByDescending { it.isPaired }.thenBy { it.name.lowercase() })

    private fun registerReceiverIfNeeded() {
        if (receiverRegistered) return
        val filter = IntentFilter().apply {
            addAction(BluetoothDevice.ACTION_FOUND)
            addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
            addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
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
                ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_ADVERTISE) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun startHidForegroundService() {
        try {
            val intent = Intent(context, HidService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ContextCompat.startForegroundService(context, intent)
            } else {
                context.startService(intent)
            }
        } catch (e: Exception) {
            // Do not block connection; some Android versions restrict service starts.
            _uiState.value = _uiState.value.copy(statusMessage = _uiState.value.statusMessage)
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
    val devices: List<RemoteDevice> = emptyList(),
    val connectionState: ConnectionState = ConnectionState.DISCONNECTED
)
