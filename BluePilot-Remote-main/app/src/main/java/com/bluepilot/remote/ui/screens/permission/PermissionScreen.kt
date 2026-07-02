@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.bluepilot.remote.ui.screens.permission

import android.Manifest
import android.os.Build
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bluepilot.remote.ui.components.BluePilotBackground
import com.bluepilot.remote.ui.components.GhostButton
import com.bluepilot.remote.ui.components.GlassCard
import com.bluepilot.remote.ui.components.PrimaryGlowButton
import com.bluepilot.remote.ui.components.ScreenHeader
import com.bluepilot.remote.ui.theme.OnSurface
import com.bluepilot.remote.ui.theme.OnSurfaceVariant
import com.bluepilot.remote.ui.theme.PrimaryDark

/**
 * Production permission screen.
 * Requests only needed permissions. No unused dangerous permissions are requested.
 */
@Composable
fun PermissionScreen(
    onRequestPermissions: (List<String>) -> Unit,
    onContinue: () -> Unit
) {
    val requiredPermissions = runtimePermissions()

    BluePilotBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(24.dp))
            ScreenHeader("BluePilot Remote", "Privacy-first Bluetooth HID control")

            GlassCard(Modifier.fillMaxWidth()) {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Bluetooth, contentDescription = null, tint = PrimaryDark, modifier = Modifier.size(76.dp))
                }
                Text(
                    "Turn your phone into a Bluetooth mouse, keyboard, media remote, presenter and gamepad.",
                    color = OnSurface,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
                Text(
                    "No cloud account. No external server. Commands are sent directly over Bluetooth HID.",
                    color = OnSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }

            GlassCard(Modifier.fillMaxWidth()) {
                Text("Required permissions", color = PrimaryDark, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                PermissionLine(Icons.Default.Bluetooth, "Nearby Bluetooth devices", "Find, pair and connect HID devices.")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    PermissionLine(Icons.Default.Notifications, "Notification", "Keep HID service alive while app is backgrounded.")
                }
                PermissionLine(Icons.Default.PrivacyTip, "Security", "No unused camera or microphone permission is requested.")
            }

            PrimaryGlowButton(
                text = "Grant permissions",
                icon = Icons.Default.ArrowForward,
                modifier = Modifier.fillMaxWidth(),
                onClick = { onRequestPermissions(requiredPermissions) }
            )
            GhostButton(
                text = "Continue",
                modifier = Modifier.fillMaxWidth(),
                onClick = onContinue
            )
            Text(
                "If controls do not connect, return here and make sure Bluetooth permissions are allowed in Android app settings.",
                color = OnSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable
private fun PermissionLine(icon: ImageVector, title: String, subtitle: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = PrimaryDark, modifier = Modifier.size(26.dp))
        Column(Modifier.weight(1f)) {
            Text(title, color = OnSurface, fontWeight = FontWeight.SemiBold)
            Text(subtitle, color = OnSurfaceVariant, style = MaterialTheme.typography.bodySmall)
        }
    }
}

private fun runtimePermissions(): List<String> {
    val permissions = mutableListOf<String>()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        permissions += Manifest.permission.BLUETOOTH_CONNECT
        permissions += Manifest.permission.BLUETOOTH_SCAN
        permissions += Manifest.permission.BLUETOOTH_ADVERTISE
    } else {
        permissions += Manifest.permission.ACCESS_FINE_LOCATION
        permissions += Manifest.permission.BLUETOOTH
        permissions += Manifest.permission.BLUETOOTH_ADMIN
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        permissions += Manifest.permission.POST_NOTIFICATIONS
    }
    return permissions.distinct()
}
