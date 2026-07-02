@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.bluepilot.remote.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bluepilot.remote.model.GamepadMappingMode
import com.bluepilot.remote.model.ThemeMode
import com.bluepilot.remote.ui.components.BluePilotBackground
import com.bluepilot.remote.ui.components.ScreenHeader
import com.bluepilot.remote.ui.theme.OnSurface
import com.bluepilot.remote.ui.theme.OnSurfaceVariant
import com.bluepilot.remote.ui.theme.Primary
import com.bluepilot.remote.ui.theme.PrimaryDark
import com.bluepilot.remote.ui.theme.SurfaceContainer
import com.bluepilot.remote.ui.theme.SurfaceContainerLow
import com.bluepilot.remote.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("General", "Mouse", "Keyboard", "Gamepad", "About")
    val appSettings by viewModel.appSettings.collectAsState()
    val mouseSettings by viewModel.mouseSettings.collectAsState()
    val keyboardSettings by viewModel.keyboardSettings.collectAsState()
    val gamepadSettings by viewModel.gamepadSettings.collectAsState()

    BluePilotBackground {
        Column(Modifier.fillMaxSize().padding(top = 18.dp)) {
            ScreenHeader("Settings", "BluePilot 2.0 control preferences", modifier = Modifier.padding(horizontal = 20.dp)) {
                IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = PrimaryDark) }
            }
            Spacer(Modifier.height(12.dp))
            ScrollableTabRow(selectedTabIndex = selectedTab, containerColor = SurfaceContainer.copy(alpha = 0.86f), contentColor = Primary, edgePadding = 8.dp) {
                tabs.forEachIndexed { index, tab -> Tab(selected = selectedTab == index, onClick = { selectedTab = index }, text = { Text(tab) }) }
            }
            Box(Modifier.fillMaxSize().padding(16.dp)) {
                when (selectedTab) {
                    0 -> GeneralSettingsTab(
                        theme = appSettings.theme,
                        onThemeChange = viewModel::setTheme,
                        fullscreen = appSettings.fullscreenMode,
                        onFullscreenChange = viewModel::setFullscreen,
                        keepScreenOn = appSettings.keepScreenOn,
                        onKeepScreenOnChange = viewModel::setKeepScreenOn,
                        touchVibrations = appSettings.touchVibrations,
                        onTouchVibrationsChange = viewModel::setTouchVibrations,
                        showAndroidNav = appSettings.showAndroidNavigation,
                        onShowAndroidNavChange = viewModel::setShowAndroidNavigation,
                        secureScreen = appSettings.secureScreen,
                        onSecureScreenChange = viewModel::setSecureScreen
                    )
                    1 -> MouseSettingsTab(
                        sensitivity = mouseSettings.sensitivity,
                        onSensitivityChange = viewModel::setSensitivity,
                        pointerSpeed = mouseSettings.pointerSpeed,
                        onPointerSpeedChange = viewModel::setPointerSpeed,
                        scrollSpeed = mouseSettings.scrollSpeed,
                        onScrollSpeedChange = viewModel::setScrollSpeed,
                        movementSmoothing = mouseSettings.movementSmoothing,
                        onMovementSmoothingChange = viewModel::setMovementSmoothing,
                        invertScroll = mouseSettings.invertScroll,
                        onInvertScrollChange = viewModel::setInvertScroll,
                        tapToClick = mouseSettings.tapToClick,
                        onTapToClickChange = viewModel::setTapToClick,
                        penMode = mouseSettings.penMode,
                        onPenModeChange = viewModel::setPenMode
                    )
                    2 -> KeyboardSettingsTab(
                        showTextInputBar = keyboardSettings.showTextInputBar,
                        onShowTextInputBarChange = viewModel::setShowTextInputBar
                    )
                    3 -> GamepadSettingsTab(
                        mode = gamepadSettings.gamepadMode,
                        onModeChange = viewModel::setGamepadMode,
                        joystickSensitivity = gamepadSettings.joystickSensitivity,
                        onJoystickSensitivityChange = viewModel::setJoystickSensitivity,
                        deadZone = gamepadSettings.deadZone,
                        onDeadZoneChange = viewModel::setDeadZone,
                        hapticFeedback = gamepadSettings.hapticFeedback,
                        onHapticFeedbackChange = viewModel::setGamepadHapticFeedback
                    )
                    4 -> AboutTab()
                }
            }
        }
    }
}

