package com.bluepilot.remote.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Mouse
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VideogameAsset
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bluepilot.remote.model.ThemeMode
import com.bluepilot.remote.ui.theme.*

/**
 * Settings screen with multiple tabs for different settings categories
 * Tabs: General, Mouse, Keyboard, Gamepad, About
 */
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("General", "Mouse", "Keyboard", "Gamepad", "About")

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
                onNavigateBack = onNavigateBack
            )

            // Tab Row
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = SurfaceContainer,
                contentColor = Primary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = Primary
                    )
                }
            ) {
                tabs.forEachIndexed { index, tab ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(tab) }
                    )
                }
            }

            // Tab Content
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                when (selectedTab) {
                    0 -> GeneralSettingsTab()
                    1 -> MouseSettingsTab()
                    2 -> KeyboardSettingsTab()
                    3 -> GamepadSettingsTab()
                    4 -> AboutTab()
                }
            }
        }
    }
}

@Composable
private fun TopAppBar(
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
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Primary
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Settings",
                color = Primary,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun GeneralSettingsTab() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Appearance Section
        SettingsSection(title = "Appearance") {
            var theme by remember { mutableStateOf(ThemeMode.DARK) }
            
            SettingItem(
                title = "Theme",
                subtitle = "Choose your preferred theme",
                onClick = { }
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ThemeChip(
                        text = "Light",
                        selected = theme == ThemeMode.LIGHT,
                        onClick = { theme = ThemeMode.LIGHT }
                    )
                    ThemeChip(
                        text = "Dark",
                        selected = theme == ThemeMode.DARK,
                        onClick = { theme = ThemeMode.DARK }
                    )
                    ThemeChip(
                        text = "System",
                        selected = theme == ThemeMode.SYSTEM,
                        onClick = { theme = ThemeMode.SYSTEM }
                    )
                }
            }

            var fullscreen by remember { mutableStateOf(false) }
            ToggleSetting(
                title = "Fullscreen mode",
                subtitle = "Hide system navigation bar",
                checked = fullscreen,
                onCheckedChange = { fullscreen = it }
            )

            var keepScreenOn by remember { mutableStateOf(true) }
            ToggleSetting(
                title = "Keep screen on",
                subtitle = "Prevent screen from turning off",
                checked = keepScreenOn,
                onCheckedChange = { keepScreenOn = it }
            )
        }

        // Interaction Section
        SettingsSection(title = "Interaction") {
            var touchVibrations by remember { mutableStateOf(true) }
            ToggleSetting(
                title = "Touch vibrations",
                subtitle = "Vibrate on button press",
                checked = touchVibrations,
                onCheckedChange = { touchVibrations = it }
            )

            var showAndroidNav by remember { mutableStateOf(true) }
            ToggleSetting(
                title = "Show Android navigation",
                subtitle = "Display navigation buttons",
                checked = showAndroidNav,
                onCheckedChange = { showAndroidNav = it }
            )
        }

        // Shortcuts Section
        SettingsSection(title = "Shortcuts") {
            var showMediaButtons by remember { mutableStateOf(true) }
            ToggleSetting(
                title = "Show media buttons",
                subtitle = "Display media control shortcuts",
                checked = showMediaButtons,
                onCheckedChange = { showMediaButtons = it }
            )

            var showShortcuts by remember { mutableStateOf(true) }
            ToggleSetting(
                title = "Show shortcuts",
                subtitle = "Display quick action buttons",
                checked = showShortcuts,
                onCheckedChange = { showShortcuts = it }
            )
        }
    }
}

@Composable
private fun MouseSettingsTab() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SettingsSection(title = "Mouse Settings") {
            var airMouse by remember { mutableStateOf(false) }
            ToggleSetting(
                title = "Air mouse",
                subtitle = "Use gyroscope for cursor movement",
                checked = airMouse,
                onCheckedChange = { airMouse = it }
            )

            SliderSetting(
                title = "Sensitivity",
                subtitle = "Cursor movement speed",
                value = 65f,
                onValueChange = { },
                valueRange = 0f..100f
            )

            SliderSetting(
                title = "Pointer speed",
                subtitle = "DPI adjustment",
                value = 45f,
                onValueChange = { },
                valueRange = 0f..100f
            )

            SliderSetting(
                title = "Scroll speed",
                subtitle = "Scroll wheel sensitivity",
                value = 50f,
                onValueChange = { },
                valueRange = 0f..100f
            )

            var invertScroll by remember { mutableStateOf(true) }
            ToggleSetting(
                title = "Invert scroll",
                subtitle = "Reverse scroll direction",
                checked = invertScroll,
                onCheckedChange = { invertScroll = it }
            )

            var mouseJiggler by remember { mutableStateOf(false) }
            ToggleSetting(
                title = "Mouse jiggler",
                subtitle = "Prevent screen sleep with movement",
                checked = mouseJiggler,
                onCheckedChange = { mouseJiggler = it }
            )

            var penMode by remember { mutableStateOf(false) }
            ToggleSetting(
                title = "Pen mode",
                subtitle = "Use stylus for precise control",
                checked = penMode,
                onCheckedChange = { penMode = it }
            )
        }
    }
}

