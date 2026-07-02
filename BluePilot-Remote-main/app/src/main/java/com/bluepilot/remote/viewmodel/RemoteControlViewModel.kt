package com.bluepilot.remote.viewmodel

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bluepilot.remote.bluetooth.BluetoothHidManager
import com.bluepilot.remote.data.SettingsRepository
import com.bluepilot.remote.model.ConsumerControlCodes
import com.bluepilot.remote.model.DpadDirection
import com.bluepilot.remote.model.GamepadButton
import com.bluepilot.remote.model.GamepadKeyboardMapping
import com.bluepilot.remote.model.GamepadMappingMode
import com.bluepilot.remote.model.GamepadState
import com.bluepilot.remote.model.KeyboardKeyCodes
import com.bluepilot.remote.model.KeyboardModifiers
import com.bluepilot.remote.model.MouseButton
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import kotlin.math.roundToInt

/**
 * Shared HID command ViewModel used by all remote-control screens.
 * It also applies user Settings to real control output: mouse speed, scroll speed,
 * invert scroll, gamepad mapping mode, and haptic feedback.
 */
@HiltViewModel
class RemoteControlViewModel @Inject constructor(
    private val hidManager: BluetoothHidManager,
    private val settingsRepository: SettingsRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    val connectionState = hidManager.connectionState
    val isConnected = hidManager.connectionState.map { it.state.name == "CONNECTED" }

    val appSettings = settingsRepository.appSettings.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        com.bluepilot.remote.model.AppSettings()
    )
    val mouseSettings = settingsRepository.mouseSettings.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        com.bluepilot.remote.model.MouseSettings()
    )
    val keyboardSettings = settingsRepository.keyboardSettings.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        com.bluepilot.remote.model.KeyboardSettings()
    )
    val gamepadSettings = settingsRepository.gamepadSettings.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        com.bluepilot.remote.model.GamepadSettings()
    )

    fun mouseMove(dx: Float, dy: Float) {
        val settings = mouseSettings.value
        val multiplier = 0.35f + (settings.sensitivity / 100f * 1.65f) + (settings.pointerSpeed / 100f * 1.15f)
        val precision = if (settings.penMode) 0.45f else 1f
        val smoothing = 1f - (settings.movementSmoothing.coerceIn(0, 90) / 130f)
        hidManager.sendMouseMove((dx * multiplier * precision * smoothing).roundToHidByte(), (dy * multiplier * precision * smoothing).roundToHidByte())
    }

    fun mouseClick(button: MouseButton = MouseButton.LEFT) {
        haptic()
        hidManager.sendMouseClick(button)
    }

    fun mouseDown(button: MouseButton) {
        haptic()
        hidManager.sendMouseButtonDown(button)
    }

    fun mouseUp(button: MouseButton) = hidManager.sendMouseButtonUp(button)

    fun mouseScroll(amount: Int) {
        val settings = mouseSettings.value
        val multiplier = 0.25f + (settings.scrollSpeed / 100f * 1.75f)
        val direction = if (settings.invertScroll) -1 else 1
        val scaled = (amount * multiplier * direction).roundToInt().coerceIn(-7, 7)
        if (scaled != 0) hidManager.sendMouseScroll(scaled.toByte())
    }

    fun key(code: Byte, modifiers: Byte = 0) {
        haptic()
        hidManager.sendKeyboardKey(code, modifiers)
    }

    fun keyDown(code: Byte, modifiers: Byte = 0) {
        haptic()
        hidManager.sendKeyDown(code, modifiers)
    }

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

    fun text(value: String) {
        haptic()
        hidManager.sendText(value)
    }

    fun enter() = key(KeyboardKeyCodes.KEY_ENTER)
    fun tab() = key(KeyboardKeyCodes.KEY_TAB)
    fun escape() = key(KeyboardKeyCodes.KEY_ESCAPE)
    fun space() = key(KeyboardKeyCodes.KEY_SPACE)
    fun backspace() = key(KeyboardKeyCodes.KEY_BACKSPACE)
    fun delete() = key(KeyboardKeyCodes.KEY_DELETE)
    fun copy() = key(KeyboardKeyCodes.KEY_C, KeyboardModifiers.LEFT_CTRL)
    fun paste() = key(KeyboardKeyCodes.KEY_V, KeyboardModifiers.LEFT_CTRL)
    fun cut() = key(KeyboardKeyCodes.KEY_X, KeyboardModifiers.LEFT_CTRL)
    fun selectAll() = key(KeyboardKeyCodes.KEY_A, KeyboardModifiers.LEFT_CTRL)
    fun save() = key(KeyboardKeyCodes.KEY_S, KeyboardModifiers.LEFT_CTRL)
    fun undo() = key(KeyboardKeyCodes.KEY_Z, KeyboardModifiers.LEFT_CTRL)
    fun redo() = key(KeyboardKeyCodes.KEY_Y, KeyboardModifiers.LEFT_CTRL)
    fun presentationStart() = key(KeyboardKeyCodes.KEY_F5)
    fun presentationBlackScreen() = key(KeyboardKeyCodes.KEY_B)

    fun mediaPlayPause() = consumer(ConsumerControlCodes.PLAY_PAUSE)
    fun mediaStop() = consumer(ConsumerControlCodes.STOP)
    fun mediaNext() = consumer(ConsumerControlCodes.NEXT_TRACK)
    fun mediaPrevious() = consumer(ConsumerControlCodes.PREVIOUS_TRACK)
    fun volumeUp() = consumer(ConsumerControlCodes.VOLUME_UP)
    fun volumeDown() = consumer(ConsumerControlCodes.VOLUME_DOWN)
    fun mute() = consumer(ConsumerControlCodes.MUTE)
    fun brightnessUp() = consumer(ConsumerControlCodes.BRIGHTNESS_UP)
    fun brightnessDown() = consumer(ConsumerControlCodes.BRIGHTNESS_DOWN)

    private fun consumer(code: Short) {
        haptic()
        hidManager.sendConsumerControl(code)
    }

    fun dpad(direction: DpadDirection) {
        haptic()
        when (gamepadSettings.value.gamepadMode) {
            GamepadMappingMode.HID_GAMEPAD -> {
                hidManager.sendGamepadReport(GamepadState(dpadDirection = direction))
                hidManager.sendGamepadReport(GamepadState())
            }
            GamepadMappingMode.KEYBOARD_FALLBACK, GamepadMappingMode.MOUSE_KEYBOARD -> {
                val code = when (direction) {
                    DpadDirection.UP -> KeyboardKeyCodes.KEY_UP_ARROW
                    DpadDirection.DOWN -> KeyboardKeyCodes.KEY_DOWN_ARROW
                    DpadDirection.LEFT -> KeyboardKeyCodes.KEY_LEFT_ARROW
                    DpadDirection.RIGHT -> KeyboardKeyCodes.KEY_RIGHT_ARROW
                    else -> 0
                }
                if (code.toInt() != 0) hidManager.sendKeyboardKey(code)
            }
        }
    }

    fun gamepadButton(button: GamepadButton) {
        haptic(gamepad = true)
        when (gamepadSettings.value.gamepadMode) {
            GamepadMappingMode.HID_GAMEPAD -> {
                hidManager.sendGamepadReport(GamepadState().withButtonPressed(button))
                hidManager.sendGamepadReport(GamepadState())
            }
            GamepadMappingMode.KEYBOARD_FALLBACK -> {
                GamepadKeyboardMapping.DEFAULT_MAPPING[button]?.let { hidManager.sendKeyboardKey(it) }
            }
            GamepadMappingMode.MOUSE_KEYBOARD -> {
                when (button) {
                    GamepadButton.A -> hidManager.sendMouseClick(MouseButton.LEFT)
                    GamepadButton.B -> hidManager.sendMouseClick(MouseButton.RIGHT)
                    GamepadButton.X -> hidManager.sendKeyboardKey(KeyboardKeyCodes.KEY_SPACE)
                    GamepadButton.Y -> hidManager.sendKeyboardKey(KeyboardKeyCodes.KEY_ENTER)
                    GamepadButton.L1 -> hidManager.sendMouseScroll((-3).toByte())
                    GamepadButton.R1 -> hidManager.sendMouseScroll(3.toByte())
                    GamepadButton.START -> hidManager.sendKeyboardKey(KeyboardKeyCodes.KEY_ENTER)
                    GamepadButton.SELECT -> hidManager.sendKeyboardKey(KeyboardKeyCodes.KEY_TAB)
                    GamepadButton.HOME -> hidManager.sendKeyboardKey(KeyboardKeyCodes.KEY_ESCAPE)
                    else -> GamepadKeyboardMapping.DEFAULT_MAPPING[button]?.let { hidManager.sendKeyboardKey(it) }
                }
            }
        }
    }

    fun gamepadAxes(leftX: Float, leftY: Float, rightX: Float = 0f, rightY: Float = 0f) {
        val settings = gamepadSettings.value
        val dead = settings.deadZone / 100f
        val sensitivity = 0.35f + (settings.joystickSensitivity / 100f * 1.3f)
        fun apply(v: Float): Float = if (kotlin.math.abs(v) < dead) 0f else (v * sensitivity).coerceIn(-1f, 1f)
        hidManager.sendGamepadReport(
            GamepadState(
                leftStickX = stickToByte(apply(leftX)),
                leftStickY = stickToByte(apply(leftY)),
                rightStickX = stickToByte(apply(rightX)),
                rightStickY = stickToByte(apply(rightY))
            )
        )
    }

    private fun haptic(gamepad: Boolean = false) {
        if (gamepad && !gamepadSettings.value.hapticFeedback) return
        if (!gamepad && !appSettings.value.touchVibrations) return
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                context.getSystemService(VibratorManager::class.java).defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Vibrator::class.java)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(12, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION") vibrator.vibrate(12)
            }
        } catch (_: Exception) {
        }
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
        "END" -> Pair(KeyboardKeyCodes.KEY_END, 0.toByte())
        "PGUP", "PAGEUP" -> Pair(KeyboardKeyCodes.KEY_PAGE_UP, 0.toByte())
        "PGDN", "PAGEDOWN" -> Pair(KeyboardKeyCodes.KEY_PAGE_DOWN, 0.toByte())
        "BACK" -> Pair(KeyboardKeyCodes.KEY_ESCAPE, 0.toByte())
        "MENU" -> Pair(KeyboardKeyCodes.KEY_APPLICATION, 0.toByte())
        "NUM" -> Pair(KeyboardKeyCodes.KEY_NUM_LOCK, 0.toByte())
        "⌫", "BACKSPACE" -> Pair(KeyboardKeyCodes.KEY_BACKSPACE, 0.toByte())
        "TAB" -> Pair(KeyboardKeyCodes.KEY_TAB, 0.toByte())
        "-" -> Pair(KeyboardKeyCodes.KEY_MINUS, 0.toByte())
        "=" -> Pair(KeyboardKeyCodes.KEY_EQUAL, 0.toByte())
        "+" -> Pair(KeyboardKeyCodes.KEY_KP_PLUS, 0.toByte())
        "." -> Pair(KeyboardKeyCodes.KEY_KP_PERIOD, 0.toByte())
        "," -> Pair(KeyboardKeyCodes.KEY_COMMA, 0.toByte())
        "/" -> Pair(KeyboardKeyCodes.KEY_KP_DIVIDE, 0.toByte())
        "*" -> Pair(KeyboardKeyCodes.KEY_KP_MULTIPLY, 0.toByte())
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
