package com.bluepilot.remote.model

/**
 * Represents a HID action that can be sent to a connected device
 */
sealed class HidAction {
    data class KeyboardKey(
        val keyCode: Byte,
        val modifiers: Byte = 0
    ) : HidAction()

    data class KeyDown(
        val keyCode: Byte,
        val modifiers: Byte = 0
    ) : HidAction()

    data class KeyUp(
        val keyCode: Byte
    ) : HidAction()

    data class SendText(
        val text: String
    ) : HidAction()

    data class MouseMove(
        val dx: Byte,
        val dy: Byte
    ) : HidAction()

    data class MouseClick(
        val button: MouseButton
    ) : HidAction()

    data class MouseButtonDown(
        val button: MouseButton
    ) : HidAction()

    data class MouseButtonUp(
        val button: MouseButton
    ) : HidAction()

    data class MouseScroll(
        val amount: Byte
    ) : HidAction()

    data class ConsumerControl(
        val code: Short
    ) : HidAction()

    data class SystemControl(
        val code: Byte
    ) : HidAction()

    data class GamepadReport(
        val state: GamepadState
    ) : HidAction()
}

/**
 * Mouse button types
 */
enum class MouseButton(val value: Byte) {
    LEFT(0x01),
    RIGHT(0x02),
    MIDDLE(0x04)
}

/**
 * Consumer control codes for media keys
 */
object ConsumerControlCodes {
    const val PLAY_PAUSE: Short = 0xCD.toShort()
    const val STOP: Short = 0xB7.toShort()
    const val NEXT_TRACK: Short = 0xB5.toShort()
    const val PREVIOUS_TRACK: Short = 0xB6.toShort()
    const val VOLUME_UP: Short = 0xE9.toShort()
    const val VOLUME_DOWN: Short = 0xEA.toShort()
    const val MUTE: Short = 0xE2.toShort()
    const val BRIGHTNESS_UP: Short = 0x6F.toShort()
    const val BRIGHTNESS_DOWN: Short = 0x70.toShort()
}

/**
 * System control codes
 */
object SystemControlCodes {
    const val POWER: Byte = 0x01
    const val SLEEP: Byte = 0x02
    const val WAKE_UP: Byte = 0x03
}

/**
 * HID keyboard modifier bits
 */
object KeyboardModifiers {
    const val LEFT_CTRL: Byte = 0x01
    const val LEFT_SHIFT: Byte = 0x02
    const val LEFT_ALT: Byte = 0x04
    const val LEFT_GUI: Byte = 0x08
    const val RIGHT_CTRL: Byte = 0x10
    const val RIGHT_SHIFT: Byte = 0x20
    const val RIGHT_ALT: Byte = 0x40
    const val RIGHT_GUI: Byte = 0x80.toByte()
}

/**
 * HID keyboard key codes (subset of common keys)
 */
