package com.medieval.ui.components

import com.medieval.foundation.ColorM
import com.medieval.managers.GameManager
import com.medieval.ui.foundation.TextPosition
import com.medieval.ui.foundation.UIBaseComponent
import java.lang.Math.clamp

class UISliderComponent(
    gm: GameManager,
    text: StringBuilder = StringBuilder(2000),
    initialValueX: Float = 0f,
    initialValueY: Float = 0f,
    x: Float = 5f,
    y: Float = 5f,
    width: Float = 250f,
    height: Float = 20f,
    textSize: Float = 8f,
    textColor: ColorM = ColorM.WHITE(),
    backgroundColor: ColorM = ColorM(0.1f, 0.1f, 0.2f, 0.8f),
    textPosition: TextPosition = TextPosition.LEFT,
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
    onUpdateViewPort = onUpdateViewPort,
) {

    override var uiPrimitiveType: UIPrimitiveType = UIPrimitiveType.UI_SLIDER_COMPONENT

    init {
        val maxValueX = (1 - sliderButtonWidth)
        val maxValueY = (1 - sliderButtonHeight)

        sliderNormalizedValueX = clamp(initialValueX, 0f, maxValueX)
        sliderNormalizedValueY = clamp(initialValueY, 0f, maxValueY)
    }

    override fun initEntity() {

        super.initEntity()

        gm.logMessage(message = "UI Slider Component created!")
    }

    override fun createUpdateBackgroundAndGlyphMesh() {

        // Como se trata de um simples botão, não iremos permitir renderização de imagem.
        // A inclusão de imagem no mesh ocorre apenas quando o imagePath é não-vazio.
        if (imagePath.isNotEmpty()) imagePath = ""

        super.createUpdateBackgroundAndGlyphMesh()
    }

    override fun onComponentDrag(deltaX: Float, deltaY: Float, xPos: Float, yPos: Float) {
        //super.onComponentDrag(deltaX, deltaY, xPos, yPos)

        val maxValueX = (1 - sliderButtonWidth)
        val maxValueY = (1 - sliderButtonHeight)

        sliderNormalizedValueX = clamp((xPos - x) / width - sliderButtonWidth / 2f, 0f, maxValueX)
        sliderNormalizedValueY = clamp((yPos - y) / height - sliderButtonHeight / 2f, 0f, maxValueY)

        onDrag(
            this,
            sliderNormalizedValueX / maxValueX,
            sliderNormalizedValueY / maxValueY,
            deltaX,
            deltaY
        );

        if (isDraggable) {

            x += deltaX
            y += deltaY

            bounds.x = x
            bounds.y = y

            modelMatrix.identity();
            modelMatrix.translate(x, y, 0f)
        }
    }
}