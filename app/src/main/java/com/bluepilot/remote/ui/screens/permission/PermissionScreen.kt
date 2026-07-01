@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.bluepilot.remote.ui.screens.permission

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.outlined.Bluetooth
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material.icons.outlined.Sensors
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bluepilot.remote.ui.theme.*

/**
 * Permission screen for setup
 * Shows app information and permission checklist
 */
@Composable
fun PermissionScreen(
    onRequestPermissions: (List<String>) -> Unit,
    onContinue: () -> Unit
) {
    var bluetoothEnabled by remember { mutableStateOf(true) }
    var nearbyDevicesEnabled by remember { mutableStateOf(true) }
    var cameraEnabled by remember { mutableStateOf(false) }
    var microphoneEnabled by remember { mutableStateOf(false) }

    val allRequiredEnabled = bluetoothEnabled && nearbyDevicesEnabled

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        // Background atmospheric effect
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Color.Transparent
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // App Logo & Branding
            LogoSection()

            Spacer(modifier = Modifier.height(32.dp))

            // Permission Selection Section
            PermissionSection(
                bluetoothEnabled = bluetoothEnabled,
                onBluetoothEnabledChange = { bluetoothEnabled = it },
                nearbyDevicesEnabled = nearbyDevicesEnabled,
                onNearbyDevicesEnabledChange = { nearbyDevicesEnabled = it },
                cameraEnabled = cameraEnabled,
                onCameraEnabledChange = { cameraEnabled = it },
                microphoneEnabled = microphoneEnabled,
                onMicrophoneEnabledChange = { microphoneEnabled = it }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Action Area
            ActionSection(
                canContinue = allRequiredEnabled,
                onRequestPermissions = {
                    val permissions = mutableListOf<String>()
                    if (bluetoothEnabled) {
                        // Add Bluetooth permissions
                        permissions.addAll(getBluetoothPermissions())
                    }
                    if (nearbyDevicesEnabled) {
                        // Add nearby devices permissions
                    }
                    if (cameraEnabled) {
                        permissions.add(android.Manifest.permission.CAMERA)
                    }
                    if (microphoneEnabled) {
                        permissions.add(android.Manifest.permission.RECORD_AUDIO)
                    }
                    onRequestPermissions(permissions)
                },
                onContinue = onContinue
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Privacy link
            Text(
                text = "Learn more about our privacy policy",
                color = Primary,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    }
}

@Composable
private fun LogoSection() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App Logo
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(PrimaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Bluetooth,
                contentDescription = "BluePilot Logo",
                tint = OnPrimaryContainer,
                modifier = Modifier.size(48.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // App Name
        Text(
            text = "BluePilot Remote",
            color = OnSurface,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        // App Description
        Text(
            text = "Control your computer, TV, tablet, and games over Bluetooth.",
            color = OnSurfaceVariant,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

@Composable
private fun PermissionSection(
    bluetoothEnabled: Boolean,
    onBluetoothEnabledChange: (Boolean) -> Unit,
    nearbyDevicesEnabled: Boolean,
    onNearbyDevicesEnabledChange: (Boolean) -> Unit,
    cameraEnabled: Boolean,
    onCameraEnabledChange: (Boolean) -> Unit,
    microphoneEnabled: Boolean,
    onMicrophoneEnabledChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceContainerLow
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Setup Permissions",
                    color = OnSurface,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Enable these to unlock full functionality",
                    color = OnSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Permission Items
            PermissionItem(
                icon = Icons.Outlined.Bluetooth,
                title = "Bluetooth",
                description = "Connect to nearby hardware",
                enabled = bluetoothEnabled,
                onEnabledChange = onBluetoothEnabledChange,
                required = true
            )

            PermissionItem(
                icon = Icons.Outlined.Sensors,
                title = "Nearby devices",
                description = "Discover local controllers",
                enabled = nearbyDevicesEnabled,
                onEnabledChange = onNearbyDevicesEnabledChange,
                required = true
            )

            PermissionItem(
                icon = Icons.Outlined.QrCodeScanner,
                title = "Camera (scanner)",
                description = "Scan pairing codes",
                enabled = cameraEnabled,
                onEnabledChange = onCameraEnabledChange,
                required = false
            )

            PermissionItem(
                icon = Icons.Outlined.Mic,
                title = "Microphone (voice)",
                description = "Voice commands & dictation",
                enabled = microphoneEnabled,
                onEnabledChange = onMicrophoneEnabledChange,
                required = false
            )
        }
    }
}

@Composable
private fun PermissionItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    enabled: Boolean,
    onEnabledChange: (Boolean) -> Unit,
    required: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (enabled) SecondaryContainer else SurfaceContainerHighest
        ),
        onClick = { onEnabledChange(!enabled) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        if (enabled) OnSecondaryContainer else SurfaceContainerHighest
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = if (enabled) OnSecondaryContainer else OnSurfaceVariant,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Text
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    color = OnSurface,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = description,
                    color = OnSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 12.sp
                )
            }

            // Checkbox
            Checkbox(
                checked = enabled,
                onCheckedChange = onEnabledChange,
                colors = CheckboxDefaults.colors(
                    checkedColor = Primary,
                    uncheckedColor = OutlineVariant
                ),
                enabled = !required
            )
        }
    }
}

@Composable
private fun ActionSection(
    canContinue: Boolean,
    onRequestPermissions: () -> Unit,
    onContinue: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                if (canContinue) {
                    onRequestPermissions()
                    onContinue()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = canContinue,
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = Primary,
                contentColor = OnPrimary,
                disabledContainerColor = OutlineVariant,
                disabledContentColor = OnSurfaceVariant
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 4.dp,
                pressedElevation = 8.dp
            )
        ) {
            Text(
                text = "Continue",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Continue",
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

// Helper function to get Bluetooth permissions based on Android version
private fun getBluetoothPermissions(): List<String> {
    return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
        listOf(
            android.Manifest.permission.BLUETOOTH_CONNECT,
            android.Manifest.permission.BLUETOOTH_SCAN,
            android.Manifest.permission.BLUETOOTH_ADVERTISE
        )
    } else {
        listOf(
            android.Manifest.permission.BLUETOOTH,
            android.Manifest.permission.BLUETOOTH_ADMIN,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        )
    }
}
