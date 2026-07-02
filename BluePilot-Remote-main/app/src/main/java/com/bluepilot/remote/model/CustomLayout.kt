package com.bluepilot.remote.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Custom layout entity for Room database
 */
@Entity(tableName = "custom_layouts")
data class CustomLayout(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String? = null,
    val portraitComponents: String, // JSON string of components
    val landscapeComponents: String? = null, // JSON string of components (optional)
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false
)

/**
 * Custom component for layout editor
 */
data class CustomComponent(
    val id: String,
    val type: ComponentType,
    val xPercent: Float,
    val yPercent: Float,
    val widthPercent: Float,
    val heightPercent: Float,
    val label: String? = null,
    val icon: String? = null,
    val action: ComponentAction? = null,
    val backgroundColor: String? = null,
    val textColor: String? = null,
    val opacity: Float = 1.0f,
    val cornerRadius: Float = 8f
)

/**
 * Component types for custom layouts
 */
enum class ComponentType {
    BUTTON,
    TOUCHPAD,
    SPEECH_INPUT,
    KEYBOARD_TOGGLE,
    FULLSCREEN_TOGGLE,
    JOYSTICK,
    DPAD,
    MACRO_BUTTON,
    SLIDER,
    LABEL
}

/**
 * Component action types
 */
sealed class ComponentAction {
    data class KeyboardKey(
        val keyCode: Byte,
        val modifiers: Byte = 0
    ) : ComponentAction()

    data class KeyboardShortcut(
        val keys: List<Byte>,
        val modifiers: Byte = 0
    ) : ComponentAction()

    data class SendText(
        val text: String
    ) : ComponentAction()

    data class MouseClick(
        val button: MouseButton
    ) : ComponentAction()

    data class MouseMove(
        val dx: Int,
        val dy: Int
    ) : ComponentAction()

    data class ConsumerControl(
        val code: Short
    ) : ComponentAction()

    data class SystemControl(
        val code: Byte
    ) : ComponentAction()

    data class Macro(
        val macroId: String
    ) : ComponentAction()

    data class Function(
        val functionType: FunctionType
    ) : ComponentAction()
}

/**
 * Function types for components
 */
enum class FunctionType {
    SEARCH,
    POWER,
    SLEEP,
    VOLUME_UP,
    VOLUME_DOWN,
    VOLUME_MUTE,
    BRIGHTNESS_UP,
    BRIGHTNESS_DOWN,
    SCREENSHOT,
    MENU_BACK,
    MENU_HOME,
    MENU_MENU,
    BROWSER_BACK,
    BROWSER_FORWARD,
    BROWSER_REFRESH,
    BROWSER_HOME
}
