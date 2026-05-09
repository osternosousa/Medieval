package com.medieval.ui.foundation

import com.medieval.foundation.InputManagerClient

object UIComponentManager : InputManagerClient {

    private var activeComponentDrag: UIBaseComponent? = null
    private var activeComponentOnFocus: UIBaseComponent? = null
    private var activeComponentOnHover: UIBaseComponent? = null
    private var activeComponentOnHold: UIBaseComponent? = null

    private val uiComponents: MutableList<UIBaseComponent> = mutableListOf<UIBaseComponent>()

    private var isDragging: Boolean = false

    fun subscribe(component: UIBaseComponent) {
        uiComponents.add(component)
    }

    fun unsubscribe(component: UIBaseComponent) {
        uiComponents.remove(component)
    }

    fun identifyPressedComponent(xPos: Double, yPos: Double) {
        clearActiveComponent()

        for (index in uiComponents.indices.reversed()) {
            if (uiComponents[index].bounds.contains(xPos, yPos)) {

                activeComponentDrag = uiComponents[index]
                activeComponentOnFocus = activeComponentDrag
                activeComponentOnFocus?.isOnFocus = true

                break
            }
        }
    }

    fun processClickOnSubscriber(xPos: Double, yPos: Double) {
        if (activeComponentDrag != null && !isDragging) {
            activeComponentDrag?.isClicked = true
            activeComponentDrag?.onComponentClick(xPos.toFloat(), yPos.toFloat())
            activeComponentDrag = null
        } else {
            isDragging = false
            activeComponentDrag = null
        }
    }

    fun processDragOnSubscriber(deltaX: Float, deltaY: Float, xPos: Float, yPos: Float) {
        if (activeComponentDrag != null) {
            isDragging = true
            activeComponentDrag?.onComponentDrag(deltaX, deltaY, xPos, yPos)
        }
    }

    fun processDragOnSubscriber(deltaX: Double, deltaY: Double, xPos: Double, yPos: Double) {
        processDragOnSubscriber(deltaX.toFloat(), deltaY.toFloat(), xPos.toFloat(), yPos.toFloat())
    }

    fun processTypingOnComponent(scancode: Int) {
        if (activeComponentOnFocus != null) {
            activeComponentOnFocus?.onComponentTyping(scancode)
        }
    }

    fun processHoverOnSubscriber(xPos: Double, yPos: Double) {
        if (isDragging) return

        var hoverFound = false

        for (index in uiComponents.indices.reversed()) {
            if (!hoverFound && uiComponents.get(index).bounds.contains(xPos, yPos)) {
                uiComponents.get(index).isHovered = true
                uiComponents.get(index).onComponentHovered(xPos.toFloat(), yPos.toFloat())
                activeComponentOnHover = uiComponents.get(index)
                hoverFound = true
            } else {
                uiComponents.get(index).isHovered = false
            }
        }

        if (!hoverFound) activeComponentOnHover = null
    }

    fun clearActiveComponent() {
        if (activeComponentDrag != null) activeComponentDrag?.isHovered = false
        if (activeComponentDrag != null) activeComponentDrag?.isOnFocus = false
        if (activeComponentOnFocus != null) activeComponentOnFocus?.isHovered = false
        if (activeComponentOnFocus != null) activeComponentOnFocus?.isOnFocus = false
        if (activeComponentOnHover != null) activeComponentOnHover?.isHovered = false
        if (activeComponentOnHover != null) activeComponentOnHover?.isOnFocus = false
        if (activeComponentOnHold != null) activeComponentOnHold?.isLongClicking = false

        activeComponentDrag = null
        activeComponentOnFocus = null
        activeComponentOnHover = null
        activeComponentOnHold = null
    }

    override fun onMove(deltaX: Double, deltaY: Double, posX: Double, posY: Double, isCursorEnabled: Boolean) {
        if (!isCursorEnabled) return
        processHoverOnSubscriber(posX, posY)
    }

    override fun onPress(posX: Double, posY: Double, isCursorEnabled: Boolean) {
        if (!isCursorEnabled) return
        identifyPressedComponent(posX, posY)
    }

    override fun onClick(posX: Double, posY: Double, isCursorEnabled: Boolean) {
        if (!isCursorEnabled) return
        processClickOnSubscriber(posX, posY)
    }

    override fun onLongClick(posX: Double, posY: Double, isCursorEnabled: Boolean) {
        if (!isCursorEnabled) return
    }

    override fun onTyping(codepoint: Int, isCursorEnabled: Boolean) {
        if (!isCursorEnabled) return
        processTypingOnComponent(codepoint)
    }

    override fun onDrag(deltaX: Double, deltaY: Double, posX: Double, posY: Double, isCursorEnabled: Boolean) {
        if (!isCursorEnabled) return
        processDragOnSubscriber(deltaX, deltaY, posX, posY)
    }

    override fun onFocus(focused: Boolean, isCursorEnabled: Boolean) {
        if (!isCursorEnabled) return
        if (!focused) clearActiveComponent()
    }
}