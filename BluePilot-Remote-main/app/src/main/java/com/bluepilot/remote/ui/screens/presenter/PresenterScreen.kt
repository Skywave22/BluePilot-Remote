@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.bluepilot.remote.ui.screens.presenter

import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bluepilot.remote.ui.components.BluePilotBackground
import com.bluepilot.remote.ui.components.GlassCard
import com.bluepilot.remote.ui.components.PrimaryGlowButton
import com.bluepilot.remote.ui.components.RemotePadButton
import com.bluepilot.remote.ui.components.ScreenHeader
import com.bluepilot.remote.ui.components.StatusPill
import com.bluepilot.remote.ui.theme.OnSurfaceVariant
import com.bluepilot.remote.ui.theme.PrimaryDark
import com.bluepilot.remote.viewmodel.RemoteControlViewModel

@Composable
fun PresenterScreen(
    onNavigateBack: () -> Unit,
    remote: RemoteControlViewModel = hiltViewModel()
) {
    val connection by remote.connectionState.collectAsState()
    BluePilotBackground {
        Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            ScreenHeader("Presenter Mode", "Slides and pointer controls") {
                IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = PrimaryDark) }
            }
            StatusPill(connection.state, connection.state.name.replace('_', ' '), Modifier.align(Alignment.CenterHorizontally))

            Row(horizontalArrangement = Arrangement.spacedBy(14.dp), modifier = Modifier.fillMaxWidth()) {
                PrimaryGlowButton("Previous", { remote.keyLabel("Left") }, Modifier.weight(1f).height(96.dp), Icons.Default.ArrowBack)
                PrimaryGlowButton("Next", { remote.keyLabel("Right") }, Modifier.weight(1f).height(96.dp), Icons.Default.ArrowForward)
            }

            GlassCard(Modifier.fillMaxWidth()) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    RemotePadButton("Start", { remote.presentationStart() }, Modifier.weight(1f), Icons.Default.Fullscreen)
                    RemotePadButton("Esc", { remote.escape() }, Modifier.weight(1f))
                    RemotePadButton("Blank", { remote.presentationBlackScreen() }, Modifier.weight(1f), Icons.Default.Pause)
                }
            }

            GlassCard(Modifier.fillMaxWidth().weight(1f)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                remote.mouseMove(dragAmount.x, dragAmount.y)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.TouchApp, null, tint = PrimaryDark, modifier = Modifier.size(72.dp))
                        Spacer(Modifier.height(8.dp))
                        Text("Presentation touchpad", color = OnSurfaceVariant, fontWeight = FontWeight.SemiBold)
                        Text("Drag pointer • use slide buttons above", color = OnSurfaceVariant)
                    }
                }
            }
        }
    }
}
