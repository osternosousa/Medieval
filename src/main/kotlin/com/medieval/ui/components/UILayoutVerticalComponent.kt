package com.medieval.ui.components

import com.medieval.foundation.ColorM
import com.medieval.managers.GameManager
import com.medieval.ui.foundation.TextPosition
import com.medieval.ui.foundation.UIBaseComponent

class UILayoutVerticalComponent(
    gm: GameManager,
    x: Float = 5f,
    y: Float = 5f,
    width: Float = 100f,
    height: Float = 100f,
    var margin: Float = 3f,
    components: UILayoutVerticalComponent.() -> Unit = { }
) : UIBaseComponent(
    gm = gm,
    x = x,
    y = y,
    width = width,
    height = height,
    backgroundColor = ColorM(0.5f, 0.5f, 0.5f, 0.5f)
) {

    override var uiPrimitiveType: UIPrimitiveType = UIPrimitiveType.UI_LAYOUT_VERTICAL_COMPONENT

    private val uiBaseComponents: MutableList<UIBaseComponent> = mutableListOf()

    init {

        components()

        updateComponentsSizePosition()
    }

    fun addComponent(component: UIBaseComponent) {

        uiBaseComponents.add(component)
    }

    fun updateComponentsSizePosition() {

        val uiX: Float = this.margin + this.x
        var uiY: Float = this.margin + this.y + 5f

        for (ui in uiBaseComponents) {

            if (!ui.isVisible) continue

            ui.x = uiX
            ui.y = uiY
            ui.width = this.width - margin * 2f
            uiY = ui.y + ui.height + margin

            ui.parent = this
            ui.isDraggable = false

            ui.scheduleComponentUpdate()
        }

        this.height = uiY - this.y
        this.scheduleComponentUpdate()

        // A atuaLização dos valores de x/y e width/height será realizada pelo
        // update agendado por scheduleComponentUpdate() no update da entity.
    }

    override fun update(dt: Float) {

        super.update(dt)
    }

    override fun initEntity() {

        super.initEntity()

        gm.logMessage(message = "UI Layout Vertical Component created!")
    }

    override fun createUpdateBackgroundAndGlyphMesh() {

        if (imagePath.isNotEmpty()) imagePath = ""
        if (text.isNotEmpty()) {
            //text = ""
            text.clear()
        }

        updateComponentsSizePosition()

        super.createUpdateBackgroundAndGlyphMesh()
    }

    override fun onComponentDrag(deltaX: Float, deltaY: Float, xPos: Float, yPos: Float) {

        if (!this.isDraggable) return

        for (ui in uiBaseComponents) {
            ui.onParentDrag(deltaX = deltaX, deltaY = deltaY, xPos = xPos, yPos = yPos)
        }

        super.onComponentDrag(deltaX, deltaY, xPos, yPos)
    }
}

fun UILayoutVerticalComponent.uiButtonComponent(
    text: StringBuilder = StringBuilder(2000),
    //x: Float = 5f,    // Will be set by the layout
    //y: Float = 5f,   // Will be set by the layout
    //width: Float = 250f,   // Will be set by the layout
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
): UIButtonComponent {

    val buttonComponent = UIButtonComponent(
        gm = this.gm,
        text = text,
        //x = x,    // Will be set by the layout
        //y = y,    // Will be set by the layout
        //width = width,    // Will be set by the layout
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
    )

    this.addComponent(buttonComponent)

    return buttonComponent
}

fun UILayoutVerticalComponent.uiCheckBoxComponent(
    text: StringBuilder = StringBuilder(2000) ,
    //x: Float = 5f,    // Will be set by the layout
    //y: Float = 5f,    // Will be set by the layout
    //width: Float = 250f,  // Will be set by the layout
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
): UICheckBoxComponent {

    val checkBox = UICheckBoxComponent(
        gm = this.gm,
        text = text,
        //x = x,    // Will be set by the layout
        //y = y,    // Will be set by the layout
        //width = width,    // Will be set by the layout
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
    )

    this.addComponent(component = checkBox)

    return checkBox
}

