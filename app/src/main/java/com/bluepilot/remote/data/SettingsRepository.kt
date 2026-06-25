package com.bluepilot.remote.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.bluepilot.remote.model.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * DataStore extension for settings
 */
private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

/**
 * Repository for managing app settings using DataStore
 */
@Singleton
class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private object PreferencesKeys {
        // App Settings
        val THEME = stringPreferencesKey("theme")
        val FULLSCREEN_MODE = booleanPreferencesKey("fullscreen_mode")
        val KEEP_SCREEN_ON = booleanPreferencesKey("keep_screen_on")
        val TOUCH_VIBRATIONS = booleanPreferencesKey("touch_vibrations")
        val SHOW_ANDROID_NAVIGATION = booleanPreferencesKey("show_android_navigation")
        val SHOW_MEDIA_BUTTONS = booleanPreferencesKey("show_media_buttons")
        val SHOW_SHORTCUTS = booleanPreferencesKey("show_shortcuts")

        // Mouse Settings
        val AIR_MOUSE_ENABLED = booleanPreferencesKey("air_mouse_enabled")
        val SENSITIVITY = intPreferencesKey("sensitivity")
        val POINTER_SPEED = intPreferencesKey("pointer_speed")
        val SCROLL_SPEED = intPreferencesKey("scroll_speed")
        val INVERT_SCROLL = booleanPreferencesKey("invert_scroll")
        val MOUSE_JIGGLER = booleanPreferencesKey("mouse_jiggler")
        val PEN_MODE = booleanPreferencesKey("pen_mode")

        // Keyboard Settings
        val KEYBOARD_LANGUAGE = stringPreferencesKey("keyboard_language")
        val SHOW_TEXT_INPUT_BAR = booleanPreferencesKey("show_text_input_bar")
        val AUTO_HIDE_KEYBOARD = booleanPreferencesKey("auto_hide_keyboard")

