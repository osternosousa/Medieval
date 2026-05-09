package com.medieval.foundation

interface InputManagerClient {

    fun onMove(deltaX: Double, deltaY: Double, posX: Double, posY: Double, isCursorEnabled: Boolean)
    fun onPress(posX: Double, posY: Double, isCursorEnabled: Boolean)
    fun onClick(posX: Double, posY: Double, isCursorEnabled: Boolean)
    fun onLongClick(posX: Double, posY: Double, isCursorEnabled: Boolean)
    fun onTyping(codepoint: Int, isCursorEnabled: Boolean)
    fun onDrag(deltaX: Double, deltaY: Double, posX: Double, posY: Double, isCursorEnabled: Boolean)
    fun onFocus(focused: Boolean, isCursorEnabled: Boolean)
}