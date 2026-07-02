@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.bluepilot.remote.ui.screens.multimedia

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
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
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
fun MultimediaScreen(
    onNavigateBack: () -> Unit,
    remote: RemoteControlViewModel = hiltViewModel()
) {
    val connection by remote.connectionState.collectAsState()
    BluePilotBackground {
        Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            ScreenHeader("Media Controls", "Playback, volume, navigation and touchpad") {
                IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = PrimaryDark) }
            }
            StatusPill(connection.state, connection.state.name.replace('_', ' '), Modifier.align(Alignment.CenterHorizontally))

            GlassCard(Modifier.fillMaxWidth()) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    RemotePadButton("Prev", { remote.mediaPrevious() }, Modifier.weight(1f), Icons.Default.SkipPrevious)
                    RemotePadButton("Rew", { remote.mediaPrevious() }, Modifier.weight(1f), Icons.Default.FastRewind)
                    PrimaryGlowButton("Play", { remote.mediaPlayPause() }, Modifier.weight(1.3f), Icons.Default.PlayArrow)
                    RemotePadButton("Fwd", { remote.mediaNext() }, Modifier.weight(1f), Icons.Default.FastForward)
                    RemotePadButton("Next", { remote.mediaNext() }, Modifier.weight(1f), Icons.Default.SkipNext)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    RemotePadButton("Stop", { remote.mediaStop() }, Modifier.weight(1f), Icons.Default.Stop)
                    RemotePadButton("Mute", { remote.mute() }, Modifier.weight(1f), Icons.Default.VolumeMute)
                    RemotePadButton("Vol -", { remote.volumeDown() }, Modifier.weight(1f), Icons.Default.VolumeDown)
                    RemotePadButton("Vol +", { remote.volumeUp() }, Modifier.weight(1f), Icons.Default.VolumeUp)
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
                        Icon(Icons.Default.TouchApp, contentDescription = null, tint = PrimaryDark, modifier = Modifier.size(58.dp))
                        Spacer(Modifier.height(8.dp))
                        Text("Mini touchpad", color = OnSurfaceVariant)
                    }
                }
            }

            GlassCard(Modifier.fillMaxWidth()) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    RemotePadButton("Back", { remote.keyLabel("Back") }, Modifier.weight(1f), Icons.Default.ArrowBack)
                    RemotePadButton("Home", { remote.keyLabel("Home") }, Modifier.weight(1f), Icons.Default.Home)
                    RemotePadButton("Menu", { remote.keyLabel("Menu") }, Modifier.weight(1f), Icons.Default.Menu)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    RemotePadButton("←", { remote.keyLabel("Left") }, Modifier.weight(1f))
                    RemotePadButton("↑", { remote.keyLabel("Up") }, Modifier.weight(1f))
                    RemotePadButton("↓", { remote.keyLabel("Down") }, Modifier.weight(1f))
                    RemotePadButton("→", { remote.keyLabel("Right") }, Modifier.weight(1f))
                }
            }
        }
    }
}
