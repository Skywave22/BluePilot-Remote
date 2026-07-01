@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.bluepilot.remote.ui.screens.numpad

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.KeyboardTab
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.SettingsBluetooth
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SettingsRemote
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bluepilot.remote.ui.theme.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.bluepilot.remote.viewmodel.RemoteControlViewModel
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.platform.LocalConfiguration

/**
 * Numpad screen with numeric keypad
 * Supports responsive portrait and landscape layouts
 */
@Composable
fun NumpadScreen(
    onNavigateBack: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.screenWidthDp > configuration.screenHeightDp

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top App Bar
            TopAppBar(
                onMenuClick = { },
                onNavigateBack = onNavigateBack
            )

            if (isLandscape) {
                LandscapeContent()
            } else {
                PortraitContent()
            }
        }

        // Bottom Navigation Bar
        BottomNavigationBar()
    }
}

@Composable
private fun TopAppBar(
    onMenuClick: () -> Unit,
    onNavigateBack: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu",
                    tint = Primary
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Numpad",
                color = Primary,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }

        // Connection Status Pill
        Surface(
            shape = CircleShape,
            color = SurfaceContainer
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF10B981))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Connected",
                    color = OnSurfaceVariant,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}

@Composable
private fun PortraitContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Numpad Grid
        NumpadGrid(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(4f / 5f)
        )

        // Quick Shortcuts
        QuickShortcuts(
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun LandscapeContent() {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Left side: Numpad
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            NumpadGrid(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(4f / 5f)
            )
        }

        // Right side: Quick shortcuts
        Column(
            modifier = Modifier
                .width(200.dp)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            QuickShortcutCard(
                icon = Icons.Default.Calculate,
                title = "Active Application",
                subtitle = "Calculator.exe",
                modifier = Modifier.fillMaxWidth()
            )
            QuickShortcutCard(
                icon = Icons.Default.KeyboardTab,
                title = "Focus Element",
                subtitle = "Input Field 1",
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun NumpadGrid(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceContainerLow
        ),
        border = BorderStroke(1.dp, OutlineVariant.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Row 1: NUM, /, *, Backspace
            NumpadRow(
                keys = listOf("NUM", "/", "*", "⌫"),
                modifiers = listOf(1f, 1f, 1f, 1f),
                specialKeys = listOf(false, false, false, true)
            )

            // Row 2: 7, 8, 9, -
            NumpadRow(
                keys = listOf("7", "8", "9", "-"),
                modifiers = listOf(1f, 1f, 1f, 1f)
            )

            // Row 3: 4, 5, 6, +
            NumpadRow(
                keys = listOf("4", "5", "6", "+"),
                modifiers = listOf(1f, 1f, 1f, 1f)
            )

            // Row 4 + 5: 1, 2, 3, Enter (tall)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 1, 2, 3
                Column(
                    modifier = Modifier.weight(3f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    NumpadRow(
                        keys = listOf("1", "2", "3"),
                        modifiers = listOf(1f, 1f, 1f)
                    )
                    // 0, =, .
                    NumpadRow(
                        keys = listOf("0", "=", "."),
                        modifiers = listOf(1f, 1f, 1f)
                    )
                }

                // Enter (tall)
                NumpadKey(
                    text = "Enter",
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    isPrimary = true
                )
            }
        }
    }
}

@Composable
private fun NumpadRow(
    keys: List<String>,
    modifiers: List<Float>,
    specialKeys: List<Boolean> = List(keys.size) { false }
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        keys.forEachIndexed { index, key ->
            NumpadKey(
                text = key,
                modifier = Modifier.weight(modifiers[index]),
                isSpecial = specialKeys[index]
            )
        }
    }
}

@Composable
private fun NumpadKey(
    text: String,
    modifier: Modifier = Modifier,
    isPrimary: Boolean = false,
    isSpecial: Boolean = false
) {
    val remote: RemoteControlViewModel = hiltViewModel()
    Button(
        onClick = { remote.keyLabel(text) },
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isPrimary) SecondaryContainer else if (isSpecial) SurfaceContainerHighest else Primary,
            contentColor = if (isPrimary) OnSecondaryContainer else if (isSpecial) OnSurface else OnPrimary
        ),
        border = if (!isPrimary && !isSpecial) BorderStroke(1.dp, OutlineVariant.copy(alpha = 0.3f)) else null
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun QuickShortcuts(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        QuickShortcutCard(
            icon = Icons.Default.Calculate,
            title = "Active Application",
            subtitle = "Calculator.exe",
            modifier = Modifier.weight(1f)
        )
        QuickShortcutCard(
            icon = Icons.Default.KeyboardTab,
            title = "Focus Element",
            subtitle = "Input Field 1",
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun QuickShortcutCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    val remote: RemoteControlViewModel = hiltViewModel()
    Card(
        onClick = { remote.keyLabel(title) },
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceContainer
        ),
        border = BorderStroke(1.dp, OutlineVariant.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SecondaryContainer.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = title,
                    color = OnSurfaceVariant,
                    style = MaterialTheme.typography.labelMedium
                )
                Text(
                    text = subtitle,
                    color = OnSurface,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun BottomNavigationBar() {
    NavigationBar(
        modifier = Modifier.fillMaxWidth(),
        containerColor = SurfaceContainer,
        tonalElevation = 0.dp
    ) {
        NavigationBarItem(
            selected = false,
            onClick = { },
            icon = {
                Icon(
                    imageVector = Icons.Default.SettingsRemote,
                    contentDescription = "Controls"
                )
            },
            label = { Text("Controls") }
        )

        NavigationBarItem(
            selected = true,
            onClick = { },
            icon = {
                Icon(
                    imageVector = Icons.Default.SettingsBluetooth,
                    contentDescription = "Keyboard"
                )
            },
            label = { Text("Keyboard") }
        )

        NavigationBarItem(
            selected = false,
            onClick = { },
            icon = {
                Icon(
                    imageVector = Icons.Default.SettingsBluetooth,
                    contentDescription = "Gamepad"
                )
            },
            label = { Text("Gamepad") }
        )

        NavigationBarItem(
            selected = false,
            onClick = { },
            icon = {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings"
                )
            },
            label = { Text("Settings") }
        )
    }
}
