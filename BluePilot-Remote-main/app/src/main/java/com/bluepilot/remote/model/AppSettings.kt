package com.bluepilot.remote.model

/** App settings that are actively used by the production UI/runtime. */
data class AppSettings(
    val theme: ThemeMode = ThemeMode.DARK,
    val fullscreenMode: Boolean = false,
    val keepScreenOn: Boolean = true,
    val touchVibrations: Boolean = true,
    val showAndroidNavigation: Boolean = true,
    val secureScreen: Boolean = false
)

enum class ThemeMode {
    LIGHT,
    DARK,
    SYSTEM
}

/** Mouse settings actively applied to trackpad output. */
data class MouseSettings(
    val sensitivity: Int = 65,
    val pointerSpeed: Int = 45,
    val scrollSpeed: Int = 50,
    val movementSmoothing: Int = 20,
    val invertScroll: Boolean = true,
    val tapToClick: Boolean = true,
    val penMode: Boolean = false
)

/** Keyboard settings actively applied to keyboard screen UI. */
data class KeyboardSettings(
    val showTextInputBar: Boolean = true
)

/** Gamepad settings actively applied to HID/fallback output. */
data class GamepadSettings(
    val gamepadMode: GamepadMappingMode = GamepadMappingMode.KEYBOARD_FALLBACK,
    val joystickSensitivity: Int = 70,
    val deadZone: Int = 10,
    val hapticFeedback: Boolean = true
)
