@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.bluepilot.remote.ui.screens.devices

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.BluetoothSearching
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bluepilot.remote.model.RemoteDevice
import com.bluepilot.remote.ui.theme.Background
import com.bluepilot.remote.ui.theme.OnPrimary
import com.bluepilot.remote.ui.theme.OnPrimaryContainer
import com.bluepilot.remote.ui.theme.OnSurface
import com.bluepilot.remote.ui.theme.OnSurfaceVariant
import com.bluepilot.remote.ui.theme.OutlineVariant
import com.bluepilot.remote.ui.theme.Primary
import com.bluepilot.remote.ui.theme.PrimaryContainer
import com.bluepilot.remote.ui.theme.SurfaceContainerHighest
import com.bluepilot.remote.ui.theme.SurfaceContainerLow
import com.bluepilot.remote.viewmodel.ConnectionViewModel

/**
 * Real Bluetooth devices screen.
 * Shows paired/found devices from ConnectionViewModel instead of fake placeholder cards.
 */
@Composable
fun DevicesScreen(
    onNavigateBack: () -> Unit,
    viewModel: ConnectionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val paired = uiState.devices.filter { it.isPaired }
    val available = uiState.devices.filterNot { it.isPaired }

    Box(
        modifier = Modifier.fillMaxSize().background(Background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(onNavigateBack = onNavigateBack)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                ScanStatusCard(
                    message = uiState.statusMessage,
                    isScanning = uiState.isScanning,
                    onScan = viewModel::startScan,
                    onCancel = viewModel::stopScan,
                    onOpenBluetoothSettings = viewModel::openBluetoothSettings
                )

                DeviceSection(
                    title = "Paired devices",
                    subtitle = "Devices already paired in Android Bluetooth settings",
                    devices = paired,
                    emptyText = "No paired devices found. Pair your PC/TV/tablet in Android Bluetooth settings first if direct connect fails.",
                    onConnect = viewModel::connectToDevice
                )

                DeviceSection(
                    title = "Available devices",
                    subtitle = "Devices found during the current scan",
                    devices = available,
                    emptyText = if (uiState.isScanning) "Scanning nearby Bluetooth devices..." else "Tap Scan for devices to search nearby devices.",
                    onConnect = viewModel::connectToDevice
                )

                Spacer(modifier = Modifier.height(48.dp))
            }
        }
    }
}

@Composable
private fun TopAppBar(onNavigateBack: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Primary)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Bluetooth devices",
                color = Primary,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun ScanStatusCard(
    message: String,
    isScanning: Boolean,
    onScan: () -> Unit,
    onCancel: () -> Unit,
    onOpenBluetoothSettings: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow),
        border = BorderStroke(1.dp, OutlineVariant.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(52.dp).clip(RoundedCornerShape(14.dp)).background(PrimaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    if (isScanning) {
                        CircularProgressIndicator(modifier = Modifier.size(28.dp), color = Primary, strokeWidth = 3.dp)
                    } else {
                        Icon(Icons.Default.BluetoothSearching, contentDescription = null, tint = OnPrimaryContainer)
                    }
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        text = if (isScanning) "Scanning devices" else "Device scanner",
                        color = OnSurface,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(message, color = OnSurfaceVariant, style = MaterialTheme.typography.bodySmall)
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = onOpenBluetoothSettings,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Android BT")
                }
                Button(
                    onClick = onScan,
                    enabled = !isScanning,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary, contentColor = OnPrimary)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (isScanning) "Scanning" else "Scan")
                }
                OutlinedButton(
                    onClick = onCancel,
                    enabled = isScanning,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Cancel")
                }
            }
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
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Column {
            Text(title, color = Primary, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(subtitle, color = OnSurfaceVariant, style = MaterialTheme.typography.bodySmall)
        }

        if (devices.isEmpty()) {
            EmptyDeviceCard(emptyText)
        } else {
            devices.forEach { device ->
                DeviceCard(device = device, onConnect = { onConnect(device) })
            }
        }
    }
}

@Composable
private fun EmptyDeviceCard(text: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow),
        border = BorderStroke(1.dp, OutlineVariant.copy(alpha = 0.12f))
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.Bluetooth, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(36.dp))
            Spacer(modifier = Modifier.height(10.dp))
            Text(text, color = OnSurfaceVariant, textAlign = TextAlign.Center, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun DeviceCard(device: RemoteDevice, onConnect: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow),
        border = BorderStroke(1.dp, OutlineVariant.copy(alpha = 0.16f))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (device.isPaired) PrimaryContainer else SurfaceContainerHighest),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (device.isPaired) Icons.Default.CheckCircle else Icons.Default.Bluetooth,
                        contentDescription = null,
                        tint = if (device.isPaired) OnPrimaryContainer else OnSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = device.name.ifBlank { "Unknown device" },
                        color = OnSurface,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = device.address,
                        color = OnSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall
                    )
                    if (device.isConnected) {
                        Text("Connected", color = Primary, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    } else if (device.isPaired) {
                        Text("Paired", color = Primary, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Button(
                onClick = onConnect,
                enabled = !device.isConnected,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary, contentColor = OnPrimary)
            ) {
                Text(if (device.isConnected) "Connected" else if (device.isPaired) "Connect" else "Pair")
            }
        }
    }
}