@Composable
private fun GeneralSettingsTab(
    theme: ThemeMode,
    onThemeChange: (ThemeMode) -> Unit,
    fullscreen: Boolean,
    onFullscreenChange: (Boolean) -> Unit,
    keepScreenOn: Boolean,
    onKeepScreenOnChange: (Boolean) -> Unit,
    touchVibrations: Boolean,
    onTouchVibrationsChange: (Boolean) -> Unit,
    showAndroidNav: Boolean,
    onShowAndroidNavChange: (Boolean) -> Unit,
    secureScreen: Boolean,
    onSecureScreenChange: (Boolean) -> Unit
) = SettingsColumn {
    SettingsSection("Appearance") {
        Text("Theme", color = OnSurface, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(8.dp))
        Row(Modifier.padding(horizontal = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ThemeChip("Light", theme == ThemeMode.LIGHT) { onThemeChange(ThemeMode.LIGHT) }
            ThemeChip("Dark", theme == ThemeMode.DARK) { onThemeChange(ThemeMode.DARK) }
            ThemeChip("System", theme == ThemeMode.SYSTEM) { onThemeChange(ThemeMode.SYSTEM) }
        }
        ToggleSetting("Fullscreen mode", "Use immersive control surface", fullscreen, onFullscreenChange)
        ToggleSetting("Keep screen on", "Prevent screen sleep while using remote", keepScreenOn, onKeepScreenOnChange)
        ToggleSetting("Show Android navigation", "Show system navigation bars", showAndroidNav, onShowAndroidNavChange)
        ToggleSetting("Secure screen", "Block screenshots and app switcher preview", secureScreen, onSecureScreenChange)
    }
    SettingsSection("Interaction") {
        ToggleSetting("Touch vibrations", "Vibrate on button press", touchVibrations, onTouchVibrationsChange)
    }
}

@Composable
private fun MouseSettingsTab(
    sensitivity: Int,
    onSensitivityChange: (Int) -> Unit,
    pointerSpeed: Int,
    onPointerSpeedChange: (Int) -> Unit,
    scrollSpeed: Int,
    onScrollSpeedChange: (Int) -> Unit,
    movementSmoothing: Int,
    onMovementSmoothingChange: (Int) -> Unit,
    invertScroll: Boolean,
    onInvertScrollChange: (Boolean) -> Unit,
    tapToClick: Boolean,
    onTapToClickChange: (Boolean) -> Unit,
    penMode: Boolean,
    onPenModeChange: (Boolean) -> Unit
) = SettingsColumn {
    SettingsSection("Mouse Trackpad") {
        SliderSetting("Sensitivity", "Cursor movement speed", sensitivity.toFloat(), { onSensitivityChange(it.toInt()) })
        SliderSetting("Pointer speed", "Pointer acceleration", pointerSpeed.toFloat(), { onPointerSpeedChange(it.toInt()) })
        SliderSetting("Scroll speed", "Scroll strip and button speed", scrollSpeed.toFloat(), { onScrollSpeedChange(it.toInt()) })
        SliderSetting("Movement smoothing", "Reduce jitter for smoother cursor motion", movementSmoothing.toFloat(), { onMovementSmoothingChange(it.toInt()) })
        ToggleSetting("Invert scroll", "Reverse scroll direction", invertScroll, onInvertScrollChange)
        ToggleSetting("Tap to click", "Tap trackpad for left click", tapToClick, onTapToClickChange)
        ToggleSetting("Pen mode", "Precise low-speed pointer movement", penMode, onPenModeChange)
    }
}

@Composable
private fun KeyboardSettingsTab(showTextInputBar: Boolean, onShowTextInputBarChange: (Boolean) -> Unit) = SettingsColumn {
    SettingsSection("Keyboard") {
        ToggleSetting("Show text input bar", "Display text beam field on keyboard screen", showTextInputBar, onShowTextInputBarChange)
    }
}

@Composable
private fun GamepadSettingsTab(
    mode: GamepadMappingMode,
    onModeChange: (GamepadMappingMode) -> Unit,
    joystickSensitivity: Int,
    onJoystickSensitivityChange: (Int) -> Unit,
    deadZone: Int,
    onDeadZoneChange: (Int) -> Unit,
    hapticFeedback: Boolean,
    onHapticFeedbackChange: (Boolean) -> Unit
) = SettingsColumn {
    SettingsSection("Gamepad") {
        ChoiceSetting("Mode", "HID gamepad or fallback mapping", mode.name, GamepadMappingMode.entries.map { it.name }) { selected -> onModeChange(GamepadMappingMode.valueOf(selected)) }
        SliderSetting("Joystick sensitivity", "Analog stick response", joystickSensitivity.toFloat(), { onJoystickSensitivityChange(it.toInt()) })
        SliderSetting("Dead zone", "Ignore tiny stick movement", deadZone.toFloat(), { onDeadZoneChange(it.toInt()) }, 0f..50f)
        ToggleSetting("Haptic feedback", "Vibrate on gamepad button press", hapticFeedback, onHapticFeedbackChange)
    }
}

@Composable
private fun AboutTab() = SettingsColumn {
    val uriHandler = LocalUriHandler.current
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow)) {
        Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Bluetooth, contentDescription = "BluePilot", tint = Primary, modifier = Modifier.height(56.dp))
            Spacer(Modifier.height(12.dp))
            Text("BluePilot Remote", color = OnSurface, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("Version 2.0.0", color = OnSurfaceVariant)
            Spacer(Modifier.height(12.dp))
            Text("Premium Bluetooth HID remote for mouse, keyboard, media, presenter and gamepad control.", color = OnSurfaceVariant, textAlign = TextAlign.Center)
        }
    }
    SettingsSection("Information") {
        InfoRow("License", "MIT")
        SettingItem("Source Code", "Open GitHub repository", { uriHandler.openUri("https://github.com/Skywave22/BluePilot-Remote") })
        InfoRow("Privacy", "No account, no cloud server, direct Bluetooth control")
        InfoRow("Safety", "Use only with devices you own or have permission to control")
    }
}

