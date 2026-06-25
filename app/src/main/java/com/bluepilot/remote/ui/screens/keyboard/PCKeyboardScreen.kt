package com.bluepilot.remote.ui.screens.keyboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.SettingsBluetooth
import androidx.compose.material.icons.filled.SettingsRemote
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bluepilot.remote.ui.theme.*

/**
 * PC Keyboard screen with full QWERTY layout
 * Supports responsive portrait and landscape layouts
 */
@Composable
fun PCKeyboardScreen(
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
                text = "PC Keyboard",
                color = Primary,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }

        // Connection Status Pill
        Surface(
            shape = RoundedCornerShape(999.dp),
            color = SurfaceContainer
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(RoundedCornerShape(999.dp))
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
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Touchpad at top (compact)
        CompactTouchpad(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        )

        // Full keyboard below
        FullKeyboard(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
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
        // Left side: Compact touchpad
        Column(
            modifier = Modifier
                .width(200.dp)
                .fillMaxHeight()
        ) {
            CompactTouchpad(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
        }

        // Right side: Wide desktop-style keyboard
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            WideKeyboard()
        }
    }
}

@Composable
private fun CompactTouchpad(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = PrimaryContainer.copy(alpha = 0.2f)
        ),
        border = BorderStroke(1.dp, Primary.copy(alpha = 0.1f))
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Keyboard,
                contentDescription = "Touchpad",
                tint = Primary.copy(alpha = 0.4f),
                modifier = Modifier.size(48.dp)
            )
        }
    }
}

@Composable
private fun FullKeyboard(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Function row (Esc, F1-F12)
        FunctionRow()

        // Number row
        NumberRow()

        // QWERTY rows
        KeyboardRow(keys = listOf("Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P"))
        KeyboardRow(keys = listOf("A", "S", "D", "F", "G", "H", "J", "K", "L"))
        KeyboardRow(keys = listOf("Z", "X", "C", "V", "B", "N", "M"))

        // Bottom row with modifiers
        ModifierRow()

        // Space and arrows
        SpaceRow()
    }
}

@Composable
private fun WideKeyboard() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Function row across width
        WideFunctionRow()

        // Number row
        NumberRow()

        // QWERTY rows
        KeyboardRow(keys = listOf("Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P"))
        KeyboardRow(keys = listOf("A", "S", "D", "F", "G", "H", "J", "K", "L"))
        KeyboardRow(keys = listOf("Z", "X", "C", "V", "B", "N", "M"))

        // Bottom row with modifiers
        WideModifierRow()

        // Space and arrows
        WideSpaceRow()
    }
}

@Composable
private fun FunctionRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        FunctionKey(text = "Esc", modifier = Modifier.weight(1f))
        FunctionKey(text = "F1", modifier = Modifier.weight(1f))
        FunctionKey(text = "F2", modifier = Modifier.weight(1f))
        FunctionKey(text = "F3", modifier = Modifier.weight(1f))
        FunctionKey(text = "F4", modifier = Modifier.weight(1f))
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        FunctionKey(text = "F5", modifier = Modifier.weight(1f))
        FunctionKey(text = "F6", modifier = Modifier.weight(1f))
        FunctionKey(text = "F7", modifier = Modifier.weight(1f))
        FunctionKey(text = "F8", modifier = Modifier.weight(1f))
        FunctionKey(text = "F9", modifier = Modifier.weight(1f))
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        FunctionKey(text = "F10", modifier = Modifier.weight(1f))
        FunctionKey(text = "F11", modifier = Modifier.weight(1f))
        FunctionKey(text = "F12", modifier = Modifier.weight(1f))
        FunctionKey(text = "Del", modifier = Modifier.weight(1f))
    }
}

@Composable
private fun WideFunctionRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        FunctionKey(text = "Esc", modifier = Modifier.weight(0.5f))
        FunctionKey(text = "F1", modifier = Modifier.weight(1f))
        FunctionKey(text = "F2", modifier = Modifier.weight(1f))
        FunctionKey(text = "F3", modifier = Modifier.weight(1f))
        FunctionKey(text = "F4", modifier = Modifier.weight(1f))
        FunctionKey(text = "F5", modifier = Modifier.weight(1f))
        FunctionKey(text = "F6", modifier = Modifier.weight(1f))
        FunctionKey(text = "F7", modifier = Modifier.weight(1f))
        FunctionKey(text = "F8", modifier = Modifier.weight(1f))
        FunctionKey(text = "F9", modifier = Modifier.weight(1f))
        FunctionKey(text = "F10", modifier = Modifier.weight(1f))
        FunctionKey(text = "F11", modifier = Modifier.weight(1f))
        FunctionKey(text = "F12", modifier = Modifier.weight(1f))
        FunctionKey(text = "Del", modifier = Modifier.weight(0.5f))
    }
}

