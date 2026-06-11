package com.medieval.foundation

/** Classes that implement this interface can be subscribed to the InputManager in order
 * to receive input events from mouse and keypad. */
interface InputManagerClient {

    fun onMove(deltaX: Double, deltaY: Double, posX: Double, posY: Double, isCursorEnabled: Boolean)
    fun onPress(posX: Double, posY: Double, isCursorEnabled: Boolean)
    fun onClick(posX: Double, posY: Double, isCursorEnabled: Boolean)
    fun onLongClick(posX: Double, posY: Double, isCursorEnabled: Boolean)
    fun onTyping(codepoint: Int, isCursorEnabled: Boolean)
    fun onDrag(deltaX: Double, deltaY: Double, posX: Double, posY: Double, isCursorEnabled: Boolean)
    fun onFocus(focused: Boolean, isCursorEnabled: Boolean)
}