object KeyboardKeyCodes {
    const val KEY_LEFT_CTRL: Byte = 0xE0.toByte()
    const val KEY_LEFT_SHIFT: Byte = 0xE1.toByte()
    const val KEY_A: Byte = 0x04
    const val KEY_B: Byte = 0x05
    const val KEY_C: Byte = 0x06
    const val KEY_D: Byte = 0x07
    const val KEY_E: Byte = 0x08
    const val KEY_F: Byte = 0x09
    const val KEY_G: Byte = 0x0A
    const val KEY_H: Byte = 0x0B
    const val KEY_I: Byte = 0x0C
    const val KEY_J: Byte = 0x0D
    const val KEY_K: Byte = 0x0E
    const val KEY_L: Byte = 0x0F
    const val KEY_M: Byte = 0x10
    const val KEY_N: Byte = 0x11
    const val KEY_O: Byte = 0x12
    const val KEY_P: Byte = 0x13
    const val KEY_Q: Byte = 0x14
    const val KEY_R: Byte = 0x15
    const val KEY_S: Byte = 0x16
    const val KEY_T: Byte = 0x17
    const val KEY_U: Byte = 0x18
    const val KEY_V: Byte = 0x19
    const val KEY_W: Byte = 0x1A
    const val KEY_X: Byte = 0x1B
    const val KEY_Y: Byte = 0x1C
    const val KEY_Z: Byte = 0x1D
    const val KEY_1: Byte = 0x1E
    const val KEY_2: Byte = 0x1F
    const val KEY_3: Byte = 0x20
    const val KEY_4: Byte = 0x21
    const val KEY_5: Byte = 0x22
    const val KEY_6: Byte = 0x23
    const val KEY_7: Byte = 0x24
    const val KEY_8: Byte = 0x25
    const val KEY_9: Byte = 0x26
    const val KEY_0: Byte = 0x27
    const val KEY_ENTER: Byte = 0x28
    const val KEY_ESCAPE: Byte = 0x29
    const val KEY_BACKSPACE: Byte = 0x2A
    const val KEY_TAB: Byte = 0x2B
    const val KEY_SPACE: Byte = 0x2C
    const val KEY_MINUS: Byte = 0x2D
    const val KEY_EQUAL: Byte = 0x2E
    const val KEY_LEFT_BRACKET: Byte = 0x2F
    const val KEY_RIGHT_BRACKET: Byte = 0x30
    const val KEY_BACKSLASH: Byte = 0x31
    const val KEY_SEMICOLON: Byte = 0x33
    const val KEY_QUOTE: Byte = 0x34
    const val KEY_GRAVE: Byte = 0x35
    const val KEY_COMMA: Byte = 0x36
    const val KEY_PERIOD: Byte = 0x37
    const val KEY_SLASH: Byte = 0x38
    const val KEY_CAPS_LOCK: Byte = 0x39
    const val KEY_F1: Byte = 0x3A
    const val KEY_F2: Byte = 0x3B
    const val KEY_F3: Byte = 0x3C
    const val KEY_F4: Byte = 0x3D
    const val KEY_F5: Byte = 0x3E
    const val KEY_F6: Byte = 0x3F
    const val KEY_F7: Byte = 0x40
    const val KEY_F8: Byte = 0x41
    const val KEY_F9: Byte = 0x42
    const val KEY_F10: Byte = 0x43
    const val KEY_F11: Byte = 0x44
    const val KEY_F12: Byte = 0x45
    const val KEY_PRINT_SCREEN: Byte = 0x46
    const val KEY_SCROLL_LOCK: Byte = 0x47
    const val KEY_PAUSE: Byte = 0x48
    const val KEY_INSERT: Byte = 0x49
    const val KEY_HOME: Byte = 0x4A
    const val KEY_PAGE_UP: Byte = 0x4B
    const val KEY_DELETE: Byte = 0x4C
    const val KEY_END: Byte = 0x4D
    const val KEY_PAGE_DOWN: Byte = 0x4E
    const val KEY_RIGHT_ARROW: Byte = 0x4F
    const val KEY_LEFT_ARROW: Byte = 0x50
    const val KEY_DOWN_ARROW: Byte = 0x51
    const val KEY_UP_ARROW: Byte = 0x52
    const val KEY_NUM_LOCK: Byte = 0x53
    const val KEY_KP_DIVIDE: Byte = 0x54
    const val KEY_KP_MULTIPLY: Byte = 0x55
    const val KEY_KP_MINUS: Byte = 0x56
    const val KEY_KP_PLUS: Byte = 0x57
    const val KEY_KP_ENTER: Byte = 0x58
    const val KEY_KP_1: Byte = 0x59
    const val KEY_KP_2: Byte = 0x5A
    const val KEY_KP_3: Byte = 0x5B
    const val KEY_KP_4: Byte = 0x5C
    const val KEY_KP_5: Byte = 0x5D
    const val KEY_KP_6: Byte = 0x5E
    const val KEY_KP_7: Byte = 0x5F
    const val KEY_KP_8: Byte = 0x60
    const val KEY_KP_9: Byte = 0x61
    const val KEY_KP_0: Byte = 0x62
    const val KEY_KP_PERIOD: Byte = 0x63
}
