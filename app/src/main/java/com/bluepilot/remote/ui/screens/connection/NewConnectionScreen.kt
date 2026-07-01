@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.bluepilot.remote.ui.screens.connection

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.BluetoothSearching
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SettingsBluetooth
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bluepilot.remote.model.ConnectionState
import com.bluepilot.remote.model.RemoteDevice
import com.bluepilot.remote.ui.components.BluePilotBackground
import com.bluepilot.remote.ui.components.GhostButton
import com.bluepilot.remote.ui.components.GlassCard
import com.bluepilot.remote.ui.components.PrimaryGlowButton
import com.bluepilot.remote.ui.components.ScreenHeader
import com.bluepilot.remote.ui.components.StatusPill
import com.bluepilot.remote.ui.theme.OnSurface
import com.bluepilot.remote.ui.theme.OnSurfaceVariant
import com.bluepilot.remote.ui.theme.OutlineVariant
import com.bluepilot.remote.ui.theme.Primary
import com.bluepilot.remote.ui.theme.PrimaryDark
import com.bluepilot.remote.ui.theme.PrimaryLight
import com.bluepilot.remote.ui.theme.SurfaceContainer
import com.bluepilot.remote.viewmodel.ConnectionViewModel

@Composable
fun NewConnectionScreen(
    onNavigateToScreen: (String) -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToDevices: () -> Unit,
    viewModel: ConnectionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var macAddress by remember { mutableStateOf("") }

    val discoverabilityLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { viewModel.markDiscoverabilityRequested() }

    fun requestDiscoverability() {
        viewModel.prepareHostMode()
        val intent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
            putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300)
        }
        discoverabilityLauncher.launch(intent)
    }

    BluePilotBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            ScreenHeader(
                title = "BluePilot Remote",
                subtitle = "Bluetooth HID control deck"
            ) {
                IconButton(onClick = onNavigateToSettings) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings", tint = PrimaryDark)
                }
            }

            StatusPill(
                state = uiState.connectionState,
                label = uiState.connectionState.name.replace('_', ' '),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            PcConnectionCard(
                isReady = uiState.isDiscoverable,
                onPrepare = ::requestDiscoverability
            )

            StatusConsole(uiState.statusMessage)

            DeviceResults(
                devices = uiState.devices,
                onConnect = viewModel::connectToDevice,
                onOpenDevices = onNavigateToDevices,
                onOpenBluetoothSettings = viewModel::openBluetoothSettings,
                onScan = viewModel::startScan,
                onCancel = viewModel::stopScan,
                isScanning = uiState.isScanning
            )

            ManualConnectCard(
                macAddress = macAddress,
                onMacAddressChange = { macAddress = formatMacAddress(it) },
                onConnectClick = { viewModel.connectToAddress(macAddress) }
            )

            Spacer(modifier = Modifier.height(80.dp))
        }

        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
            BottomNavigationBar(onNavigateToScreen = onNavigateToScreen)
        }
    }
}

