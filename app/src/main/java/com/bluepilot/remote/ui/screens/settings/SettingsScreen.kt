@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.bluepilot.remote.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bluepilot.remote.model.GamepadMappingMode
import com.bluepilot.remote.model.ThemeMode
import com.bluepilot.remote.ui.theme.*
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

    Column(
        modifier = Modifier.fillMaxSize().background(Background)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Primary)
            }
            Spacer(Modifier.width(8.dp))
            Text("Settings", color = Primary, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        }

        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = SurfaceContainer,
            contentColor = Primary,
            edgePadding = 8.dp
        ) {
            tabs.forEachIndexed { index, tab ->
                Tab(selected = selectedTab == index, onClick = { selectedTab = index }, text = { Text(tab) })
            }
        }

        Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
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
                    showMediaButtons = appSettings.showMediaButtons,
                    onShowMediaButtonsChange = viewModel::setShowMediaButtons,
                    showShortcuts = appSettings.showShortcuts,
                    onShowShortcutsChange = viewModel::setShowShortcuts
                )
                1 -> MouseSettingsTab(
                    airMouse = mouseSettings.airMouseEnabled,
                    onAirMouseChange = viewModel::setAirMouseEnabled,
                    sensitivity = mouseSettings.sensitivity,
                    onSensitivityChange = viewModel::setSensitivity,
                    pointerSpeed = mouseSettings.pointerSpeed,
                    onPointerSpeedChange = viewModel::setPointerSpeed,
                    scrollSpeed = mouseSettings.scrollSpeed,
                    onScrollSpeedChange = viewModel::setScrollSpeed,
                    invertScroll = mouseSettings.invertScroll,
                    onInvertScrollChange = viewModel::setInvertScroll,
                    mouseJiggler = mouseSettings.mouseJiggler,
                    onMouseJigglerChange = viewModel::setMouseJiggler,
                    penMode = mouseSettings.penMode,
                    onPenModeChange = viewModel::setPenMode
                )
                2 -> KeyboardSettingsTab(
                    language = keyboardSettings.language,
                    onLanguageChange = viewModel::setKeyboardLanguage,
                    showTextInputBar = keyboardSettings.showTextInputBar,
                    onShowTextInputBarChange = viewModel::setShowTextInputBar,
                    autoHideKeyboard = keyboardSettings.autoHideKeyboard,
                    onAutoHideKeyboardChange = viewModel::setAutoHideKeyboard
                )
                3 -> GamepadSettingsTab(
                    mode = gamepadSettings.gamepadMode,
                    onModeChange = viewModel::setGamepadMode,
                    joystickSensitivity = gamepadSettings.joystickSensitivity,
                    onJoystickSensitivityChange = viewModel::setJoystickSensitivity,
                    deadZone = gamepadSettings.deadZone,
                    onDeadZoneChange = viewModel::setDeadZone,
                    buttonOpacity = gamepadSettings.buttonOpacity,
                    onButtonOpacityChange = viewModel::setButtonOpacity,
                    turboSpeed = gamepadSettings.turboSpeed,
                    onTurboSpeedChange = viewModel::setTurboSpeed,
                    lockLayout = gamepadSettings.lockLayoutWhilePlaying,
                    onLockLayoutChange = viewModel::setLockLayoutWhilePlaying,
                    hapticFeedback = gamepadSettings.hapticFeedback,
                    onHapticFeedbackChange = viewModel::setGamepadHapticFeedback
                )
                4 -> AboutTab()
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
    showMediaButtons: Boolean,
    onShowMediaButtonsChange: (Boolean) -> Unit,
    showShortcuts: Boolean,
    onShowShortcutsChange: (Boolean) -> Unit
) = SettingsColumn {
    SettingsSection("Appearance") {
        Text("Theme", color = OnSurface, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(8.dp))
        Row(Modifier.padding(horizontal = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ThemeChip("Light", theme == ThemeMode.LIGHT) { onThemeChange(ThemeMode.LIGHT) }
            ThemeChip("Dark", theme == ThemeMode.DARK) { onThemeChange(ThemeMode.DARK) }
            ThemeChip("System", theme == ThemeMode.SYSTEM) { onThemeChange(ThemeMode.SYSTEM) }
        }
        ToggleSetting("Fullscreen mode", "Saved preference for fullscreen UI", fullscreen, onFullscreenChange)
        ToggleSetting("Keep screen on", "Prevent phone screen sleeping while using remote", keepScreenOn, onKeepScreenOnChange)
    }
    SettingsSection("Interaction") {
        ToggleSetting("Touch vibrations", "Vibrate on button press", touchVibrations, onTouchVibrationsChange)
        ToggleSetting("Show Android navigation", "Save navigation bar preference", showAndroidNav, onShowAndroidNavChange)
        ToggleSetting("Show media buttons", "Show media shortcuts in supported screens", showMediaButtons, onShowMediaButtonsChange)
        ToggleSetting("Show shortcuts", "Show quick action shortcut buttons", showShortcuts, onShowShortcutsChange)
    }
}

@Composable
private fun MouseSettingsTab(
    airMouse: Boolean,
    onAirMouseChange: (Boolean) -> Unit,
    sensitivity: Int,
    onSensitivityChange: (Int) -> Unit,
    pointerSpeed: Int,
    onPointerSpeedChange: (Int) -> Unit,
    scrollSpeed: Int,
    onScrollSpeedChange: (Int) -> Unit,
    invertScroll: Boolean,
    onInvertScrollChange: (Boolean) -> Unit,
    mouseJiggler: Boolean,
    onMouseJigglerChange: (Boolean) -> Unit,
    penMode: Boolean,
    onPenModeChange: (Boolean) -> Unit
) = SettingsColumn {
    SettingsSection("Mouse Settings") {
        ToggleSetting("Air mouse", "Save gyroscope mouse preference", airMouse, onAirMouseChange)
        SliderSetting("Sensitivity", "Cursor movement speed", sensitivity.toFloat(), { onSensitivityChange(it.toInt()) })
        SliderSetting("Pointer speed", "Pointer DPI multiplier", pointerSpeed.toFloat(), { onPointerSpeedChange(it.toInt()) })
        SliderSetting("Scroll speed", "Scroll wheel sensitivity", scrollSpeed.toFloat(), { onScrollSpeedChange(it.toInt()) })
        ToggleSetting("Invert scroll", "Reverse scroll direction", invertScroll, onInvertScrollChange)
        ToggleSetting("Mouse jiggler", "Save anti-sleep movement preference", mouseJiggler, onMouseJigglerChange)
        ToggleSetting("Pen mode", "Save precise stylus mode preference", penMode, onPenModeChange)
    }
}

@Composable
private fun KeyboardSettingsTab(
    language: String,
    onLanguageChange: (String) -> Unit,
    showTextInputBar: Boolean,
    onShowTextInputBarChange: (Boolean) -> Unit,
    autoHideKeyboard: Boolean,
    onAutoHideKeyboardChange: (Boolean) -> Unit
) = SettingsColumn {
    SettingsSection("Keyboard Settings") {
        ChoiceSetting("Language", "Keyboard layout language", language, listOf("en_US", "en_GB", "de_DE", "fr_FR", "es_ES"), onLanguageChange)
        ToggleSetting("Show text input bar", "Display text input field", showTextInputBar, onShowTextInputBarChange)
        ToggleSetting("Auto-hide keyboard", "Hide keyboard when not in use", autoHideKeyboard, onAutoHideKeyboardChange)
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
    buttonOpacity: Float,
    onButtonOpacityChange: (Float) -> Unit,
    turboSpeed: Int,
    onTurboSpeedChange: (Int) -> Unit,
    lockLayout: Boolean,
    onLockLayoutChange: (Boolean) -> Unit,
    hapticFeedback: Boolean,
    onHapticFeedbackChange: (Boolean) -> Unit
) = SettingsColumn {
    SettingsSection("Gamepad Settings") {
        ChoiceSetting(
            "Gamepad mode",
            "Control mapping mode",
            mode.name,
            GamepadMappingMode.entries.map { it.name },
            { selected -> onModeChange(GamepadMappingMode.valueOf(selected)) }
        )
        SliderSetting("Joystick sensitivity", "Analog stick response", joystickSensitivity.toFloat(), { onJoystickSensitivityChange(it.toInt()) })
        SliderSetting("Dead zone", "Stick movement threshold", deadZone.toFloat(), { onDeadZoneChange(it.toInt()) }, 0f..50f)
        SliderSetting("Button opacity", "Control transparency", buttonOpacity * 100f, { onButtonOpacityChange(it / 100f) })
        SliderSetting("Turbo speed", "Rapid-fire button speed", turboSpeed.toFloat(), { onTurboSpeedChange(it.toInt()) })
        ToggleSetting("Lock layout while playing", "Prevent accidental layout changes", lockLayout, onLockLayoutChange)
        ToggleSetting("Haptic feedback", "Vibrate on gamepad button press", hapticFeedback, onHapticFeedbackChange)
    }
}

@Composable
private fun AboutTab() = SettingsColumn {
    val uriHandler = LocalUriHandler.current
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow)
    ) {
        Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Bluetooth, contentDescription = "BluePilot", tint = Primary, modifier = Modifier.size(56.dp))
            Spacer(Modifier.height(12.dp))
            Text("BluePilot Remote", color = OnSurface, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("Version 1.0.0", color = OnSurfaceVariant)
            Spacer(Modifier.height(12.dp))
            Text("Bluetooth HID remote for mouse, keyboard, media, presenter and gamepad controls.", color = OnSurfaceVariant, textAlign = TextAlign.Center)
        }
    }
    SettingsSection("Information") {
        InfoRow("License", "MIT")
        SettingItem("Source Code", "Open GitHub repository", { uriHandler.openUri("https://github.com/Skywave22/BluePilot-Remote") })
        SettingItem("Privacy Policy", "No account and no cloud server in this app", { })
        SettingItem("Terms", "Use only with devices you own or have permission to control", { })
    }
    SettingsSection("Support") {
        SettingItem("Report a bug", "Open GitHub issues", { uriHandler.openUri("https://github.com/Skywave22/BluePilot-Remote/issues") })
        SettingItem("Connection help", "Tip: remove old PC pairing, pair again, then connect", { })
    }
}

@Composable
private fun SettingsColumn(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        content = content
    )
}

@Composable
private fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(title, color = Primary, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow)
        ) {
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
                    DropdownMenuItem(
                        text = { Text(choice.replace('_', ' ')) },
                        onClick = {
                            expanded = false
                            onSelected(choice)
                        }
                    )
                }
            }
        }
    }
}
