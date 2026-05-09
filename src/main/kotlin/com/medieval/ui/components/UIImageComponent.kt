package com.medieval.ui.components

import com.medieval.foundation.ColorM
import com.medieval.managers.GameManager
import com.medieval.ui.foundation.UIBaseComponent

class UIImageComponent(
    gm: GameManager,
    imagePath: String = "",
    x: Float = 5f,
    y: Float = 5f,
    width: Float = 250f,
    height: Float = 20f,
    backgroundColor: ColorM = ColorM(0.1f, 0.1f, 0.2f, 0.8f),
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
    imagePath = imagePath,
    x = x,
    y = y,
    width = width,
    height = height,
    backgroundColor = backgroundColor,
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

    override var uiPrimitiveType: UIPrimitiveType = UIPrimitiveType.UI_IMAGE_COMPONENT


    override fun initEntity() {

        super.initEntity()

        gm.logMessage(message = "UI Image Component created!")
    }

    override fun createUpdateBackgroundAndGlyphMesh() {

        // Como se trata de um simples botão, não iremos permitir renderização de imagem.
        // A inclusão de imagem no mesh ocorre apenas quando o imagePath é não-vazio.
        if (text.isNotEmpty()) text = ""

        super.createUpdateBackgroundAndGlyphMesh()
    }
}