fun UILayoutVerticalComponent.uiColorPickerComponent(
    //x: Float = 5f,    // Will be set by the layout
    //y: Float = 5f,    // Will be set by the layout
    //width: Float = 250f,  // Will be set by the layout
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
    onDragColorPicker: (ui: UIBaseComponent, r: Float, g: Float, b: Float, normalizedValueX: Float, normalizedValueY: Float, dx: Float, dy: Float) -> Unit = { ui: UIBaseComponent, r: Float, g: Float, b: Float, value: Float, normalizedValueY: Float, dx: Float, dy: Float -> },
    onHovered: (ui: UIBaseComponent, posX: Float, posY: Float) -> Unit = { ui: UIBaseComponent, posX: Float, posY: Float -> },
    onFocus: (ui: UIBaseComponent, posX: Float, posY: Float) -> Unit = { ui: UIBaseComponent, posX: Float, posY: Float -> },
    onUpdateViewPort: (ui: UIBaseComponent) -> Unit = { ui: UIBaseComponent -> },
): UIColorPickerComponent {

    val colorPicker = UIColorPickerComponent(
        gm = this.gm,
        //x = x,    // Will be set by the layout
        //y = y,    // Will be set by the layout
        //width = width,    // Will be set by the layout
        height = height,
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
        onDragColorPicker = onDragColorPicker,
        onHovered = onHovered,
        onFocus = onFocus,
        onUpdateViewPort = onUpdateViewPort,
    )

    this.addComponent(component = colorPicker)

    return colorPicker
}

fun UILayoutVerticalComponent.uiImageComponent(
    imagePath: String = "",
    //x: Float = 5f,    // Will be set by the layout
    //y: Float = 5f,    // Will be set by the layout
    //width: Float = 250f,  // Will be set by the layout
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
): UIImageComponent {

    val image = UIImageComponent(
        gm = this.gm,
        imagePath = imagePath,
        //x = x,    // Will be set by the layout
        //y = y,    // Will be set by the layout
        //width = width,    // Will be set by the layout
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
    )

    this.addComponent(component = image)

    return image
}

fun UILayoutVerticalComponent.uiSliderComponent(
    text: StringBuilder = StringBuilder(2000),
    initialValueX: Float = 0f,
    initialValueY: Float = 0f,
    //x = x,    // Will be set by the layout
    //y = y,    // Will be set by the layout
    //width = width,    // Will be set by the layout
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
): UISliderComponent {


    val sliderComponent = UISliderComponent(
        gm = this.gm,
        text = text,
        initialValueX = initialValueX,
        initialValueY = initialValueY,
        //x = x,    // Will be set by the layout
        //y = y,    // Will be set by the layout
        //width = width,    // Will be set by the layout
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
    )

    this.addComponent(component = sliderComponent)

    return sliderComponent
}

fun UILayoutVerticalComponent.uiSpacerComponent(
    width: Float = 250f,
    height: Float = 20f,
): UISpacerComponent {

    val spacer = UISpacerComponent(
        gm = this.gm,
        width = width,
        height = height
    )

    this.addComponent(component = spacer)

    return spacer
}

fun UILayoutVerticalComponent.uiTextFieldComponent(
    text: StringBuilder = StringBuilder(2000),
    //x: Float = 5f,    //Will be set by the layout
    //y: Float = 5f,    //Will be set by the layout
    //width: Float = 250f,  //Will be set by the layout
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
) : UITextFieldComponent {

    val textField = UITextFieldComponent(
        gm = this.gm,
        text = text,
        //x = x,    //Will be set by the layout
        //y = y,    //Will be set by the layout
        //width = width,    //Will be set by the layout
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
    )

    this.addComponent(component = textField)

    return textField
}