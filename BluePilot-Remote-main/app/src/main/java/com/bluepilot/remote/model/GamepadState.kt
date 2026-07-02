package com.bluepilot.remote.model

/**
 * Represents the current state of a gamepad controller
 */
data class GamepadState(
    val leftStickX: Byte = 0,
    val leftStickY: Byte = 0,
    val rightStickX: Byte = 0,
    val rightStickY: Byte = 0,
    val dpadDirection: DpadDirection = DpadDirection.NONE,
    val pressedButtons: Int = 0,
    val leftTrigger: Byte = 0,
    val rightTrigger: Byte = 0
) {
    /**
     * Check if a specific button is pressed
     */
    fun isButtonPressed(button: GamepadButton): Boolean {
        return (pressedButtons and button.bitmask) != 0
    }

    /**
     * Set a button as pressed
     */
    fun withButtonPressed(button: GamepadButton): GamepadState {
        return copy(pressedButtons = pressedButtons or button.bitmask)
    }

    /**
     * Set a button as released
     */
    fun withButtonReleased(button: GamepadButton): GamepadState {
        return copy(pressedButtons = pressedButtons and button.bitmask.inv())
    }
}

/**
 * D-pad directions
 */
enum class DpadDirection(val value: Byte) {
    NONE(0),
    UP(1),
    UP_RIGHT(2),
    RIGHT(3),
    DOWN_RIGHT(4),
    DOWN(5),
    DOWN_LEFT(6),
    LEFT(7),
    UP_LEFT(8)
}

/**
 * Gamepad buttons with bitmask
 */
enum class GamepadButton(val bitmask: Int) {
    A(1 shl 0),
    B(1 shl 1),
    X(1 shl 2),
    Y(1 shl 3),
    L1(1 shl 4),
    R1(1 shl 5),
    L2(1 shl 6),
    R2(1 shl 7),
    SELECT(1 shl 8),
    START(1 shl 9),
    LEFT_STICK(1 shl 10),
    RIGHT_STICK(1 shl 11),
    HOME(1 shl 12)
}

/**
 * Gamepad mapping mode
 */
enum class GamepadMappingMode {
    HID_GAMEPAD,
    KEYBOARD_FALLBACK,
    MOUSE_KEYBOARD
}

/**
 * Keyboard fallback mapping for gamepad buttons
 */
object GamepadKeyboardMapping {
    val DEFAULT_MAPPING = mapOf(
        GamepadButton.A to KeyboardKeyCodes.KEY_SPACE,
        GamepadButton.B to KeyboardKeyCodes.KEY_LEFT_CTRL,
        GamepadButton.X to KeyboardKeyCodes.KEY_LEFT_SHIFT,
        GamepadButton.Y to KeyboardKeyCodes.KEY_E,
        GamepadButton.L1 to KeyboardKeyCodes.KEY_Q,
        GamepadButton.R1 to KeyboardKeyCodes.KEY_F,
        GamepadButton.START to KeyboardKeyCodes.KEY_ENTER,
        GamepadButton.SELECT to KeyboardKeyCodes.KEY_TAB,
        GamepadButton.HOME to KeyboardKeyCodes.KEY_ESCAPE
    )
}
