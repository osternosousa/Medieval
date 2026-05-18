package com.medieval.ui.components

import com.medieval.foundation.ColorM
import com.medieval.managers.GameManager
import com.medieval.ui.foundation.TextPosition
import com.medieval.ui.foundation.UIBaseComponent
import org.lwjgl.glfw.GLFW.GLFW_KEY_BACKSPACE
import org.lwjgl.glfw.GLFW.GLFW_KEY_DELETE
import org.lwjgl.glfw.GLFW.GLFW_KEY_ENTER
import org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE
import org.lwjgl.glfw.GLFW.GLFW_KEY_INSERT
import org.lwjgl.glfw.GLFW.GLFW_KEY_TAB

open class UITextFieldComponent(
    gm: GameManager,
    text: StringBuilder = StringBuilder(2000),
    x: Float = 5f,
    y: Float = 5f,
    width: Float = 250f,
    height: Float = 20f,
    textSize: Float = 8f,
    textColor: ColorM = ColorM.WHITE(),
    backgroundColor: ColorM = ColorM(0.1f, 0.1f, 0.2f, 0.8f),
    textPosition: TextPosition = TextPosition.CENTER,
    initialTopPadding: Float = 0f,
    initialLeftPadding: Float = 4f,
    letterSpacing: Float = 2.0f,
    lineSpacing: Float = 0f,
    showBorder: Boolean = false,
    cornerCurveSize: Float = 0.15f,
    isChecked: Boolean = false,
    canBeMadeFirst: Boolean = true,
    isDraggable: Boolean = false,
    isVisible: Boolean = true,
    isClickable: Boolean = true,
    onUpdate: (ui: UIBaseComponent, dx: Float) -> Unit =  { ui: UIBaseComponent, dx: Float -> },
    onPress: (ui: UIBaseComponent, posX: Float, posY: Float) -> Unit = { ui: UIBaseComponent, posX: Float, posY: Float -> },
    onRelease: (ui: UIBaseComponent) -> Unit = { ui: UIBaseComponent -> },
    onClick: (ui: UIBaseComponent, posX: Float, posY: Float) -> Unit = { ui: UIBaseComponent, posX: Float, posY: Float -> },
    onLongClick: (ui: UIBaseComponent) -> Unit = { ui: UIBaseComponent -> },
    onDrag: (ui: UIBaseComponent, normalizedValueX: Float, normalizedValueY: Float, dx: Float, dy: Float) -> Unit = { ui: UIBaseComponent, value: Float, normalizedValueY: Float, dx: Float, dy: Float -> },
    onHovered: (ui: UIBaseComponent, posX: Float, posY: Float) -> Unit = { ui: UIBaseComponent, posX: Float, posY: Float -> },
    onFocus: (ui: UIBaseComponent, posX: Float, posY: Float) -> Unit = { ui: UIBaseComponent, posX: Float, posY: Float -> },
    onTyping: (ui: UIBaseComponent, scancode: Int) -> Unit = { ui: UIBaseComponent, scancode: Int -> },
    onUpdateViewPort: (ui: UIBaseComponent) -> Unit = { ui: UIBaseComponent -> },
) : UIBaseComponent(
    gm = gm,
    text = text,
    imagePath = "",
    x = x,
    y = y,
    width = width,
    height = height,
    textSize = textSize,
    textColor = textColor,
    backgroundColor = backgroundColor,
    textPosition = textPosition,
    initialTopPadding = initialTopPadding,
    initialLeftPadding = initialLeftPadding,
    letterSpacing = letterSpacing,
    lineSpacing = lineSpacing,
    showBorder = showBorder,
    cornerCurveSize = cornerCurveSize,
    isChecked = isChecked,
    canBeMadeFirst = canBeMadeFirst,
    isDraggable = isDraggable,
    isVisible = isVisible,
    isClickable = isClickable,
    onUpdate = onUpdate,
    onPress = onPress,
    onRelease = onRelease,
    onClick = onClick,
    onLongClick = onLongClick,
    onDrag = onDrag,
    onHovered = onHovered,
    onFocus = onFocus,
    onTyping = onTyping,
    onUpdateViewPort = onUpdateViewPort
) {


    override var uiPrimitiveType: UIPrimitiveType = UIPrimitiveType.UI_TEXT_FIELD_COMPONENT

    private var backspaceCount = 0f

    override fun update(dt: Float) {

        if (isOnFocus) {

            if (gm.inputManager.isKeyTyped(GLFW_KEY_ESCAPE)) {

            } else if (gm.inputManager.isKeyTyped(GLFW_KEY_ENTER)) {

                //text += '\n'
                text.append('\n')
                scheduleComponentUpdate()
            } else if (gm.inputManager.isKeyTyped(GLFW_KEY_TAB)) {

            } else if (gm.inputManager.isKeyPressed(GLFW_KEY_BACKSPACE)) {

                backspaceCount += dt;

                if (backspaceCount > 0.1f) {

                    backspaceCount = 0f;

                    if (!text.isEmpty()) {
                        text.deleteCharAt(text.length - 1)
                        //text = text.substring(0, text.length - 1)
                    }
                    scheduleComponentUpdate()
                }
            } else if (gm.inputManager.isKeyTyped(GLFW_KEY_BACKSPACE)) {

                if (!text.isEmpty()) {
                    text.deleteCharAt(text.length - 1)
                    //text = text.substring(0, text.length - 1)
                }
                scheduleComponentUpdate()
            } else if (gm.inputManager.isKeyTyped(GLFW_KEY_INSERT)) {

            } else if (gm.inputManager.isKeyTyped(GLFW_KEY_DELETE)) {

            }
        }

        super.update(dt)
    }

    override fun initEntity() {
        super.initEntity()

        gm.logMessage(message = "UI Text Field Component created!")
    }

    override fun createUpdateBackgroundAndGlyphMesh() {

        // Campo de texto não fará exibição de imagem.
        if (imagePath.isNotEmpty()) imagePath = ""

        super.createUpdateBackgroundAndGlyphMesh()
    }
    override fun onComponentTyping(scancode: Int) {
        //super.onComponentTyping(scancode)

        text.append(scancode.toChar())
        //text += scancode.toChar()
        scheduleComponentUpdate()

        onTyping(this, scancode)
    }
}