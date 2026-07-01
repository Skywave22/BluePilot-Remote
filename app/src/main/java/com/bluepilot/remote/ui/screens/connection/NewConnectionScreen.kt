@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.bluepilot.remote.ui.screens.connection

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.BluetoothDisabled
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SettingsBluetooth
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.PrivacyTip
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bluepilot.remote.model.RemoteDevice
import com.bluepilot.remote.ui.theme.*
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
    ) {
        viewModel.markDiscoverabilityRequested()
    }

    fun requestDiscoverability() {
        viewModel.prepareHostMode()
        val intent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
            putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300)
        }
        discoverabilityLauncher.launch(intent)
    }

    Box(
        modifier = Modifier.fillMaxSize().background(Background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
        ) {
            TopAppBar(onNavigateBack = { }, onNavigateToSettings = onNavigateToSettings)
            Spacer(modifier = Modifier.height(16.dp))

            AnimatedVisibility(
                visible = uiState.isScanning,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                SearchingState(
                    statusMessage = uiState.statusMessage,
                    onCancel = viewModel::stopScan
                )
            }

            StatusCard(message = uiState.statusMessage)

            ConnectionCards(
                isSearching = uiState.isScanning,
                onSearchClick = viewModel::startScan,
                isDiscovering = uiState.isDiscoverable,
                onDiscoverClick = ::requestDiscoverability,
                macAddress = macAddress,
                onMacAddressChange = { macAddress = formatMacAddress(it) },
                onConnectClick = { viewModel.connectToAddress(macAddress) }
            )

            if (uiState.devices.isNotEmpty()) {
                DeviceResults(
                    devices = uiState.devices,
                    onConnect = viewModel::connectToDevice,
                    onOpenDevices = onNavigateToDevices,
                    onOpenBluetoothSettings = viewModel::openBluetoothSettings
                )
            }

            Spacer(modifier = Modifier.height(100.dp))
        }

        BottomNavigationBar(onNavigateToScreen = onNavigateToScreen)
    }
}

@Composable
private fun TopAppBar(onNavigateBack: () -> Unit, onNavigateToSettings: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Primary)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("New connection", color = Primary, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        }
        Surface(shape = CircleShape, color = SurfaceContainerHighest) {
            Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(8.dp).clip(CircleShape).background(Error))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Disconnected", color = OnSurfaceVariant, style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

@Composable
private fun StatusCard(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow),
        border = BorderStroke(1.dp, OutlineVariant.copy(alpha = 0.18f))
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(14.dp),
            color = OnSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun SearchingState(statusMessage: String, onCancel: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow),
        border = BorderStroke(1.dp, OutlineVariant.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.size(96.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(modifier = Modifier.size(96.dp), color = Primary, strokeWidth = 4.dp)
                Icon(Icons.Default.Search, contentDescription = "Searching", tint = Primary, modifier = Modifier.size(48.dp))
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text("Searching for devices", color = Primary, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Medium)
            Text(statusMessage, color = OnSurfaceVariant, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onCancel,
                colors = ButtonDefaults.buttonColors(containerColor = SurfaceContainerHighest, contentColor = OnSurface),
                shape = CircleShape
            ) { Text("Cancel") }
        }
    }
}

@Composable
private fun ConnectionCards(
    isSearching: Boolean,
    onSearchClick: () -> Unit,
    isDiscovering: Boolean,
    onDiscoverClick: () -> Unit,
    macAddress: String,
    onMacAddressChange: (String) -> Unit,
    onConnectClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ConnectionCard(
            icon = Icons.Default.Search,
            iconBackgroundColor = SecondaryContainer,
            iconTintColor = OnSecondaryContainer,
            title = "Connect to another device",
            description = "Scan for nearby Bluetooth devices. Paired devices appear immediately; new discoverable devices appear while scanning.",
            buttonText = if (isSearching) "Searching..." else "Search devices",
            buttonIcon = Icons.Default.Radar,
            onButtonClick = onSearchClick,
            enabled = !isSearching,
            modifier = Modifier.alpha(if (isSearching) 0.65f else 1f)
        )
        DiscoverabilityCard(isDiscovering = isDiscovering, onDiscoverClick = onDiscoverClick)
        MacAddressCard(macAddress = macAddress, onMacAddressChange = onMacAddressChange, onConnectClick = onConnectClick)
    }
}

