package com.medieval.ui.components

import com.medieval.foundation.ColorM
import com.medieval.managers.GameManager
import com.medieval.ui.foundation.UIBaseComponent
import com.medieval.utility.MathM

class UIColorPickerComponent(
    gm: GameManager,
    x: Float = 5f,
    y: Float = 5f,
    width: Float = 250f,
    height: Float = 20f,
    backgroundColor: ColorM = ColorM(0.1f, 0.1f, 0.2f, 0.8f),
    showBorder: Boolean = false,
    cornerCurveSize: Float = 0.15f,
    canBeMadeFirst: Boolean = true,
    isDraggable: Boolean = false,
    isVisible: Boolean = true,
    isClickable: Boolean = true,
    onUpdate: (ui: UIBaseComponent, dx: Float) -> Unit =  { ui: UIBaseComponent, dx: Float -> },
    onPress: (ui: UIBaseComponent, posX: Float, posY: Float) -> Unit = { ui: UIBaseComponent, posX: Float, posY: Float -> },
    onRelease: (ui: UIBaseComponent) -> Unit = { ui: UIBaseComponent -> },
    onClick: (ui: UIBaseComponent, posX: Float, posY: Float) -> Unit = { ui: UIBaseComponent, posX: Float, posY: Float -> },
    onLongClick: (ui: UIBaseComponent) -> Unit = { ui: UIBaseComponent -> },
    val onDragColorPicker: (ui: UIBaseComponent, r: Float, g: Float, b: Float, normalizedValueX: Float, normalizedValueY: Float, dx: Float, dy: Float) -> Unit = { ui: UIBaseComponent, r: Float, g: Float, b: Float, value: Float, normalizedValueY: Float, dx: Float, dy: Float -> },
    onHovered: (ui: UIBaseComponent, posX: Float, posY: Float) -> Unit = { ui: UIBaseComponent, posX: Float, posY: Float -> },
    onFocus: (ui: UIBaseComponent, posX: Float, posY: Float) -> Unit = { ui: UIBaseComponent, posX: Float, posY: Float -> },
    onUpdateViewPort: (ui: UIBaseComponent) -> Unit = { ui: UIBaseComponent -> },
) : UIBaseComponent(
    gm = gm,
    x = x,
    y = y,
    width = width,
    height = height,
    textSize = 0f,
    backgroundColor = backgroundColor,
    showBorder = showBorder,
    cornerCurveSize = cornerCurveSize,
    canBeMadeFirst = canBeMadeFirst,
    isDraggable = isDraggable,
    isVisible = isVisible,
    isClickable = isClickable,
    onUpdate = onUpdate,
    onPress = onPress,
    onRelease = onRelease,
    onClick = onClick,
    onLongClick = onLongClick,
    onHovered = onHovered,
    onFocus = onFocus,
    onUpdateViewPort = onUpdateViewPort,
) {

    override var uiPrimitiveType: UIPrimitiveType = UIPrimitiveType.UI_COLOR_PICKER_COMPONENT

    override fun initEntity() {

        super.initEntity()

        gm.logMessage(message = "UI Color Picker Component created!")
    }

    override fun createUpdateBackgroundAndGlyphMesh() {

        // Como se trata de um simples botão, não iremos permitir renderização de imagem.
        // A inclusão de imagem no mesh ocorre apenas quando o imagePath é não-vazio.
        if (imagePath.isNotEmpty()) imagePath = ""
        if (text.isNotEmpty()) text = ""

        super.createUpdateBackgroundAndGlyphMesh()
    }

    private val colorBlack = ColorM.BLACK()
    private val colorWhite = ColorM.WHITE()

    override fun onComponentDrag(deltaX: Float, deltaY: Float, xPos: Float, yPos: Float) {
        //super.onComponentDrag(deltaX, deltaY, xPos, yPos)

        sliderNormalizedValueX = Math.clamp((xPos - x) / width, 0f, 1f)
        sliderNormalizedValueY = Math.clamp((yPos - y) / height, 0f, 1f)

        var r = MathM.mix(colorWhite.red, backgroundColor.red, sliderNormalizedValueX)
        r = MathM.mix(r, colorBlack.red, sliderNormalizedValueY)

        var g = MathM.mix(colorWhite.green, backgroundColor.green, sliderNormalizedValueX)
        g = MathM.mix(g, colorBlack.green, sliderNormalizedValueY)

        var b = MathM.mix(colorWhite.blue, backgroundColor.blue, sliderNormalizedValueX)
        b = MathM.mix(b, colorBlack.blue, sliderNormalizedValueY)

        onDragColorPicker(
            this,
            r,
            g,
            b,
            sliderNormalizedValueX,
            sliderNormalizedValueY,
            deltaX,
            deltaY
        )

        if (isDraggable) {

            x += deltaX
            y += deltaY

            bounds.x = x
            bounds.y = y

            modelMatrix.identity()
            modelMatrix.translate(x, y, 0f)
        }
    }
}