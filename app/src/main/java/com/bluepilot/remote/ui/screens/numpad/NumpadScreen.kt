@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.bluepilot.remote.ui.screens.numpad

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
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.KeyboardReturn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bluepilot.remote.ui.components.BluePilotBackground
import com.bluepilot.remote.ui.components.GlassCard
import com.bluepilot.remote.ui.components.PrimaryGlowButton
import com.bluepilot.remote.ui.components.RemotePadButton
import com.bluepilot.remote.ui.components.ScreenHeader
import com.bluepilot.remote.ui.components.StatusPill
import com.bluepilot.remote.ui.theme.PrimaryDark
import com.bluepilot.remote.viewmodel.RemoteControlViewModel

@Composable
fun NumpadScreen(
    onNavigateBack: () -> Unit,
    remote: RemoteControlViewModel = hiltViewModel()
) {
    val connection by remote.connectionState.collectAsState()
    BluePilotBackground {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ScreenHeader("Numpad Controller", "Calculator-style numeric input") {
                IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = PrimaryDark) }
            }
            StatusPill(connection.state, connection.state.name.replace('_', ' '), Modifier.align(Alignment.CenterHorizontally))
            GlassCard(Modifier.fillMaxWidth()) {
                val rows = listOf(
                    listOf("Num", "/", "*", "-"),
                    listOf("7", "8", "9", "+"),
                    listOf("4", "5", "6", "Backspace"),
                    listOf("1", "2", "3", "Enter"),
                    listOf("0", "00", ".", "Tab")
                )
                rows.forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        row.forEach { label ->
                            val weight = if (label == "Backspace" || label == "Enter") 1.45f else 1f
                            NumpadKey(label, Modifier.weight(weight), remote)
                        }
                    }
                }
            }
            GlassCard(Modifier.fillMaxWidth()) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    PrimaryGlowButton("Enter", { remote.enter() }, Modifier.weight(1f), Icons.Default.KeyboardReturn)
                    PrimaryGlowButton("Calculator", { remote.keyLabel("Menu") }, Modifier.weight(1f), Icons.Default.Calculate)
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun NumpadKey(label: String, modifier: Modifier, remote: RemoteControlViewModel) {
    val action = {
        when (label) {
            "Num" -> remote.keyLabel("Num")
            "00" -> remote.text("00")
            "Backspace" -> remote.backspace()
            "Enter" -> remote.enter()
            else -> remote.keyLabel(label)
        }
    }
    RemotePadButton(text = label, onClick = action, modifier = modifier.height(60.dp))
}