@Composable
private fun SettingsColumn(content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(16.dp), content = content)
}

@Composable
private fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(title, color = Primary, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium)
        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow)) {
            Column(Modifier.padding(12.dp), content = content)
        }
    }
}

@Composable
private fun SettingItem(title: String, subtitle: String, onClick: () -> Unit, trailing: @Composable () -> Unit = {}) {
    Surface(onClick = onClick, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), color = SurfaceContainerLow) {
        Row(Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(title, color = OnSurface, style = MaterialTheme.typography.bodyLarge)
                Text(subtitle, color = OnSurfaceVariant, style = MaterialTheme.typography.bodySmall)
            }
            trailing()
        }
    }
}

@Composable
private fun InfoRow(title: String, value: String) = SettingItem(title, value, {})

@Composable
private fun ToggleSetting(title: String, subtitle: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(title, color = OnSurface, style = MaterialTheme.typography.bodyLarge)
            Text(subtitle, color = OnSurfaceVariant, style = MaterialTheme.typography.bodySmall)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun SliderSetting(title: String, subtitle: String, value: Float, onValueChange: (Float) -> Unit, valueRange: ClosedFloatingPointRange<Float> = 0f..100f) {
    Column(Modifier.fillMaxWidth().padding(12.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(Modifier.weight(1f)) {
                Text(title, color = OnSurface, style = MaterialTheme.typography.bodyLarge)
                Text(subtitle, color = OnSurfaceVariant, style = MaterialTheme.typography.bodySmall)
            }
            Text("${value.toInt()}%", color = Primary)
        }
        Slider(value = value.coerceIn(valueRange.start, valueRange.endInclusive), onValueChange = onValueChange, valueRange = valueRange)
    }
}

@Composable
private fun ThemeChip(text: String, selected: Boolean, onClick: () -> Unit) {
    FilterChip(selected = selected, onClick = onClick, label = { Text(text) })
}

@Composable
private fun ChoiceSetting(title: String, subtitle: String, value: String, choices: List<String>, onSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    SettingItem(title, subtitle, { expanded = true }) {
        Box {
            TextButton(onClick = { expanded = true }) { Text(value.replace('_', ' ')) }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                choices.forEach { choice ->
                    DropdownMenuItem(text = { Text(choice.replace('_', ' ')) }, onClick = { expanded = false; onSelected(choice) })
                }
            }
        }
    }
}