        // Gamepad Settings
        val GAMEPAD_MODE = stringPreferencesKey("gamepad_mode")
        val JOYSTICK_SENSITIVITY = intPreferencesKey("joystick_sensitivity")
        val DEAD_ZONE = intPreferencesKey("dead_zone")
        val BUTTON_OPACITY = stringPreferencesKey("button_opacity")
        val TURBO_SPEED = intPreferencesKey("turbo_speed")
        val LOCK_LAYOUT_WHILE_PLAYING = booleanPreferencesKey("lock_layout_while_playing")
        val GAMEPAD_HAPTIC_FEEDBACK = booleanPreferencesKey("gamepad_haptic_feedback")
    }

    // App Settings
    val appSettings: Flow<AppSettings> = context.settingsDataStore.data.map { preferences ->
        AppSettings(
            theme = ThemeMode.valueOf(
                preferences[PreferencesKeys.THEME] ?: ThemeMode.DARK.name
            ),
            fullscreenMode = preferences[PreferencesKeys.FULLSCREEN_MODE] ?: false,
            keepScreenOn = preferences[PreferencesKeys.KEEP_SCREEN_ON] ?: true,
            touchVibrations = preferences[PreferencesKeys.TOUCH_VIBRATIONS] ?: true,
            showAndroidNavigation = preferences[PreferencesKeys.SHOW_ANDROID_NAVIGATION] ?: true,
            showMediaButtons = preferences[PreferencesKeys.SHOW_MEDIA_BUTTONS] ?: true,
            showShortcuts = preferences[PreferencesKeys.SHOW_SHORTCUTS] ?: true
        )
    }

    suspend fun saveAppSettings(settings: AppSettings) {
        context.settingsDataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME] = settings.theme.name
            preferences[PreferencesKeys.FULLSCREEN_MODE] = settings.fullscreenMode
            preferences[PreferencesKeys.KEEP_SCREEN_ON] = settings.keepScreenOn
            preferences[PreferencesKeys.TOUCH_VIBRATIONS] = settings.touchVibrations
            preferences[PreferencesKeys.SHOW_ANDROID_NAVIGATION] = settings.showAndroidNavigation
            preferences[PreferencesKeys.SHOW_MEDIA_BUTTONS] = settings.showMediaButtons
            preferences[PreferencesKeys.SHOW_SHORTCUTS] = settings.showShortcuts
        }
    }

    // Mouse Settings
    val mouseSettings: Flow<MouseSettings> = context.settingsDataStore.data.map { preferences ->
        MouseSettings(
            airMouseEnabled = preferences[PreferencesKeys.AIR_MOUSE_ENABLED] ?: false,
            sensitivity = preferences[PreferencesKeys.SENSITIVITY] ?: 65,
            pointerSpeed = preferences[PreferencesKeys.POINTER_SPEED] ?: 45,
            scrollSpeed = preferences[PreferencesKeys.SCROLL_SPEED] ?: 50,
            invertScroll = preferences[PreferencesKeys.INVERT_SCROLL] ?: true,
            mouseJiggler = preferences[PreferencesKeys.MOUSE_JIGGLER] ?: false,
            penMode = preferences[PreferencesKeys.PEN_MODE] ?: false
        )
    }

    suspend fun saveMouseSettings(settings: MouseSettings) {
        context.settingsDataStore.edit { preferences ->
            preferences[PreferencesKeys.AIR_MOUSE_ENABLED] = settings.airMouseEnabled
            preferences[PreferencesKeys.SENSITIVITY] = settings.sensitivity
            preferences[PreferencesKeys.POINTER_SPEED] = settings.pointerSpeed
            preferences[PreferencesKeys.SCROLL_SPEED] = settings.scrollSpeed
            preferences[PreferencesKeys.INVERT_SCROLL] = settings.invertScroll
            preferences[PreferencesKeys.MOUSE_JIGGLER] = settings.mouseJiggler
            preferences[PreferencesKeys.PEN_MODE] = settings.penMode
        }
    }

    // Keyboard Settings
    val keyboardSettings: Flow<KeyboardSettings> = context.settingsDataStore.data.map { preferences ->
        KeyboardSettings(
            language = preferences[PreferencesKeys.KEYBOARD_LANGUAGE] ?: "en_US",
            showTextInputBar = preferences[PreferencesKeys.SHOW_TEXT_INPUT_BAR] ?: true,
            autoHideKeyboard = preferences[PreferencesKeys.AUTO_HIDE_KEYBOARD] ?: false
        )
    }

    suspend fun saveKeyboardSettings(settings: KeyboardSettings) {
        context.settingsDataStore.edit { preferences ->
            preferences[PreferencesKeys.KEYBOARD_LANGUAGE] = settings.language
            preferences[PreferencesKeys.SHOW_TEXT_INPUT_BAR] = settings.showTextInputBar
            preferences[PreferencesKeys.AUTO_HIDE_KEYBOARD] = settings.autoHideKeyboard
        }
    }

    // Gamepad Settings
    val gamepadSettings: Flow<GamepadSettings> = context.settingsDataStore.data.map { preferences ->
        GamepadSettings(
            gamepadMode = GamepadMappingMode.valueOf(
                preferences[PreferencesKeys.GAMEPAD_MODE] ?: GamepadMappingMode.KEYBOARD_FALLBACK.name
            ),
            joystickSensitivity = preferences[PreferencesKeys.JOYSTICK_SENSITIVITY] ?: 70,
            deadZone = preferences[PreferencesKeys.DEAD_ZONE] ?: 10,
            buttonOpacity = preferences[PreferencesKeys.BUTTON_OPACITY]?.toFloatOrNull() ?: 0.8f,
            turboSpeed = preferences[PreferencesKeys.TURBO_SPEED] ?: 50,
            lockLayoutWhilePlaying = preferences[PreferencesKeys.LOCK_LAYOUT_WHILE_PLAYING] ?: true,
            hapticFeedback = preferences[PreferencesKeys.GAMEPAD_HAPTIC_FEEDBACK] ?: true
        )
    }

    suspend fun saveGamepadSettings(settings: GamepadSettings) {
        context.settingsDataStore.edit { preferences ->
            preferences[PreferencesKeys.GAMEPAD_MODE] = settings.gamepadMode.name
            preferences[PreferencesKeys.JOYSTICK_SENSITIVITY] = settings.joystickSensitivity
            preferences[PreferencesKeys.DEAD_ZONE] = settings.deadZone
            preferences[PreferencesKeys.BUTTON_OPACITY] = settings.buttonOpacity.toString()
            preferences[PreferencesKeys.TURBO_SPEED] = settings.turboSpeed
            preferences[PreferencesKeys.LOCK_LAYOUT_WHILE_PLAYING] = settings.lockLayoutWhilePlaying
            preferences[PreferencesKeys.GAMEPAD_HAPTIC_FEEDBACK] = settings.hapticFeedback
        }
    }

    // Individual setting setters for quick access
    suspend fun setTheme(theme: ThemeMode) {
        context.settingsDataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME] = theme.name
        }
    }

    suspend fun setKeepScreenOn(keepOn: Boolean) {
        context.settingsDataStore.edit { preferences ->
            preferences[PreferencesKeys.KEEP_SCREEN_ON] = keepOn
        }
    }

    suspend fun setTouchVibrations(enabled: Boolean) {
        context.settingsDataStore.edit { preferences ->
            preferences[PreferencesKeys.TOUCH_VIBRATIONS] = enabled
        }
    }

    suspend fun setAirMouseEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { preferences ->
            preferences[PreferencesKeys.AIR_MOUSE_ENABLED] = enabled
        }
    }

    suspend fun setSensitivity(value: Int) {
        context.settingsDataStore.edit { preferences ->
            preferences[PreferencesKeys.SENSITIVITY] = value
        }
    }

    suspend fun setPointerSpeed(value: Int) {
        context.settingsDataStore.edit { preferences ->
            preferences[PreferencesKeys.POINTER_SPEED] = value
        }
    }

    suspend fun setInvertScroll(invert: Boolean) {
        context.settingsDataStore.edit { preferences ->
            preferences[PreferencesKeys.INVERT_SCROLL] = invert
        }
    }

    suspend fun setGamepadMode(mode: GamepadMappingMode) {
        context.settingsDataStore.edit { preferences ->
            preferences[PreferencesKeys.GAMEPAD_MODE] = mode.name
        }
    }

    suspend fun setJoystickSensitivity(value: Int) {
        context.settingsDataStore.edit { preferences ->
            preferences[PreferencesKeys.JOYSTICK_SENSITIVITY] = value
        }
    }

    suspend fun setDeadZone(value: Int) {
        context.settingsDataStore.edit { preferences ->
            preferences[PreferencesKeys.DEAD_ZONE] = value
        }
    }

    suspend fun setButtonOpacity(value: Float) {
        context.settingsDataStore.edit { preferences ->
            preferences[PreferencesKeys.BUTTON_OPACITY] = value.toString()
        }
    }

    suspend fun setTurboSpeed(value: Int) {
        context.settingsDataStore.edit { preferences ->
            preferences[PreferencesKeys.TURBO_SPEED] = value
        }
    }

    suspend fun setLockLayoutWhilePlaying(lock: Boolean) {
        context.settingsDataStore.edit { preferences ->
            preferences[PreferencesKeys.LOCK_LAYOUT_WHILE_PLAYING] = lock
        }
    }

    suspend fun setGamepadHapticFeedback(enabled: Boolean) {
        context.settingsDataStore.edit { preferences ->
            preferences[PreferencesKeys.GAMEPAD_HAPTIC_FEEDBACK] = enabled
        }
    }
}
