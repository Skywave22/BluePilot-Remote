@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.bluepilot.remote.ui.screens.gamepad

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bluepilot.remote.model.DpadDirection
import com.bluepilot.remote.model.GamepadButton
import com.bluepilot.remote.ui.components.BluePilotBackground
import com.bluepilot.remote.ui.components.GlassCard
import com.bluepilot.remote.ui.components.PrimaryGlowButton
import com.bluepilot.remote.ui.components.RemotePadButton
import com.bluepilot.remote.ui.components.ScreenHeader
import com.bluepilot.remote.ui.components.StatusPill
import com.bluepilot.remote.ui.theme.PrimaryDark
import com.bluepilot.remote.ui.theme.OnSurfaceVariant
import com.bluepilot.remote.viewmodel.RemoteControlViewModel

@Composable
fun GamepadScreen(
    onNavigateBack: () -> Unit,
    remote: RemoteControlViewModel = hiltViewModel()
) {
    val connection by remote.connectionState.collectAsState()
    val gamepadSettings by remote.gamepadSettings.collectAsState()
    BluePilotBackground {
        Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            ScreenHeader("Gamepad Controller", "Mode: ${gamepadSettings.gamepadMode.name.replace('_', ' ')}") {
                IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = PrimaryDark) }
            }
            StatusPill(connection.state, connection.state.name.replace('_', ' '), Modifier.align(Alignment.CenterHorizontally))

            Text(
                "Settings applied: sensitivity ${gamepadSettings.joystickSensitivity}%, dead zone ${gamepadSettings.deadZone}%",
                color = OnSurfaceVariant,
                style = MaterialTheme.typography.bodySmall
            )

            Row(Modifier.fillMaxWidth().weight(1f), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                GlassCard(Modifier.weight(1f).fillMaxSize()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            RemotePadButton("↑", { remote.dpad(DpadDirection.UP) }, Modifier.fillMaxWidth(0.55f))
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                RemotePadButton("←", { remote.dpad(DpadDirection.LEFT) }, Modifier.weight(1f))
                                RemotePadButton("→", { remote.dpad(DpadDirection.RIGHT) }, Modifier.weight(1f))
                            }
                            RemotePadButton("↓", { remote.dpad(DpadDirection.DOWN) }, Modifier.fillMaxWidth(0.55f))
                        }
                    }
                }
                GlassCard(Modifier.weight(1f).fillMaxSize()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            RemotePadButton("Y", { remote.gamepadButton(GamepadButton.Y) }, Modifier.fillMaxWidth(0.55f), active = true)
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                RemotePadButton("X", { remote.gamepadButton(GamepadButton.X) }, Modifier.weight(1f), active = true)
                                RemotePadButton("B", { remote.gamepadButton(GamepadButton.B) }, Modifier.weight(1f), active = true)
                            }
                            RemotePadButton("A", { remote.gamepadButton(GamepadButton.A) }, Modifier.fillMaxWidth(0.55f), active = true)
                        }
                    }
                }
            }

            GlassCard(Modifier.fillMaxWidth()) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    RemotePadButton("L1", { remote.gamepadButton(GamepadButton.L1) }, Modifier.weight(1f))
                    RemotePadButton("L2", { remote.gamepadButton(GamepadButton.L2) }, Modifier.weight(1f))
                    PrimaryGlowButton("Home", { remote.gamepadButton(GamepadButton.HOME) }, Modifier.weight(1.3f), Icons.Default.SportsEsports)
                    RemotePadButton("R2", { remote.gamepadButton(GamepadButton.R2) }, Modifier.weight(1f))
                    RemotePadButton("R1", { remote.gamepadButton(GamepadButton.R1) }, Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    RemotePadButton("Select", { remote.gamepadButton(GamepadButton.SELECT) }, Modifier.weight(1f))
                    RemotePadButton("Start", { remote.gamepadButton(GamepadButton.START) }, Modifier.weight(1f))
                }
            }
            Spacer(Modifier.height(10.dp))
        }
    }
}
