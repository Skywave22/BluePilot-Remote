package com.bluepilot.remote.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bluepilot.remote.data.SettingsRepository
import com.bluepilot.remote.model.AppSettings
import com.bluepilot.remote.model.GamepadMappingMode
import com.bluepilot.remote.model.GamepadSettings
import com.bluepilot.remote.model.KeyboardSettings
import com.bluepilot.remote.model.MouseSettings
import com.bluepilot.remote.model.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: SettingsRepository
) : ViewModel() {

    val appSettings: StateFlow<AppSettings> = repository.appSettings.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        AppSettings()
    )

    val mouseSettings: StateFlow<MouseSettings> = repository.mouseSettings.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        MouseSettings()
    )

    val keyboardSettings: StateFlow<KeyboardSettings> = repository.keyboardSettings.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        KeyboardSettings()
    )

    val gamepadSettings: StateFlow<GamepadSettings> = repository.gamepadSettings.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        GamepadSettings()
    )

    fun setTheme(theme: ThemeMode) = viewModelScope.launch { repository.setTheme(theme) }
    fun setFullscreen(enabled: Boolean) = updateApp { it.copy(fullscreenMode = enabled) }
    fun setKeepScreenOn(enabled: Boolean) = viewModelScope.launch { repository.setKeepScreenOn(enabled) }
    fun setTouchVibrations(enabled: Boolean) = viewModelScope.launch { repository.setTouchVibrations(enabled) }
    fun setShowAndroidNavigation(enabled: Boolean) = updateApp { it.copy(showAndroidNavigation = enabled) }
    fun setShowMediaButtons(enabled: Boolean) = updateApp { it.copy(showMediaButtons = enabled) }
    fun setShowShortcuts(enabled: Boolean) = updateApp { it.copy(showShortcuts = enabled) }

    fun setAirMouseEnabled(enabled: Boolean) = viewModelScope.launch { repository.setAirMouseEnabled(enabled) }
    fun setSensitivity(value: Int) = viewModelScope.launch { repository.setSensitivity(value) }
    fun setPointerSpeed(value: Int) = viewModelScope.launch { repository.setPointerSpeed(value) }
    fun setScrollSpeed(value: Int) = updateMouse { it.copy(scrollSpeed = value) }
    fun setInvertScroll(enabled: Boolean) = viewModelScope.launch { repository.setInvertScroll(enabled) }
    fun setMouseJiggler(enabled: Boolean) = updateMouse { it.copy(mouseJiggler = enabled) }
    fun setPenMode(enabled: Boolean) = updateMouse { it.copy(penMode = enabled) }

    fun setKeyboardLanguage(language: String) = updateKeyboard { it.copy(language = language) }
    fun setShowTextInputBar(enabled: Boolean) = updateKeyboard { it.copy(showTextInputBar = enabled) }
    fun setAutoHideKeyboard(enabled: Boolean) = updateKeyboard { it.copy(autoHideKeyboard = enabled) }

    fun setGamepadMode(mode: GamepadMappingMode) = viewModelScope.launch { repository.setGamepadMode(mode) }
    fun setJoystickSensitivity(value: Int) = viewModelScope.launch { repository.setJoystickSensitivity(value) }
    fun setDeadZone(value: Int) = viewModelScope.launch { repository.setDeadZone(value) }
    fun setButtonOpacity(value: Float) = viewModelScope.launch { repository.setButtonOpacity(value) }
    fun setTurboSpeed(value: Int) = viewModelScope.launch { repository.setTurboSpeed(value) }
    fun setLockLayoutWhilePlaying(enabled: Boolean) = viewModelScope.launch { repository.setLockLayoutWhilePlaying(enabled) }
    fun setGamepadHapticFeedback(enabled: Boolean) = viewModelScope.launch { repository.setGamepadHapticFeedback(enabled) }

    private fun updateApp(block: (AppSettings) -> AppSettings) = viewModelScope.launch {
        repository.saveAppSettings(block(appSettings.value))
    }

    private fun updateMouse(block: (MouseSettings) -> MouseSettings) = viewModelScope.launch {
        repository.saveMouseSettings(block(mouseSettings.value))
    }

    private fun updateKeyboard(block: (KeyboardSettings) -> KeyboardSettings) = viewModelScope.launch {
        repository.saveKeyboardSettings(block(keyboardSettings.value))
    }
}
