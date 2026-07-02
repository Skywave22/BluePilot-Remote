@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.bluepilot.remote.ui.screens.scanner

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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

/** Manual pairing helper for reliable PC connection. */
@Composable
fun ScannerScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    BluePilotBackground {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ScreenHeader("Pairing Helper", "Reliable manual PC connection steps") {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = PrimaryDark)
                }
            }

            GlassCard(Modifier.fillMaxWidth()) {
                Icon(
                    imageVector = Icons.Default.QrCodeScanner,
                    contentDescription = null,
                    tint = PrimaryDark,
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 8.dp)
                )
                Text("Manual pairing helper", color = OnSurface, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                Text(
                    "Use these steps when Windows pairing is stuck or an old pairing blocks HID connection.",
                    color = OnSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }

            GlassCard(Modifier.fillMaxWidth()) {
                Text("PC connection checklist", color = PrimaryDark, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                listOf(
                    "1. Remove old phone/BluePilot pairing from Windows Bluetooth.",
                    "2. Open BluePilot Connect screen and tap Prepare PC connection.",
                    "3. Accept Android discoverable permission.",
                    "4. On PC: Settings > Bluetooth > Add device > Bluetooth.",
                    "5. Select your phone and accept pairing on both devices.",
                    "6. Return to BluePilot and connect from paired device list if needed."
                ).forEach { Text(it, color = OnSurfaceVariant, style = MaterialTheme.typography.bodyMedium) }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                PrimaryGlowButton(
                    text = "Copy steps",
                    icon = Icons.Default.ContentCopy,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        val steps = "Remove old pairing. Tap Prepare PC connection. Accept discoverable. On PC add Bluetooth device. Select phone and accept pairing."
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("BluePilot steps", steps))
                        Toast.makeText(context, "Steps copied", Toast.LENGTH_SHORT).show()
                    }
                )
                GhostButton("Back", onNavigateBack, Modifier.weight(1f))
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}
