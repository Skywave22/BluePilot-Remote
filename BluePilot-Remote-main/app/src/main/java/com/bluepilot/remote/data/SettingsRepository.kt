package com.bluepilot.remote.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.bluepilot.remote.model.AppSettings
import com.bluepilot.remote.model.GamepadMappingMode
import com.bluepilot.remote.model.GamepadSettings
import com.bluepilot.remote.model.KeyboardSettings
import com.bluepilot.remote.model.MouseSettings
import com.bluepilot.remote.model.ThemeMode
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

/** DataStore-backed settings repository. Only production-used settings are exposed. */
@Singleton
class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object PreferencesKeys {
        val THEME = stringPreferencesKey("theme")
        val FULLSCREEN_MODE = booleanPreferencesKey("fullscreen_mode")
        val KEEP_SCREEN_ON = booleanPreferencesKey("keep_screen_on")
        val TOUCH_VIBRATIONS = booleanPreferencesKey("touch_vibrations")
        val SHOW_ANDROID_NAVIGATION = booleanPreferencesKey("show_android_navigation")
        val SECURE_SCREEN = booleanPreferencesKey("secure_screen")

        val SENSITIVITY = intPreferencesKey("sensitivity")
        val POINTER_SPEED = intPreferencesKey("pointer_speed")
        val SCROLL_SPEED = intPreferencesKey("scroll_speed")
        val MOVEMENT_SMOOTHING = intPreferencesKey("movement_smoothing")
        val INVERT_SCROLL = booleanPreferencesKey("invert_scroll")
        val TAP_TO_CLICK = booleanPreferencesKey("tap_to_click")
        val PEN_MODE = booleanPreferencesKey("pen_mode")

        val SHOW_TEXT_INPUT_BAR = booleanPreferencesKey("show_text_input_bar")