@Composable
private fun NumberRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        KeyboardKey(text = "1", modifier = Modifier.weight(1f))
        KeyboardKey(text = "2", modifier = Modifier.weight(1f))
        KeyboardKey(text = "3", modifier = Modifier.weight(1f))
        KeyboardKey(text = "4", modifier = Modifier.weight(1f))
        KeyboardKey(text = "5", modifier = Modifier.weight(1f))
        KeyboardKey(text = "6", modifier = Modifier.weight(1f))
        KeyboardKey(text = "7", modifier = Modifier.weight(1f))
        KeyboardKey(text = "8", modifier = Modifier.weight(1f))
        KeyboardKey(text = "9", modifier = Modifier.weight(1f))
        KeyboardKey(text = "0", modifier = Modifier.weight(1f))
    }
}

@Composable
private fun KeyboardRow(
    keys: List<String>
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        keys.forEach { key ->
            KeyboardKey(
                text = key,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ModifierRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        ModifierKey(text = "Ctrl", modifier = Modifier.weight(1.5f))
        ModifierKey(text = "Win", modifier = Modifier.weight(1f))
        ModifierKey(text = "Alt", modifier = Modifier.weight(1f))
        KeyboardKey(text = "Space", modifier = Modifier.weight(3f))
        ModifierKey(text = "Alt", modifier = Modifier.weight(1f))
        ModifierKey(text = "Ctrl", modifier = Modifier.weight(1.5f))
    }
}

@Composable
private fun WideModifierRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        ModifierKey(text = "Ctrl", modifier = Modifier.weight(1f))
        ModifierKey(text = "Win", modifier = Modifier.weight(1f))
        ModifierKey(text = "Alt", modifier = Modifier.weight(1f))
        KeyboardKey(text = "Space", modifier = Modifier.weight(4f))
        ModifierKey(text = "AltGr", modifier = Modifier.weight(1f))
        ModifierKey(text = "Win", modifier = Modifier.weight(1f))
        ModifierKey(text = "Menu", modifier = Modifier.weight(1f))
        ModifierKey(text = "Ctrl", modifier = Modifier.weight(1f))
    }
}

@Composable
private fun SpaceRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        ModifierKey(text = "Shift", modifier = Modifier.weight(1.5f))
        KeyboardKey(text = "Enter", modifier = Modifier.weight(2f))
        ArrowKeys(modifier = Modifier.weight(2f))
    }
}

@Composable
private fun WideSpaceRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        ModifierKey(text = "Shift", modifier = Modifier.weight(1f))
        ModifierKey(text = "Ctrl", modifier = Modifier.weight(1f))
        KeyboardKey(text = "Enter", modifier = Modifier.weight(2f))
        ArrowKeys(modifier = Modifier.weight(2f))
        ModifierKey(text = "Shift", modifier = Modifier.weight(1f))
        ModifierKey(text = "Ctrl", modifier = Modifier.weight(1f))
    }
}

@Composable
private fun KeyboardKey(
    text: String,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = { },
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Primary,
            contentColor = OnPrimary
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun FunctionKey(
    text: String,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = { },
        modifier = modifier.height(40.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = SurfaceContainerHighest,
            contentColor = OnSurface
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
private fun ModifierKey(
    text: String,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = { },
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = SurfaceContainerHighest,
            contentColor = OnSurface
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
private fun ArrowKeys(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        ArrowKey(text = "↑")
        Row(verticalAlignment = Alignment.CenterVertically) {
            ArrowKey(text = "←")
            ArrowKey(text = "↓")
            ArrowKey(text = "→")
        }
    }
}

@Composable
private fun ArrowKey(
    text: String
) {
    Button(
        onClick = { },
        modifier = Modifier.size(32.dp),
        shape = RoundedCornerShape(6.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = SurfaceContainerHighest,
            contentColor = OnSurface
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall
        )
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
                    imageVector = Icons.Default.Keyboard,
                    contentDescription = "Terminal"
                )
            },
            label = { Text("Terminal") }
        )

        NavigationBarItem(
            selected = false,
            onClick = { },
            icon = {
                Icon(
                    imageVector = Icons.Default.SettingsBluetooth,
                    contentDescription = "Macros"
                )
            },
            label = { Text("Macros") }
        )

        NavigationBarItem(
            selected = false,
            onClick = { },
            icon = {
                Icon(
                    imageVector = Icons.Default.SettingsBluetooth,
                    contentDescription = "Settings"
                )
            },
            label = { Text("Settings") }
        )
    }
}
