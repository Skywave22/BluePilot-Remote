package com.bluepilot.remote.viewmodel

import androidx.lifecycle.ViewModel
import com.bluepilot.remote.bluetooth.BluetoothHidManager
import com.bluepilot.remote.model.ConsumerControlCodes
import com.bluepilot.remote.model.DpadDirection
import com.bluepilot.remote.model.GamepadButton
import com.bluepilot.remote.model.GamepadState
import com.bluepilot.remote.model.KeyboardKeyCodes
import com.bluepilot.remote.model.KeyboardModifiers
import com.bluepilot.remote.model.MouseButton
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.math.roundToInt

/**
 * Shared HID command ViewModel used by all remote-control screens.
 * Keeps UI screens simple and avoids dead onClick handlers.
 */
@HiltViewModel
class RemoteControlViewModel @Inject constructor(
    private val hidManager: BluetoothHidManager
) : ViewModel() {

    val connectionState = hidManager.connectionState
    val isConnected = hidManager.connectionState.map { it.state.name == "CONNECTED" }

    fun mouseMove(dx: Float, dy: Float) {
        hidManager.sendMouseMove(dx.roundToHidByte(), dy.roundToHidByte())
    }

    fun mouseClick(button: MouseButton = MouseButton.LEFT) {
        hidManager.sendMouseClick(button)
    }

    fun mouseDown(button: MouseButton) = hidManager.sendMouseButtonDown(button)
    fun mouseUp(button: MouseButton) = hidManager.sendMouseButtonUp(button)
    fun mouseScroll(amount: Int) = hidManager.sendMouseScroll(amount.coerceIn(-127, 127).toByte())

    fun key(code: Byte, modifiers: Byte = 0) = hidManager.sendKeyboardKey(code, modifiers)
    fun keyDown(code: Byte, modifiers: Byte = 0) = hidManager.sendKeyDown(code, modifiers)
    fun keyUp(code: Byte) = hidManager.sendKeyUp(code)

    fun keyLabel(label: String) {
        when (label.uppercase()) {
            "CTRL" -> key(0.toByte(), KeyboardModifiers.LEFT_CTRL)
            "SHIFT" -> key(0.toByte(), KeyboardModifiers.LEFT_SHIFT)
            "ALT", "ALTGR" -> key(0.toByte(), KeyboardModifiers.LEFT_ALT)
            "WIN", "GUI" -> key(0.toByte(), KeyboardModifiers.LEFT_GUI)
            else -> keyStrokeForLabel(label)?.let { (code, modifiers) -> key(code, modifiers) }
        }
    }
    fun text(value: String) = hidManager.sendText(value)

    fun enter() = key(KeyboardKeyCodes.KEY_ENTER)
    fun tab() = key(KeyboardKeyCodes.KEY_TAB)
    fun escape() = key(KeyboardKeyCodes.KEY_ESCAPE)
    fun space() = key(KeyboardKeyCodes.KEY_SPACE)
    fun backspace() = key(KeyboardKeyCodes.KEY_BACKSPACE)

    fun mediaPlayPause() = hidManager.sendConsumerControl(ConsumerControlCodes.PLAY_PAUSE)
    fun mediaStop() = hidManager.sendConsumerControl(ConsumerControlCodes.STOP)
    fun mediaNext() = hidManager.sendConsumerControl(ConsumerControlCodes.NEXT_TRACK)
    fun mediaPrevious() = hidManager.sendConsumerControl(ConsumerControlCodes.PREVIOUS_TRACK)
    fun volumeUp() = hidManager.sendConsumerControl(ConsumerControlCodes.VOLUME_UP)
    fun volumeDown() = hidManager.sendConsumerControl(ConsumerControlCodes.VOLUME_DOWN)
    fun mute() = hidManager.sendConsumerControl(ConsumerControlCodes.MUTE)
    fun brightnessUp() = hidManager.sendConsumerControl(ConsumerControlCodes.BRIGHTNESS_UP)
    fun brightnessDown() = hidManager.sendConsumerControl(ConsumerControlCodes.BRIGHTNESS_DOWN)

    fun dpad(direction: DpadDirection) {
        hidManager.sendGamepadReport(GamepadState(dpadDirection = direction))
        hidManager.sendGamepadReport(GamepadState())
    }

    fun gamepadButton(button: GamepadButton) {
        hidManager.sendGamepadReport(GamepadState().withButtonPressed(button))
        hidManager.sendGamepadReport(GamepadState())
    }

    fun gamepadAxes(leftX: Float, leftY: Float, rightX: Float = 0f, rightY: Float = 0f) {
        hidManager.sendGamepadReport(
            GamepadState(
                leftStickX = stickToByte(leftX),
                leftStickY = stickToByte(leftY),
                rightStickX = stickToByte(rightX),
                rightStickY = stickToByte(rightY)
            )
        )
    }

    private fun keyStrokeForLabel(label: String): Pair<Byte, Byte>? = when (label.uppercase()) {
        "A" -> Pair(KeyboardKeyCodes.KEY_A, 0.toByte())
        "B" -> Pair(KeyboardKeyCodes.KEY_B, 0.toByte())
        "C" -> Pair(KeyboardKeyCodes.KEY_C, 0.toByte())
        "D" -> Pair(KeyboardKeyCodes.KEY_D, 0.toByte())
        "E" -> Pair(KeyboardKeyCodes.KEY_E, 0.toByte())
        "F" -> Pair(KeyboardKeyCodes.KEY_F, 0.toByte())
        "G" -> Pair(KeyboardKeyCodes.KEY_G, 0.toByte())
        "H" -> Pair(KeyboardKeyCodes.KEY_H, 0.toByte())
        "I" -> Pair(KeyboardKeyCodes.KEY_I, 0.toByte())
        "J" -> Pair(KeyboardKeyCodes.KEY_J, 0.toByte())
        "K" -> Pair(KeyboardKeyCodes.KEY_K, 0.toByte())
        "L" -> Pair(KeyboardKeyCodes.KEY_L, 0.toByte())
        "M" -> Pair(KeyboardKeyCodes.KEY_M, 0.toByte())
        "N" -> Pair(KeyboardKeyCodes.KEY_N, 0.toByte())
        "O" -> Pair(KeyboardKeyCodes.KEY_O, 0.toByte())
        "P" -> Pair(KeyboardKeyCodes.KEY_P, 0.toByte())
        "Q" -> Pair(KeyboardKeyCodes.KEY_Q, 0.toByte())
        "R" -> Pair(KeyboardKeyCodes.KEY_R, 0.toByte())
        "S" -> Pair(KeyboardKeyCodes.KEY_S, 0.toByte())
        "T" -> Pair(KeyboardKeyCodes.KEY_T, 0.toByte())
        "U" -> Pair(KeyboardKeyCodes.KEY_U, 0.toByte())
        "V" -> Pair(KeyboardKeyCodes.KEY_V, 0.toByte())
        "W" -> Pair(KeyboardKeyCodes.KEY_W, 0.toByte())
        "X" -> Pair(KeyboardKeyCodes.KEY_X, 0.toByte())
        "Y" -> Pair(KeyboardKeyCodes.KEY_Y, 0.toByte())
        "Z" -> Pair(KeyboardKeyCodes.KEY_Z, 0.toByte())
        "1" -> Pair(KeyboardKeyCodes.KEY_1, 0.toByte())
        "2" -> Pair(KeyboardKeyCodes.KEY_2, 0.toByte())
        "3" -> Pair(KeyboardKeyCodes.KEY_3, 0.toByte())
        "4" -> Pair(KeyboardKeyCodes.KEY_4, 0.toByte())
        "5" -> Pair(KeyboardKeyCodes.KEY_5, 0.toByte())
        "6" -> Pair(KeyboardKeyCodes.KEY_6, 0.toByte())
        "7" -> Pair(KeyboardKeyCodes.KEY_7, 0.toByte())
        "8" -> Pair(KeyboardKeyCodes.KEY_8, 0.toByte())
        "9" -> Pair(KeyboardKeyCodes.KEY_9, 0.toByte())
        "0" -> Pair(KeyboardKeyCodes.KEY_0, 0.toByte())
        "SPACE" -> Pair(KeyboardKeyCodes.KEY_SPACE, 0.toByte())
        "ENTER" -> Pair(KeyboardKeyCodes.KEY_ENTER, 0.toByte())
        "ESC", "ESCAPE" -> Pair(KeyboardKeyCodes.KEY_ESCAPE, 0.toByte())
        "DEL", "DELETE" -> Pair(KeyboardKeyCodes.KEY_DELETE, 0.toByte())
        "HOME" -> Pair(KeyboardKeyCodes.KEY_HOME, 0.toByte())
        "BACK" -> Pair(KeyboardKeyCodes.KEY_ESCAPE, 0.toByte())
        "MENU" -> Pair(KeyboardKeyCodes.KEY_APPLICATION, 0.toByte())
        "⌫", "BACKSPACE" -> Pair(KeyboardKeyCodes.KEY_BACKSPACE, 0.toByte())
        "TAB" -> Pair(KeyboardKeyCodes.KEY_TAB, 0.toByte())
        "-" -> Pair(KeyboardKeyCodes.KEY_MINUS, 0.toByte())
        "=" -> Pair(KeyboardKeyCodes.KEY_EQUAL, 0.toByte())
        "+" -> Pair(KeyboardKeyCodes.KEY_EQUAL, KeyboardModifiers.LEFT_SHIFT)
        "." -> Pair(KeyboardKeyCodes.KEY_PERIOD, 0.toByte())
        "," -> Pair(KeyboardKeyCodes.KEY_COMMA, 0.toByte())
        "/" -> Pair(KeyboardKeyCodes.KEY_SLASH, 0.toByte())
        "*" -> Pair(KeyboardKeyCodes.KEY_8, KeyboardModifiers.LEFT_SHIFT)
        "!" -> Pair(KeyboardKeyCodes.KEY_1, KeyboardModifiers.LEFT_SHIFT)
        "@" -> Pair(KeyboardKeyCodes.KEY_2, KeyboardModifiers.LEFT_SHIFT)
        "#" -> Pair(KeyboardKeyCodes.KEY_3, KeyboardModifiers.LEFT_SHIFT)
        "$" -> Pair(KeyboardKeyCodes.KEY_4, KeyboardModifiers.LEFT_SHIFT)
        "%" -> Pair(KeyboardKeyCodes.KEY_5, KeyboardModifiers.LEFT_SHIFT)
        "^" -> Pair(KeyboardKeyCodes.KEY_6, KeyboardModifiers.LEFT_SHIFT)
        "&" -> Pair(KeyboardKeyCodes.KEY_7, KeyboardModifiers.LEFT_SHIFT)
        "(" -> Pair(KeyboardKeyCodes.KEY_9, KeyboardModifiers.LEFT_SHIFT)
        ")" -> Pair(KeyboardKeyCodes.KEY_0, KeyboardModifiers.LEFT_SHIFT)
        "LEFT", "←" -> Pair(KeyboardKeyCodes.KEY_LEFT_ARROW, 0.toByte())
        "RIGHT", "→" -> Pair(KeyboardKeyCodes.KEY_RIGHT_ARROW, 0.toByte())
        "UP", "↑" -> Pair(KeyboardKeyCodes.KEY_UP_ARROW, 0.toByte())
        "DOWN", "↓" -> Pair(KeyboardKeyCodes.KEY_DOWN_ARROW, 0.toByte())
        "F1" -> Pair(KeyboardKeyCodes.KEY_F1, 0.toByte())
        "F2" -> Pair(KeyboardKeyCodes.KEY_F2, 0.toByte())
        "F3" -> Pair(KeyboardKeyCodes.KEY_F3, 0.toByte())
        "F4" -> Pair(KeyboardKeyCodes.KEY_F4, 0.toByte())
        "F5" -> Pair(KeyboardKeyCodes.KEY_F5, 0.toByte())
        "F6" -> Pair(KeyboardKeyCodes.KEY_F6, 0.toByte())
        "F7" -> Pair(KeyboardKeyCodes.KEY_F7, 0.toByte())
        "F8" -> Pair(KeyboardKeyCodes.KEY_F8, 0.toByte())
        "F9" -> Pair(KeyboardKeyCodes.KEY_F9, 0.toByte())
        "F10" -> Pair(KeyboardKeyCodes.KEY_F10, 0.toByte())
        "F11" -> Pair(KeyboardKeyCodes.KEY_F11, 0.toByte())
        "F12" -> Pair(KeyboardKeyCodes.KEY_F12, 0.toByte())
        else -> null
    }

    private fun Float.roundToHidByte(): Byte = roundToInt().coerceIn(-127, 127).toByte()
    private fun stickToByte(v: Float): Byte = ((v.coerceIn(-1f, 1f) + 1f) * 127.5f).roundToInt().coerceIn(0, 255).toByte()
}