@Composable
private fun ConnectionCard(
    icon: ImageVector,
    iconBackgroundColor: Color,
    iconTintColor: Color,
    title: String,
    description: String,
    buttonText: String,
    buttonIcon: ImageVector,
    onButtonClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow),
        border = BorderStroke(1.dp, OutlineVariant.copy(alpha = 0.2f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                IconBox(icon, iconBackgroundColor, iconTintColor)
                Spacer(modifier = Modifier.width(16.dp))
                Column(Modifier.weight(1f)) {
                    Text(title, color = OnSurface, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
                    Text(description, color = OnSurfaceVariant, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 4.dp))
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onButtonClick,
                enabled = enabled,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary, contentColor = OnPrimary)
            ) {
                Icon(buttonIcon, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(buttonText, style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Composable
private fun DiscoverabilityCard(isDiscovering: Boolean, onDiscoverClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow),
        border = BorderStroke(1.dp, OutlineVariant.copy(alpha = 0.2f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                IconBox(Icons.Outlined.Visibility, PrimaryContainer, OnPrimaryContainer)
                Spacer(modifier = Modifier.width(16.dp))
                Column(Modifier.weight(1f)) {
                    Text("Connect PC to this phone", color = OnSurface, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
                    Text("For Windows PC, use this first: remove old BluePilot/phone entry on PC, tap this, then on PC choose Add device > Bluetooth and select this phone. After pairing, BluePilot connects as keyboard/mouse.", color = OnSurfaceVariant, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 4.dp))
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onDiscoverClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = if (isDiscovering) Primary.copy(alpha = 0.2f) else Color.Transparent, contentColor = Primary),
                border = if (!isDiscovering) BorderStroke(1.dp, Primary) else null
            ) {
                Icon(if (isDiscovering) Icons.Default.BluetoothDisabled else Icons.Outlined.Visibility, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (isDiscovering) "Host mode ready" else "Prepare PC connection", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Composable
private fun MacAddressCard(macAddress: String, onMacAddressChange: (String) -> Unit, onConnectClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow),
        border = BorderStroke(1.dp, OutlineVariant.copy(alpha = 0.2f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                IconBox(Icons.Outlined.PrivacyTip, TertiaryContainer, OnTertiaryContainer)
                Spacer(modifier = Modifier.width(16.dp))
                Column(Modifier.weight(1f)) {
                    Text("Connect to hidden device", color = OnSurface, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
                    Text("Enter a Bluetooth MAC address manually.", color = OnSurfaceVariant, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 4.dp))
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            OutlinedTextField(
                value = macAddress,
                onValueChange = { value -> if (value.length <= 17) onMacAddressChange(value.uppercase()) },
                label = { Text("Bluetooth address") },
                placeholder = { Text("XX:XX:XX:XX:XX:XX") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, unfocusedBorderColor = OutlineVariant)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onConnectClick,
                modifier = Modifier.fillMaxWidth(),
                enabled = macAddress.length == 17,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SurfaceContainerHighest, contentColor = OnSurface)
            ) {
                Icon(Icons.Default.Link, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Connect", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Composable
private fun DeviceResults(
    devices: List<RemoteDevice>,
    onConnect: (RemoteDevice) -> Unit,
    onOpenDevices: () -> Unit,
    onOpenBluetoothSettings: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Found devices", color = Primary, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Row {
                TextButton(onClick = onOpenBluetoothSettings) { Text("Android BT") }
                TextButton(onClick = onOpenDevices) { Text("Manage") }
            }
        }
        devices.forEach { device ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow),
                border = BorderStroke(1.dp, OutlineVariant.copy(alpha = 0.15f))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        IconBox(Icons.Default.Bluetooth, if (device.isPaired) PrimaryContainer else SurfaceContainerHighest, if (device.isPaired) OnPrimaryContainer else OnSurfaceVariant)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(device.name, color = OnSurface, fontWeight = FontWeight.Medium)
                            Text("${device.address}${if (device.isConnected) " • Connected" else if (device.isPaired) " • Paired" else ""}", color = OnSurfaceVariant, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                    Button(onClick = { onConnect(device) }, enabled = !device.isConnected, shape = RoundedCornerShape(10.dp)) { Text(if (device.isConnected) "Connected" else if (device.isPaired) "Connect" else "Pair") }
                }
            }
        }
    }
}

@Composable
private fun IconBox(icon: ImageVector, background: Color, tint: Color) {
    Box(
        modifier = Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(background),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(24.dp))
    }
}

@Composable
private fun BottomNavigationBar(onNavigateToScreen: (String) -> Unit) {
    NavigationBar(modifier = Modifier.fillMaxWidth(), containerColor = SurfaceContainer, tonalElevation = 0.dp) {
        NavigationBarItem(selected = false, onClick = { onNavigateToScreen("mouse_keyboard") }, icon = { Icon(Icons.Default.SettingsBluetooth, contentDescription = "Controls") }, label = { Text("Controls") })
        NavigationBarItem(selected = true, onClick = { }, icon = { Icon(Icons.Default.Search, contentDescription = "Connection") }, label = { Text("Connect") })
        NavigationBarItem(selected = false, onClick = { onNavigateToScreen("gamepad") }, icon = { Icon(Icons.Default.Radar, contentDescription = "Gamepad") }, label = { Text("Gamepad") })
        NavigationBarItem(selected = false, onClick = { onNavigateToScreen("settings") }, icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") }, label = { Text("Settings") })
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
