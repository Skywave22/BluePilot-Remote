@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.bluepilot.remote.ui.screens.keyboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bluepilot.remote.ui.components.BluePilotBackground
import com.bluepilot.remote.ui.components.GhostButton
import com.bluepilot.remote.ui.components.GlassCard
import com.bluepilot.remote.ui.components.PrimaryGlowButton
import com.bluepilot.remote.ui.components.RemotePadButton
import com.bluepilot.remote.ui.components.ScreenHeader
import com.bluepilot.remote.ui.components.StatusPill
import com.bluepilot.remote.ui.theme.OnSurface
import com.bluepilot.remote.ui.theme.OnSurfaceVariant
import com.bluepilot.remote.ui.theme.OutlineVariant
import com.bluepilot.remote.ui.theme.PrimaryDark
import com.bluepilot.remote.viewmodel.RemoteControlViewModel

@Composable
fun PCKeyboardScreen(
    onNavigateBack: () -> Unit,
    remote: RemoteControlViewModel = hiltViewModel()
) {
    val connection by remote.connectionState.collectAsState()
    val keyboardSettings by remote.keyboardSettings.collectAsState()
    var text by remember { mutableStateOf("") }

    BluePilotBackground {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ScreenHeader("PC Keyboard", "Full HID keyboard control") {
                IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = PrimaryDark) }
            }
            StatusPill(connection.state, connection.state.name.replace('_', ' '), Modifier.align(Alignment.CenterHorizontally))

            if (keyboardSettings.showTextInputBar) {
                GlassCard(Modifier.fillMaxWidth()) {
                    Text("TEXT BEAM", color = PrimaryDark, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = text,
                        onValueChange = { text = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Write text for your PC") },
                        singleLine = false,
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryDark,
                            unfocusedBorderColor = OutlineVariant,
                            focusedTextColor = OnSurface,
                            unfocusedTextColor = OnSurface
                        )
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        PrimaryGlowButton("Send text", { remote.text(text); text = "" }, Modifier.weight(1f), Icons.Default.Send, text.isNotBlank())
                        GhostButton("Enter", { remote.enter() }, Modifier.weight(1f))
                        GhostButton("Backspace", { remote.backspace() }, Modifier.weight(1f))
                    }
                }
            }

            GlassCard(Modifier.fillMaxWidth()) {
                Text("FUNCTION KEYS", color = PrimaryDark, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                listOf((1..6), (7..12)).forEach { range ->
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        range.forEach { n -> KeyButton("F$n", { remote.keyLabel("F$n") }, Modifier.weight(1f)) }
                    }
                }
            }

            GlassCard(Modifier.fillMaxWidth()) {
                KeyboardRows(remote)
            }


            GlassCard(Modifier.fillMaxWidth()) {
                Text("PRODUCTIVITY SHORTCUTS", color = PrimaryDark, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    KeyButton("Copy", { remote.copy() }, Modifier.weight(1f))
                    KeyButton("Paste", { remote.paste() }, Modifier.weight(1f))
                    KeyButton("Cut", { remote.cut() }, Modifier.weight(1f))
                    KeyButton("All", { remote.selectAll() }, Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    KeyButton("Save", { remote.save() }, Modifier.weight(1f))
                    KeyButton("Undo", { remote.undo() }, Modifier.weight(1f))
                    KeyButton("Redo", { remote.redo() }, Modifier.weight(1f))
                    KeyButton("Del", { remote.delete() }, Modifier.weight(1f))
                }
            }

            GlassCard(Modifier.fillMaxWidth()) {
                Text("NAVIGATION", color = PrimaryDark, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    KeyButton("Home", { remote.keyLabel("Home") }, Modifier.weight(1f))
                    KeyButton("Menu", { remote.keyLabel("Menu") }, Modifier.weight(1f))
                    KeyButton("Esc", { remote.escape() }, Modifier.weight(1f))
                    KeyButton("Tab", { remote.tab() }, Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    KeyButton("←", { remote.keyLabel("Left") }, Modifier.weight(1f))
                    KeyButton("↑", { remote.keyLabel("Up") }, Modifier.weight(1f))
                    KeyButton("↓", { remote.keyLabel("Down") }, Modifier.weight(1f))
                    KeyButton("→", { remote.keyLabel("Right") }, Modifier.weight(1f))
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun KeyboardRows(remote: RemoteControlViewModel) {
    val rows = listOf(
        listOf("1","2","3","4","5","6","7","8","9","0"),
        listOf("Q","W","E","R","T","Y","U","I","O","P"),
        listOf("A","S","D","F","G","H","J","K","L"),
        listOf("Z","X","C","V","B","N","M")
    )
    rows.forEach { row ->
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            row.forEach { label -> KeyButton(label, { remote.keyLabel(label) }, Modifier.weight(1f)) }
        }
    }
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        KeyButton("Ctrl", { remote.keyLabel("Ctrl") }, Modifier.weight(1f))
        KeyButton("Alt", { remote.keyLabel("Alt") }, Modifier.weight(1f))
        PrimaryGlowButton("Space", { remote.space() }, Modifier.weight(2.2f), Icons.Default.Keyboard)
        KeyButton("Enter", { remote.enter() }, Modifier.weight(1.4f))
    }
}

@Composable
private fun KeyButton(label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    RemotePadButton(text = label, onClick = onClick, modifier = modifier)
}
