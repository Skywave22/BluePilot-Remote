@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.bluepilot.remote.ui.screens.help

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
import androidx.compose.material.icons.filled.BluetoothSearching
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.bluepilot.remote.ui.components.ScreenHeader
import com.bluepilot.remote.ui.theme.OnSurface
import com.bluepilot.remote.ui.theme.OnSurfaceVariant
import com.bluepilot.remote.ui.theme.PrimaryDark

/** Professional help and troubleshooting screen. */
@Composable
fun HelpScreen(onNavigateBack: () -> Unit) {
    BluePilotBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ScreenHeader("Help & Troubleshooting", "Fast fixes for Bluetooth HID connection") {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = PrimaryDark)
                }
            }

            GlassCard(Modifier.fillMaxWidth()) {
                Icon(Icons.Default.Help, contentDescription = null, tint = PrimaryDark, modifier = Modifier.align(Alignment.CenterHorizontally))
                Text("BluePilot Remote", color = OnSurface, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                Text(
                    "A privacy-first Bluetooth HID remote for mouse, keyboard, media, presenter and gamepad control.",
                    color = OnSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }

            HelpBlock(
                icon = Icons.Default.BluetoothSearching,
                title = "Best Windows PC pairing steps",
                lines = listOf(
                    "Remove old BluePilot / phone pairing from Windows Bluetooth.",
                    "Open BluePilot and tap Prepare PC connection.",
                    "Accept Android discoverability.",
                    "On Windows, Add device > Bluetooth, select this phone, accept pairing.",
                    "Wait for Connected before using controls."
                )
            )

            HelpBlock(
                icon = Icons.Default.TouchApp,
                title = "Controls not responding",
                lines = listOf(
                    "Confirm the status pill says Connected.",
                    "Try Keyboard first: send a simple Enter or Space key.",
                    "If paired but not connected, remove the Windows pairing and pair again.",
                    "Some Android ROMs do not support HID Device mode; the app will show HID unsupported if Android refuses registration."
                )
            )

            HelpBlock(
                icon = Icons.Default.Security,
                title = "Privacy and security",
                lines = listOf(
                    "No account is required.",
                    "No cloud server is used for controls.",
                    "Camera and microphone permissions are not requested in this build.",
                    "Bluetooth commands are sent directly to your paired device."
                )
            )

            GlassCard(Modifier.fillMaxWidth()) {
                Text("Quick test checklist", color = PrimaryDark, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                CheckLine("1", "Connect to PC until status says Connected.")
                CheckLine("2", "Open Mouse and move cursor.")
                CheckLine("3", "Tap trackpad for left click and use scroll strip.")
                CheckLine("4", "Open Keyboard and send text or Enter.")
                CheckLine("5", "Try Media volume or play/pause.")
            }

            GhostButton("Back", onNavigateBack, Modifier.fillMaxWidth())
            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable
private fun HelpBlock(icon: ImageVector, title: String, lines: List<String>) {
    GlassCard(Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(icon, contentDescription = null, tint = PrimaryDark)
            Text(title, color = OnSurface, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
        lines.forEachIndexed { index, line -> CheckLine((index + 1).toString(), line) }
    }
}

@Composable
private fun CheckLine(number: String, text: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.Top) {
        Text(number, color = PrimaryDark, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 1.dp))
        Text(text, color = OnSurfaceVariant, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
    }
}
