package com.bluepilot.remote.model

/**
 * App settings data class
 */
data class AppSettings(
    val theme: ThemeMode = ThemeMode.DARK,
    val fullscreenMode: Boolean = false,
    val keepScreenOn: Boolean = true,
    val touchVibrations: Boolean = true,
    val showAndroidNavigation: Boolean = true,
    val showMediaButtons: Boolean = true,
    val showShortcuts: Boolean = true
)

/**
 * Theme mode
 */
enum class ThemeMode {
    LIGHT,
    DARK,
    SYSTEM
}

/**
 * Mouse settings
 */
data class MouseSettings(
    val airMouseEnabled: Boolean = false,
    val sensitivity: Int = 65,
    val pointerSpeed: Int = 45,
    val scrollSpeed: Int = 50,
    val invertScroll: Boolean = true,
    val mouseJiggler: Boolean = false,
    val penMode: Boolean = false
)

/**
 * Keyboard settings
 */
data class KeyboardSettings(
    val language: String = "en_US",
    val showTextInputBar: Boolean = true,
    val autoHideKeyboard: Boolean = false
)

/**
 * Display settings
 */
data class DisplaySettings(
    val theme: ThemeMode = ThemeMode.DARK,
    val fullscreenMode: Boolean = false,
    val keepScreenOn: Boolean = true
)

/**
 * Gamepad settings
 */
data class GamepadSettings(
    val gamepadMode: GamepadMappingMode = GamepadMappingMode.KEYBOARD_FALLBACK,
    val joystickSensitivity: Int = 70,
    val deadZone: Int = 10,
    val buttonOpacity: Float = 0.8f,
    val turboSpeed: Int = 50,
    val lockLayoutWhilePlaying: Boolean = true,
    val hapticFeedback: Boolean = true
)

/**
 * Joystick settings
 */
data class JoystickSettings(
    val sensitivity: Int = 70,
    val deadZone: Int = 10,
    val invertY: Boolean = false
)

/**
 * Gamepad profile for custom layouts
 */
data class GamepadProfile(
    val id: String,
    val name: String,
    val layoutType: GamepadLayoutType,
    val components: List<GamepadComponent>,
    val isPortrait: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Gamepad layout types
 */
enum class GamepadLayoutType {
    CLASSIC_GAMEPAD,
    FPS_KEYBOARD_MAPPING,
    RACING_LAYOUT,
    RETRO_EMULATOR,
    MEDIA_GAMING,
    CUSTOM
}

/**
 * Gamepad component for layout editor
 */
data class GamepadComponent(
    val id: String,
    val type: GamepadComponentType,
    val xPercent: Float, // Position as percentage of parent width
    val yPercent: Float, // Position as percentage of parent height
    val widthPercent: Float,
    val heightPercent: Float,
    val opacity: Float = 0.8f,
    val label: String? = null,
    val action: GamepadButtonAction? = null
)

/**
 * Gamepad component types
 */
enum class GamepadComponentType {
    BUTTON,
    JOYSTICK,
    DPAD,
    TRIGGER,
    BUMPER,
    MACRO
}

/**
 * Gamepad button action
 */
sealed class GamepadButtonAction {
    data class GamepadButton(val button: com.bluepilot.remote.model.GamepadButton) : GamepadButtonAction()
    data class KeyboardKey(val keyCode: Byte, val modifiers: Byte = 0) : GamepadButtonAction()
    data class MouseClick(val button: MouseButton) : GamepadButtonAction()
    data class ConsumerControl(val code: Short) : GamepadButtonAction()
    data class Macro(val macroId: String) : GamepadButtonAction()
}