        val GAMEPAD_MODE = stringPreferencesKey("gamepad_mode")
        val JOYSTICK_SENSITIVITY = intPreferencesKey("joystick_sensitivity")
        val DEAD_ZONE = intPreferencesKey("dead_zone")
        val GAMEPAD_HAPTIC_FEEDBACK = booleanPreferencesKey("gamepad_haptic_feedback")
    }

    val appSettings: Flow<AppSettings> = context.settingsDataStore.data.map { preferences ->
        AppSettings(
            theme = runCatching { ThemeMode.valueOf(preferences[PreferencesKeys.THEME] ?: ThemeMode.DARK.name) }.getOrDefault(ThemeMode.DARK),
            fullscreenMode = preferences[PreferencesKeys.FULLSCREEN_MODE] ?: false,
            keepScreenOn = preferences[PreferencesKeys.KEEP_SCREEN_ON] ?: true,
            touchVibrations = preferences[PreferencesKeys.TOUCH_VIBRATIONS] ?: true,
            showAndroidNavigation = preferences[PreferencesKeys.SHOW_ANDROID_NAVIGATION] ?: true,
            secureScreen = preferences[PreferencesKeys.SECURE_SCREEN] ?: false
        )
    }

    suspend fun saveAppSettings(settings: AppSettings) {
        context.settingsDataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME] = settings.theme.name
            preferences[PreferencesKeys.FULLSCREEN_MODE] = settings.fullscreenMode
            preferences[PreferencesKeys.KEEP_SCREEN_ON] = settings.keepScreenOn
            preferences[PreferencesKeys.TOUCH_VIBRATIONS] = settings.touchVibrations
            preferences[PreferencesKeys.SHOW_ANDROID_NAVIGATION] = settings.showAndroidNavigation
            preferences[PreferencesKeys.SECURE_SCREEN] = settings.secureScreen
        }
    }

    val mouseSettings: Flow<MouseSettings> = context.settingsDataStore.data.map { preferences ->
        MouseSettings(
            sensitivity = preferences[PreferencesKeys.SENSITIVITY] ?: 65,
            pointerSpeed = preferences[PreferencesKeys.POINTER_SPEED] ?: 45,
            scrollSpeed = preferences[PreferencesKeys.SCROLL_SPEED] ?: 50,
            movementSmoothing = preferences[PreferencesKeys.MOVEMENT_SMOOTHING] ?: 20,
            invertScroll = preferences[PreferencesKeys.INVERT_SCROLL] ?: true,
            tapToClick = preferences[PreferencesKeys.TAP_TO_CLICK] ?: true,
            penMode = preferences[PreferencesKeys.PEN_MODE] ?: false
        )
    }

    suspend fun saveMouseSettings(settings: MouseSettings) {
        context.settingsDataStore.edit { preferences ->
            preferences[PreferencesKeys.SENSITIVITY] = settings.sensitivity
            preferences[PreferencesKeys.POINTER_SPEED] = settings.pointerSpeed
            preferences[PreferencesKeys.SCROLL_SPEED] = settings.scrollSpeed
            preferences[PreferencesKeys.MOVEMENT_SMOOTHING] = settings.movementSmoothing
            preferences[PreferencesKeys.INVERT_SCROLL] = settings.invertScroll
            preferences[PreferencesKeys.TAP_TO_CLICK] = settings.tapToClick
            preferences[PreferencesKeys.PEN_MODE] = settings.penMode
        }
    }

    val keyboardSettings: Flow<KeyboardSettings> = context.settingsDataStore.data.map { preferences ->
        KeyboardSettings(
            showTextInputBar = preferences[PreferencesKeys.SHOW_TEXT_INPUT_BAR] ?: true
        )
    }

    suspend fun saveKeyboardSettings(settings: KeyboardSettings) {
        context.settingsDataStore.edit { preferences ->
            preferences[PreferencesKeys.SHOW_TEXT_INPUT_BAR] = settings.showTextInputBar
        }
    }

    val gamepadSettings: Flow<GamepadSettings> = context.settingsDataStore.data.map { preferences ->
        GamepadSettings(
            gamepadMode = runCatching {
                GamepadMappingMode.valueOf(preferences[PreferencesKeys.GAMEPAD_MODE] ?: GamepadMappingMode.KEYBOARD_FALLBACK.name)
            }.getOrDefault(GamepadMappingMode.KEYBOARD_FALLBACK),
            joystickSensitivity = preferences[PreferencesKeys.JOYSTICK_SENSITIVITY] ?: 70,
            deadZone = preferences[PreferencesKeys.DEAD_ZONE] ?: 10,
            hapticFeedback = preferences[PreferencesKeys.GAMEPAD_HAPTIC_FEEDBACK] ?: true
        )
    }

    suspend fun saveGamepadSettings(settings: GamepadSettings) {
        context.settingsDataStore.edit { preferences ->
            preferences[PreferencesKeys.GAMEPAD_MODE] = settings.gamepadMode.name
            preferences[PreferencesKeys.JOYSTICK_SENSITIVITY] = settings.joystickSensitivity
            preferences[PreferencesKeys.DEAD_ZONE] = settings.deadZone
            preferences[PreferencesKeys.GAMEPAD_HAPTIC_FEEDBACK] = settings.hapticFeedback
        }
    }

    suspend fun setTheme(theme: ThemeMode) = context.settingsDataStore.edit { it[PreferencesKeys.THEME] = theme.name }
    suspend fun setKeepScreenOn(keepOn: Boolean) = context.settingsDataStore.edit { it[PreferencesKeys.KEEP_SCREEN_ON] = keepOn }
    suspend fun setTouchVibrations(enabled: Boolean) = context.settingsDataStore.edit { it[PreferencesKeys.TOUCH_VIBRATIONS] = enabled }
    suspend fun setSecureScreen(enabled: Boolean) = context.settingsDataStore.edit { it[PreferencesKeys.SECURE_SCREEN] = enabled }
    suspend fun setSensitivity(value: Int) = context.settingsDataStore.edit { it[PreferencesKeys.SENSITIVITY] = value.coerceIn(0, 100) }
    suspend fun setPointerSpeed(value: Int) = context.settingsDataStore.edit { it[PreferencesKeys.POINTER_SPEED] = value.coerceIn(0, 100) }
    suspend fun setMovementSmoothing(value: Int) = context.settingsDataStore.edit { it[PreferencesKeys.MOVEMENT_SMOOTHING] = value.coerceIn(0, 100) }
    suspend fun setInvertScroll(invert: Boolean) = context.settingsDataStore.edit { it[PreferencesKeys.INVERT_SCROLL] = invert }
    suspend fun setGamepadMode(mode: GamepadMappingMode) = context.settingsDataStore.edit { it[PreferencesKeys.GAMEPAD_MODE] = mode.name }
    suspend fun setJoystickSensitivity(value: Int) = context.settingsDataStore.edit { it[PreferencesKeys.JOYSTICK_SENSITIVITY] = value.coerceIn(0, 100) }
    suspend fun setDeadZone(value: Int) = context.settingsDataStore.edit { it[PreferencesKeys.DEAD_ZONE] = value.coerceIn(0, 50) }
    suspend fun setGamepadHapticFeedback(enabled: Boolean) = context.settingsDataStore.edit { it[PreferencesKeys.GAMEPAD_HAPTIC_FEEDBACK] = enabled }
}