@Composable
private fun PcConnectionCard(isReady: Boolean, onPrepare: () -> Unit) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconBadge(Icons.Default.BluetoothSearching)
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text("Connect PC to this phone", color = OnSurface, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("Use this first for Windows PC connection.", color = OnSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
            }
        }
        val steps = listOf(
            "Remove old BluePilot / phone pairing from Windows Bluetooth.",
            "Tap Prepare PC connection and accept discoverability.",
            "On PC open Bluetooth > Add device > Bluetooth.",
            "Select this phone, accept pairing on both sides.",
            "Wait for Connected, then use mouse and keyboard."
        )
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            steps.forEachIndexed { index, step ->
                Row(verticalAlignment = Alignment.Top) {
                    Text("${index + 1}", color = PrimaryDark, fontWeight = FontWeight.Bold, modifier = Modifier.width(26.dp))
                    Text(step, color = OnSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
        PrimaryGlowButton(
            text = if (isReady) "Host mode ready" else "Prepare PC connection",
            icon = Icons.Default.Visibility,
            onClick = onPrepare,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun StatusConsole(message: String) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Text("STATUS", color = PrimaryDark, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
        Text(message, color = OnSurface, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun DeviceResults(
    devices: List<RemoteDevice>,
    onConnect: (RemoteDevice) -> Unit,
    onOpenDevices: () -> Unit,
    onOpenBluetoothSettings: () -> Unit,
    onScan: () -> Unit,
    onCancel: () -> Unit,
    isScanning: Boolean
) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text("Available Devices", color = OnSurface, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(if (devices.isEmpty()) "No devices yet" else "${devices.size} device(s) found", color = OnSurfaceVariant, style = MaterialTheme.typography.bodySmall)
            }
            GhostButton(text = "Manage", onClick = onOpenDevices, modifier = Modifier.width(112.dp))
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            PrimaryGlowButton(text = if (isScanning) "Scanning" else "Scan", icon = Icons.Default.Refresh, onClick = onScan, enabled = !isScanning, modifier = Modifier.weight(1f))
            GhostButton(text = "Android BT", onClick = onOpenBluetoothSettings, modifier = Modifier.weight(1f))
            GhostButton(text = "Cancel", onClick = onCancel, enabled = isScanning, modifier = Modifier.weight(1f))
        }

        if (devices.isEmpty()) {
            Text("Tip: for PC connection, tap Prepare PC connection, then pair from Windows Add device.", color = OnSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                devices.forEach { device ->
                    DeviceCard(device = device, onConnect = { onConnect(device) })
                }
            }
        }
    }
}

@Composable
private fun DeviceCard(device: RemoteDevice, onConnect: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconBadge(if (device.isConnected) Icons.Default.Computer else Icons.Default.Bluetooth, small = true)
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(device.name.ifBlank { "Unknown device" }, color = OnSurface, fontWeight = FontWeight.Bold)
            Text(
                "${device.address} • ${if (device.isConnected) "Connected" else if (device.isPaired) "Paired" else "Discovered"}",
                color = OnSurfaceVariant,
                style = MaterialTheme.typography.bodySmall
            )
        }
        PrimaryGlowButton(
            text = if (device.isConnected) "Connected" else if (device.isPaired) "Connect" else "Pair",
            onClick = onConnect,
            enabled = !device.isConnected,
            modifier = Modifier.width(118.dp)
        )
    }
}

@Composable
private fun ManualConnectCard(macAddress: String, onMacAddressChange: (String) -> Unit, onConnectClick: () -> Unit) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconBadge(Icons.Default.Link)
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text("Manual MAC connect", color = OnSurface, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("Use only if you know the Bluetooth address.", color = OnSurfaceVariant, style = MaterialTheme.typography.bodySmall)
            }
        }
        OutlinedTextField(
            value = macAddress,
            onValueChange = { value -> if (value.length <= 17) onMacAddressChange(value.uppercase()) },
            label = { Text("XX:XX:XX:XX:XX:XX") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryDark,
                unfocusedBorderColor = OutlineVariant,
                focusedTextColor = OnSurface,
                unfocusedTextColor = OnSurface
            )
        )
        GhostButton(text = "Connect by address", icon = Icons.Default.Link, onClick = onConnectClick, enabled = macAddress.length == 17, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
private fun IconBadge(icon: ImageVector, small: Boolean = false) {
    Box(
        modifier = Modifier
            .size(if (small) 44.dp else 54.dp)
            .border(BorderStroke(1.dp, PrimaryLight.copy(alpha = 0.22f)), CircleShape)
            .padding(2.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(2.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = PrimaryDark, modifier = Modifier.size(if (small) 22.dp else 28.dp))
        }
    }
}

@Composable
private fun BottomNavigationBar(onNavigateToScreen: (String) -> Unit) {
    NavigationBar(containerColor = SurfaceContainer.copy(alpha = 0.92f), tonalElevation = 0.dp) {
        NavigationBarItem(selected = true, onClick = { }, icon = { Icon(Icons.Default.BluetoothSearching, contentDescription = "Connect") }, label = { Text("Connect") })
        NavigationBarItem(selected = false, onClick = { onNavigateToScreen("mouse_keyboard") }, icon = { Icon(Icons.Default.SettingsBluetooth, contentDescription = "Mouse") }, label = { Text("Mouse") })
        NavigationBarItem(selected = false, onClick = { onNavigateToScreen("pc_keyboard") }, icon = { Icon(Icons.Default.Computer, contentDescription = "Keyboard") }, label = { Text("Keyboard") })
        NavigationBarItem(selected = false, onClick = { onNavigateToScreen("gamepad") }, icon = { Icon(Icons.Default.SportsEsports, contentDescription = "Gamepad") }, label = { Text("Gamepad") })
    }
}

private fun formatMacAddress(input: String): String {
    val cleaned = input.uppercase().filter { it.isLetterOrDigit() }
    val formatted = StringBuilder()
    for (i in cleaned.indices) {
        if (i > 0 && i % 2 == 0) formatted.append(":")
        formatted.append(cleaned[i])
        if (formatted.length >= 17) break
    }
    return formatted.toString()
}