@Composable
private fun KeyboardSettingsTab() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SettingsSection(title = "Keyboard Settings") {
            var language by remember { mutableStateOf("en_US") }
            
            SettingItem(
                title = "Language",
                subtitle = "Keyboard layout language",
                onClick = { }
            ) {
                Text(
                    text = language,
                    color = Primary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            var showTextInputBar by remember { mutableStateOf(true) }
            ToggleSetting(
                title = "Show text input bar",
                subtitle = "Display text input field",
                checked = showTextInputBar,
                onCheckedChange = { showTextInputBar = it }
            )

            var autoHideKeyboard by remember { mutableStateOf(false) }
            ToggleSetting(
                title = "Auto-hide keyboard",
                subtitle = "Hide keyboard when not in use",
                checked = autoHideKeyboard,
                onCheckedChange = { autoHideKeyboard = it }
            )
        }
    }
}

@Composable
private fun GamepadSettingsTab() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SettingsSection(title = "Gamepad Settings") {
            var mode by remember { mutableStateOf("Keyboard Fallback") }
            
            SettingItem(
                title = "Gamepad mode",
                subtitle = "Control mapping mode",
                onClick = { }
            ) {
                Text(
                    text = mode,
                    color = Primary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            SliderSetting(
                title = "Joystick sensitivity",
                subtitle = "Analog stick response",
                value = 70f,
                onValueChange = { },
                valueRange = 0f..100f
            )

            SliderSetting(
                title = "Dead zone",
                subtitle = "Stick movement threshold",
                value = 10f,
                onValueChange = { },
                valueRange = 0f..50f
            )

            SliderSetting(
                title = "Button opacity",
                subtitle = "Control transparency",
                value = 80f,
                onValueChange = { },
                valueRange = 0f..100f
            )

            SliderSetting(
                title = "Turbo speed",
                subtitle = "Rapid-fire button speed",
                value = 50f,
                onValueChange = { },
                valueRange = 0f..100f
            )

            var lockLayout by remember { mutableStateOf(true) }
            ToggleSetting(
                title = "Lock layout while playing",
                subtitle = "Prevent accidental layout changes",
                checked = lockLayout,
                onCheckedChange = { lockLayout = it }
            )

            var hapticFeedback by remember { mutableStateOf(true) }
            ToggleSetting(
                title = "Haptic feedback",
                subtitle = "Vibrate on button press",
                checked = hapticFeedback,
                onCheckedChange = { hapticFeedback = it }
            )
        }
    }
}

@Composable
private fun AboutTab() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App Info Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = SurfaceContainerLow
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(PrimaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "App Icon",
                        tint = OnPrimaryContainer,
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "BluePilot Remote",
                    color = OnSurface,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Version 2.4.0",
                    color = OnSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "A versatile Bluetooth HID remote control for Android devices.",
                    color = OnSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }
        }

        SettingsSection(title = "Information") {
            SettingItem(
                title = "License",
                subtitle = "Open Source License",
                onClick = { }
            ) {
                Text(
                    text = "MIT",
                    color = Primary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            SettingItem(
                title = "Privacy Policy",
                subtitle = "View our privacy policy",
                onClick = { }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null,
                    tint = OnSurfaceVariant
                )
            }

            SettingItem(
                title = "Terms of Service",
                subtitle = "View terms of service",
                onClick = { }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null,
                    tint = OnSurfaceVariant
                )
            }

            SettingItem(
                title = "Source Code",
                subtitle = "View on GitHub",
                onClick = { }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null,
                    tint = OnSurfaceVariant
                )
            }
        }

        SettingsSection(title = "Support") {
            SettingItem(
                title = "Report a bug",
                subtitle = "Submit bug report",
                onClick = { }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null,
                    tint = OnSurfaceVariant
                )
            }

            SettingItem(
                title = "Contact support",
                subtitle = "Get help from our team",
                onClick = { }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null,
                    tint = OnSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            color = Primary,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Medium
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = SurfaceContainerLow
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                content()
            }
        }
    }
}

@Composable
private fun SettingItem(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    trailingContent: @Composable () -> Unit = {}
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    color = OnSurface,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = subtitle,
                    color = OnSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            trailingContent()
        }
    }
}

@Composable
private fun ToggleSetting(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                color = OnSurface,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = subtitle,
                color = OnSurfaceVariant,
                style = MaterialTheme.typography.bodySmall
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Primary,
                checkedTrackColor = PrimaryContainer,
                uncheckedThumbColor = OnSurfaceVariant,
                uncheckedTrackColor = SurfaceContainer
            )
        )
    }
}

@Composable
private fun SliderSetting(
    title: String,
    subtitle: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float> = 0f..100f
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = title,
                    color = OnSurface,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = subtitle,
                    color = OnSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Text(
                text = "${value.toInt()}%",
                color = Primary,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            colors = SliderDefaults.colors(
                activeTrackColor = Primary,
                thumbColor = Primary,
                inactiveTrackColor = SurfaceContainer
            )
        )
    }
}

@Composable
private fun ThemeChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(text) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = PrimaryContainer,
            selectedLabelColor = OnPrimaryContainer
        )
    )
}
