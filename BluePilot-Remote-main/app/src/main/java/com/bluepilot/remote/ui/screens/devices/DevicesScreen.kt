@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.bluepilot.remote.ui.screens.devices

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.BluetoothConnected
import androidx.compose.material.icons.filled.BluetoothSearching
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bluepilot.remote.model.RemoteDevice
import com.bluepilot.remote.ui.components.BluePilotBackground
import com.bluepilot.remote.ui.components.GhostButton
import com.bluepilot.remote.ui.components.GlassCard
import com.bluepilot.remote.ui.components.PrimaryGlowButton
import com.bluepilot.remote.ui.components.ScreenHeader
import com.bluepilot.remote.ui.components.StatusPill
import com.bluepilot.remote.ui.theme.OnSurface
import com.bluepilot.remote.ui.theme.OnSurfaceVariant
import com.bluepilot.remote.ui.theme.PrimaryDark
import com.bluepilot.remote.viewmodel.ConnectionViewModel

/** Bluetooth devices screen backed by ConnectionViewModel. */
@Composable
fun DevicesScreen(
    onNavigateBack: () -> Unit,
    viewModel: ConnectionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val paired = uiState.devices.filter { it.isPaired }
    val available = uiState.devices.filterNot { it.isPaired }

    BluePilotBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ScreenHeader("Bluetooth Devices", "Paired and nearby devices") {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = PrimaryDark)
                }
            }
            StatusPill(uiState.connectionState, uiState.connectionState.name.replace('_', ' '), Modifier.align(Alignment.CenterHorizontally))

            GlassCard(Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Icon(Icons.Default.BluetoothSearching, contentDescription = null, tint = PrimaryDark)
                    Column(Modifier.weight(1f)) {
                        Text("Device search", color = OnSurface, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(uiState.statusMessage, color = OnSurfaceVariant, style = MaterialTheme.typography.bodySmall)
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    PrimaryGlowButton(if (uiState.isScanning) "Scanning" else "Scan", viewModel::startScan, Modifier.weight(1f), Icons.Default.Refresh, enabled = !uiState.isScanning)
                    GhostButton("Android BT", viewModel::openBluetoothSettings, Modifier.weight(1f), Icons.Default.Bluetooth)
                    GhostButton("Cancel", viewModel::stopScan, Modifier.weight(1f), enabled = uiState.isScanning)
                }
            }

            DeviceSection(
                title = "Paired devices",
                subtitle = "Best option after Windows pairing is complete.",
                devices = paired,
                emptyText = "No paired devices loaded. Pair from Windows Bluetooth after Prepare PC connection.",
                onConnect = viewModel::connectToDevice
            )

            DeviceSection(
                title = "Nearby devices",
                subtitle = "Devices found in current scan.",
                devices = available,
                emptyText = if (uiState.isScanning) "Searching nearby devices..." else "Tap Scan to search nearby Bluetooth devices.",
                onConnect = viewModel::connectToDevice
            )

            GlassCard(Modifier.fillMaxWidth()) {
                Text("Connection tip", color = PrimaryDark, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                Text(
                    "For Windows PC, the most reliable method is: remove old pairing, prepare PC connection in BluePilot, then add this phone from Windows Bluetooth.",
                    color = OnSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            GhostButton("Back", onNavigateBack, Modifier.fillMaxWidth())
            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable
private fun DeviceSection(
    title: String,
    subtitle: String,
    devices: List<RemoteDevice>,
    emptyText: String,
    onConnect: (RemoteDevice) -> Unit
) {
    GlassCard(Modifier.fillMaxWidth()) {
        Text(title, color = OnSurface, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text(subtitle, color = OnSurfaceVariant, style = MaterialTheme.typography.bodySmall)
        if (devices.isEmpty()) {
            Text(emptyText, color = OnSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
        } else {
            devices.forEach { device ->
                DeviceRow(device, onConnect)
            }
        }
    }
}

@Composable
private fun DeviceRow(device: RemoteDevice, onConnect: (RemoteDevice) -> Unit) {
    GlassCard(Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(
                if (device.isConnected) Icons.Default.BluetoothConnected else Icons.Default.Bluetooth,
                contentDescription = null,
                tint = PrimaryDark
            )
            Column(Modifier.weight(1f)) {
                Text(device.name.ifBlank { "Unknown device" }, color = OnSurface, fontWeight = FontWeight.SemiBold)
                Text(device.address, color = OnSurfaceVariant, style = MaterialTheme.typography.bodySmall)
                Text(
                    when {
                        device.isConnected -> "Connected"
                        device.isPaired -> "Paired"
                        else -> "Not paired"
                    },
                    color = OnSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        PrimaryGlowButton(
            text = if (device.isConnected) "Connected" else if (device.isPaired) "Connect" else "Pair & Connect",
            onClick = { onConnect(device) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !device.isConnected
        )
    }
}
