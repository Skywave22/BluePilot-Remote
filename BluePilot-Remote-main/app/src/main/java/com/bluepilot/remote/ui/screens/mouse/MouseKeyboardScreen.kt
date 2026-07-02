@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.bluepilot.remote.ui.screens.mouse

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
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
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BluetoothSearching
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Mouse
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bluepilot.remote.model.ConnectionState
import com.bluepilot.remote.model.MouseButton as HidMouseButton
import com.bluepilot.remote.ui.components.BluePilotBackground
import com.bluepilot.remote.ui.components.GhostButton
import com.bluepilot.remote.ui.components.GlassCard
import com.bluepilot.remote.ui.components.PrimaryGlowButton
import com.bluepilot.remote.ui.components.RemotePadButton
import com.bluepilot.remote.ui.components.ScreenHeader
import com.bluepilot.remote.ui.components.StatusPill
import com.bluepilot.remote.ui.theme.OnSurface
import com.bluepilot.remote.ui.theme.OnSurfaceVariant
import com.bluepilot.remote.ui.theme.PrimaryDark
import com.bluepilot.remote.ui.theme.SurfaceContainerHighest
import com.bluepilot.remote.viewmodel.RemoteControlViewModel

@Composable
fun MouseKeyboardScreen(
    onNavigateBack: () -> Unit,
    onNavigateToConnection: () -> Unit,
    onNavigateToKeyboard: () -> Unit,
    onNavigateToSettings: () -> Unit,
    remote: RemoteControlViewModel = hiltViewModel()
) {
    val connection by remote.connectionState.collectAsState()
    val mouseSettings by remote.mouseSettings.collectAsState()

    BluePilotBackground {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            ScreenHeader("Mouse Trackpad", "Glass touch surface for PC cursor") {
                IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = PrimaryDark) }
            }
            StatusPill(connection.state, connection.state.name.replace('_', ' '), Modifier.align(Alignment.CenterHorizontally))

            GlassCard(modifier = Modifier.fillMaxWidth().weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                            .pointerInput(mouseSettings.sensitivity, mouseSettings.pointerSpeed, mouseSettings.penMode) {
                                detectTapGestures(
                                    onTap = { if (mouseSettings.tapToClick) remote.mouseClick(HidMouseButton.LEFT) },
                                    onDoubleTap = {
                                        remote.mouseClick(HidMouseButton.LEFT)
                                        remote.mouseClick(HidMouseButton.LEFT)
                                    },
                                    onLongPress = { remote.mouseClick(HidMouseButton.RIGHT) }
                                )
                            }
                            .pointerInput(mouseSettings.sensitivity, mouseSettings.pointerSpeed, mouseSettings.penMode) {
                                detectDragGestures { change, dragAmount ->
                                    change.consume()
                                    remote.mouseMove(dragAmount.x, dragAmount.y)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.TouchApp, contentDescription = null, tint = PrimaryDark, modifier = Modifier.size(72.dp))
                            Spacer(Modifier.height(12.dp))
                            Text("TRACKPAD", color = PrimaryDark, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                            Text("Drag move • Tap left • Long press right", color = OnSurfaceVariant, textAlign = TextAlign.Center)
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Pointer ${mouseSettings.pointerSpeed}% • Sensitivity ${mouseSettings.sensitivity}% • Smooth ${mouseSettings.movementSmoothing}%${if (mouseSettings.penMode) " • Pen" else ""}",
                                color = OnSurfaceVariant,
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .width(58.dp)
                            .fillMaxSize()
                            .background(SurfaceContainerHighest.copy(alpha = 0.45f), RoundedCornerShape(28.dp))
                            .pointerInput(mouseSettings.scrollSpeed, mouseSettings.invertScroll) {
                                detectVerticalDragGestures { change, dragAmount ->
                                    change.consume()
                                    remote.mouseScroll((dragAmount / 6f).toInt())
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("↑", color = PrimaryDark, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(8.dp))
                            Box(
                                modifier = Modifier
                                    .width(8.dp)
                                    .height(90.dp)
                                    .background(PrimaryDark.copy(alpha = 0.32f), CircleShape)
                            )
                            Spacer(Modifier.height(8.dp))
                            Text("↓", color = PrimaryDark, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    PrimaryGlowButton("Left", { remote.mouseClick(HidMouseButton.LEFT) }, Modifier.weight(1f), Icons.Default.Mouse)
                    GhostButton("Middle", { remote.mouseClick(HidMouseButton.MIDDLE) }, Modifier.weight(1f))
                    PrimaryGlowButton("Right", { remote.mouseClick(HidMouseButton.RIGHT) }, Modifier.weight(1f), Icons.Default.Mouse)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    RemotePadButton("Scroll ↑", { remote.mouseScroll(-4) }, Modifier.weight(1f))
                    RemotePadButton("Scroll ↓", { remote.mouseScroll(4) }, Modifier.weight(1f))
                    RemotePadButton("Tab", { remote.tab() }, Modifier.weight(1f), Icons.Default.Keyboard)
                }
                Text(
                    if (connection.state == ConnectionState.CONNECTED) "Ready for input" else "Connect to PC first. Controls are sent only after HID connection is active.",
                    color = OnSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(Modifier.height(72.dp))
        }
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
            NavigationBar(containerColor = com.bluepilot.remote.ui.theme.SurfaceContainer.copy(alpha = 0.92f), tonalElevation = 0.dp) {
                NavigationBarItem(selected = false, onClick = onNavigateToConnection, icon = { Icon(Icons.Default.BluetoothSearching, null) }, label = { Text("Connect") })
                NavigationBarItem(selected = true, onClick = {}, icon = { Icon(Icons.Default.Mouse, null) }, label = { Text("Mouse") })
                NavigationBarItem(selected = false, onClick = onNavigateToKeyboard, icon = { Icon(Icons.Default.Keyboard, null) }, label = { Text("Keyboard") })
                NavigationBarItem(selected = false, onClick = onNavigateToSettings, icon = { Icon(Icons.Default.Settings, null) }, label = { Text("More") })
            }
        }
    }
}
