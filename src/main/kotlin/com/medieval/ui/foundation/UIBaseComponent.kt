package com.medieval.ui.foundation

import com.medieval.foundation.ColorM
import com.medieval.foundation.Entity
import com.medieval.foundation.RectangleM
import com.medieval.managers.GameManager
import com.medieval.opengl.IndexBufferObject
import com.medieval.opengl.ProgramShaderObject
import com.medieval.opengl.Texture2DBufferObject
import com.medieval.opengl.VertexArrayObject
import com.medieval.opengl.VertexBufferObject
import com.medieval.utility.UtilityM
import org.joml.Matrix4f
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW

abstract class UIBaseComponent(
    val gm: GameManager,
    var text: String = "",
    var imagePath: String = "",
    var x: Float = 5f,
    var y: Float = 5f,
    var width: Float = 250f,
    var height: Float = 20f,
    var textSize: Float = 8f,
    var textColor: ColorM = ColorM.WHITE(),
    var backgroundColor: ColorM = ColorM(0.1f, 0.1f, 0.2f, 0.8f),
    var textPosition: TextPosition = TextPosition.CENTER,
    var initialTopPadding: Float = 0f,
    var initialLeftPadding: Float = 4f,
    var letterSpacing: Float = 2.0f,
    var lineSpacing: Float = 0f,
    var showBorder: Boolean = false,
    var cornerCurveSize: Float = 0.15f,
    var isChecked: Boolean = false,
    var canBeMadeFirst: Boolean = true,
    var isDraggable: Boolean = false,
    var isVisible: Boolean = true,
    var isClickable: Boolean = true,
    var onUpdate: (ui: UIBaseComponent, dx: Float) -> Unit =  { ui: UIBaseComponent, dx: Float -> },
    var onPress: (ui: UIBaseComponent, posX: Float, posY: Float) -> Unit = { ui: UIBaseComponent, posX: Float, posY: Float -> },
    var onRelease: (ui: UIBaseComponent) -> Unit = { ui: UIBaseComponent -> },
    var onClick: (ui: UIBaseComponent, posX: Float, posY: Float) -> Unit = { ui: UIBaseComponent, posX: Float, posY: Float -> },
    var onLongClick: (ui: UIBaseComponent) -> Unit = { ui: UIBaseComponent -> },
    var onDrag: (ui: UIBaseComponent, normalizedValueX: Float, normalizedValueY: Float, dx: Float, dy: Float) -> Unit = { ui: UIBaseComponent, value: Float, normalizedValueY: Float, dx: Float, dy: Float -> },
    var onHovered: (ui: UIBaseComponent, posX: Float, posY: Float) -> Unit = { ui: UIBaseComponent, posX: Float, posY: Float -> },
    var onFocus: (ui: UIBaseComponent, posX: Float, posY: Float) -> Unit = { ui: UIBaseComponent, posX: Float, posY: Float -> },
    var onTyping: (ui: UIBaseComponent, scancode: Int) -> Unit = { ui: UIBaseComponent, scancode: Int -> },
    var onUpdateViewPort: (ui: UIBaseComponent) -> Unit = { ui: UIBaseComponent -> },
) : Entity(){

    open var uiPrimitiveType: UIPrimitiveType = UIPrimitiveType.UI_BASE_COMPONENT

    companion object {

        // Used by all components.
        val PID: ProgramShaderObject = ProgramShaderObject()

        val baseVertices: FloatArray = floatArrayOf(
            // XYZ           RGBA                        UV          TEXTURE INDEX   BORDER SIZE
            +1.0f, +0.0f, 0.0f, 0.3f, 0.8f, 1.0f, 1f, 0f, 256f, 0f,
            +0.0f, +0.0f, 0.0f, 0.3f, 0.8f, 1.0f, 0f, 0f, 256f, 0f,
            +0.0f, -1.0f, 0.0f, 0.3f, 0.8f, 1.0f, 0f, 1f, 256f, 0f,
            +1.0f, -1.0f, 0.0f, 0.3f, 0.8f, 1.0f, 1f, 1f, 256f, 0f,
        )

        val baseIndices: IntArray = intArrayOf(
            0, 1, 2,
            2, 3, 0,
        )

        var shaderSourceFilePath: String = "programs/uibasecomponent.glsl"
        var indicesComponents: IntArray = intArrayOf(2, 4, 2, 1, 1)
        var type: Int = GL11.GL_FLOAT

        private var uOrthoMatrixLoc: Int = -1
        private var uModelMatrixLoc: Int = -1
        private var uBaseUIStatesLoc: Int = -1
        private var uiPrimitiveTypeLoc: Int = -1
        private var uIsCheckedLoc: Int = -1
        private var uValuesPrimitiveTypeIsCheckViewPortWidthHeightLoc: Int = -1
        private var uiPositionSizeValuesLoc: Int = -1
        private var uiSliderValuesLoc: Int = -1
    }

    var bounds: RectangleM = RectangleM()
    var parent: UIBaseComponent? = null

    // STATES DINÂMICOS DA UI
    // =================================================================
    var isHovered: Boolean = false
    var isOnFocus: Boolean = false
    var isLongClicking: Boolean = false
    var isShowingBorder: Boolean = false
    var isClicked: Boolean = false

    var sliderNormalizedValueX: Float = 0f
    var sliderNormalizedValueY: Float = 0f
    var sliderButtonWidth: Float = 0.05f
    var sliderButtonHeight: Float = 0.4f

    // =================================================================
    private var isClickedCounter = 0f

    private lateinit var vertices: FloatArray
    private lateinit var indices: IntArray
    private var verticesArraySize = 0
    private var indicesArraySize = 0

    private var hasToPerformUpdate = false

    // Exclusive to each component.
    val VAO: VertexArrayObject = VertexArrayObject()
    val VBO: VertexBufferObject = VertexBufferObject()
    val EBO: IndexBufferObject = IndexBufferObject()
    var TBO: Texture2DBufferObject = Texture2DBufferObject()

    val modelMatrix: Matrix4f = Matrix4f()

    init {

        bounds.x = x
        bounds.y = y
        bounds.width = width
        bounds.height = height

        if (!imagePath.isEmpty()) {

            TBO.init(
                imagePath,
                true,
                gm.resourcesManager.TEXTURE_UNIT_FREE_USE_15
            )
        }

        modelMatrix.translate(x, y, 0f)
        gm.inputManager.subscribeClient(client = UIComponentManager)
        UIComponentManager.subscribe(component = this)
        gm.rendererManager.subscribeEntityInit(entity = this)
    }

    override fun update(dt: Float) {

        onUpdate(this, dt);

        if(hasToPerformUpdate) {

            createUpdateBackgroundAndGlyphMesh();

            VAO.bind()

            VBO.bind()
            VBO.bufferDataOrphaning(vertices, GL_DYNAMIC_DRAW)

            EBO.bind()
            EBO.bufferDataOrphaning(indices, GL_DYNAMIC_DRAW)

            VAO.unbind()
            VBO.unbind()
            EBO.unbind()

            // Atualização dos valores de x/y e width/height é importante neste ponto
            // devido a mudanças realizadas pelo Layout parent gerenciando o componente.
            bounds.x = x
            bounds.y = y
            bounds.width = width
            bounds.height = height

            modelMatrix.identity()
            modelMatrix.translate(x, y, 0f)
        }

        if (isClicked) {
            isClickedCounter += dt
            if (isClickedCounter > 0.100) {
                isClickedCounter = 0f
                isClicked = false;
            }
        }
    }

    override fun initEntity() {

        createUpdateBackgroundAndGlyphMesh()

        VAO.init()
        VAO.bind()

        VBO.init()
        VBO.bind()
        VBO.bufferData(data = vertices)

        EBO.init()
        EBO.bind()
        EBO.bufferData(data = indices)

        VAO.enableAndSetAllVertexAttributes(indicesComponents = indicesComponents, type = type)

        //if (!imagePath.isEmpty()) TBO.init()

        VAO.unbind()
        VBO.unbind()
        EBO.unbind()

        if (!imagePath.isEmpty()) TBO.unbind()

        if (PID.PID_ID == -1) {

            val shaders = UtilityM.getShadersFromTextFile(path = shaderSourceFilePath)

            val vs = shaders[0]
            val fs = shaders[1]

            PID.init(vs, fs)
            PID.bindUseProgram()
            PID.setUniformInt("uSampler2DArrayGlyphs", gm.resourcesManager.TBO_1_2D_ARRAY_GLYPHS_ARRAY_SAMPLER_VALUE)
            PID.setUniformInt("uSampler2DImage", gm.resourcesManager.TEXTURE_UNIT_FREE_USE_15_SAMPLER_ID)

            uOrthoMatrixLoc = PID.getUniformLocation(name = "uOrthoMatrix")
            uModelMatrixLoc = PID.getUniformLocation(name = "uModelMatrix")
            uBaseUIStatesLoc = PID.getUniformLocation(name = "uBaseUIStates")
            uiPrimitiveTypeLoc = PID.getUniformLocation(name = "uiPrimitiveType")
            uIsCheckedLoc = PID.getUniformLocation(name = "uIsChecked")
            uValuesPrimitiveTypeIsCheckViewPortWidthHeightLoc = PID.getUniformLocation(name = "uValuesPrimitiveTypeIsCheckViewPortWidthHeight")
            uiPositionSizeValuesLoc = PID.getUniformLocation(name = "uiPositionSizeValues")
            uiSliderValuesLoc = PID.getUniformLocation(name = "uiSliderValues")

            gm.logMessage(message = "UI_BASE_COMPONENT -> \n${PID.log}")
        }

//        PID.setUniformFloat("uSliderNormalizedValueX", sliderNormalizedValueX)
//        PID.setUniformFloat("uSliderNormalizedValueY", sliderNormalizedValueY)
//        PID.setUniformFloat("uSliderButtonWidth", sliderButtonWidth)
//        PID.setUniformFloat("uSliderButtonHeight", sliderButtonHeight)
//        PID.setUniformVector2f("uUISizeWidthHeigh", width, height)

        PID.bindUseProgram()
        PID.setUniformInt(uiPrimitiveTypeLoc, uiPrimitiveType.id)
        PID.setUniformInt(uIsCheckedLoc, if (isChecked)  1 else 0)
        PID.setUniformVector4f(uiPositionSizeValuesLoc, x, y, width, height)
        PID.setUniformVector4f(uiSliderValuesLoc, sliderNormalizedValueX, sliderNormalizedValueY, sliderButtonWidth, sliderButtonHeight)
        PID.unbindUseProgram()

        gm.rendererManager.subscribeEntityUI(entity = this)
    }

    open fun scheduleComponentUpdate() {

        if (hasToPerformUpdate) return
        hasToPerformUpdate = true
    }

    open fun createUpdateBackgroundAndGlyphMesh() {

        val charList = text.toList()
        var breakLineCount = 0

        for (char in charList) {
            if (char.code == 10) breakLineCount++
        }

        var backgroundAndImageCount = 1
        if (!imagePath.isEmpty()) backgroundAndImageCount++


        // One or two more, because the first one (0 - zero index) will render background.
        // The second one will be to render image.
        val verticesBufferSize = charList.size + backgroundAndImageCount - breakLineCount

        vertices = FloatArray(verticesBufferSize * baseVertices.size)
        indices = IntArray(verticesBufferSize * baseIndices.size)

        updateVerticesIndicesSize()

        val glyphWidth = textSize
        val glyphHeight = textSize * 1.5f

        // Inclui background.
        buildQuad(
            vertexIndexOffset = 0,
            x = 0f,
            y = 0f,
            quadWidth = width,
            quadHeight = height,
            lettersSpacing = 0f,
            glyphTextureIndex = 256f,
            quadColor = backgroundColor,
        )

        // Inclui imagem, caso tenha sido fornecido um image path.
        // TextureIndex 257 é para uso da imagem individual.
        if (backgroundAndImageCount == 2) {
            buildQuad(
                vertexIndexOffset = 1,
                x = 0f,
                y = 0f,
                quadWidth = width,
                quadHeight = height,
                lettersSpacing = 0f,
                glyphTextureIndex = 257f,
                quadColor = ColorM.TRANSPARENT(),
            )
        }

        var localInitialLeftPadding = 0f

        // Posição inicial x da linha de texto conforme diretiva em textPosition.
        var initialPositionX = 0f

        // Posição inicial y da linha de texto conforme diretiva em textPosition.
        var initialPositionY = 0f

        // Calcula o tamanho da primeira linha e o tamanho total
        // de todas as linahs.
        if (charList.isNotEmpty()) {

            // Vamos calcular o height total baseado no total de linhas por '\n'.
            // E também o width da primeira linha para posicionar o texto no centro,
            // Tanto da primeira linha em relação ao seu width quanto em relação
            // ao total de todas as linhas.
            var lineWidth = 0.0f
            var lineHeight = glyphHeight + lineSpacing

            var tempLineWidth = lineWidth
            var tempLineHeight = lineHeight

            var hasLineBreak = false

            for (c in charList) {
                if (c.code != 10 && !hasLineBreak) {
                    // Caso seja caractere e não quebra de linha, expande width da linha atual, mas
                    // apenas para a primeira linha, pois hasLineBreak não deixa computar a informação
                    // para as linhas seguintes, pois aqui lineWidth é apenas para posicionar x para a
                    // primeira linha. As demais serão posicionas em seguida.
                    tempLineWidth += glyphWidth + letterSpacing
                }
                if (c.code == 10) {
                    // Caso seja quebra de linha, expande height da linha.
                    // Caso a linha atual tenha ficado maior do que a anterior, atualiza o tamanho maior,
                    // pois precisamos calcular o tamanho total do texto para todas as linhas para
                    // encontrar o centro da área do texto no quad do componente.
                    tempLineHeight += glyphHeight + lineSpacing
                    hasLineBreak = true
                }
            }

            lineWidth = tempLineWidth
            lineHeight = tempLineHeight

            when (textPosition) {
                TextPosition.CENTER -> {
                    initialPositionX = width / 2.0f - lineWidth / 2.0f
                    initialPositionY = height / 2.0f - lineHeight / 2.0f - initialTopPadding / 2.0f
                }

                TextPosition.RIGHT -> {
                    initialPositionX = width - lineWidth - initialLeftPadding
                    initialPositionY = height / 2.0f - lineHeight / 2.0f - initialTopPadding / 2.0f
                }

                TextPosition.TOP -> {
                    initialPositionX = width / 2.0f - lineWidth / 2.0f
                    initialPositionY = 0f
                }

                TextPosition.LEFT -> {
                    initialPositionX = 0 + initialLeftPadding
                    initialPositionY = height / 2.0f - lineHeight / 2.0f - initialTopPadding / 2.0f
                }

                TextPosition.BOTTOM -> {
                    initialPositionX = width / 2.0f - lineWidth / 2.0f
                    initialPositionY = height - lineHeight - initialTopPadding
                }

                TextPosition.TOP_LEFT -> {
                    initialPositionX = 0.0f + initialLeftPadding
                    initialPositionY = 0.0f
                }

                TextPosition.TOP_RIGHT -> {
                    initialPositionX = width - lineWidth - initialLeftPadding
                    initialPositionY = 0.0f
                }

                TextPosition.BOTTOM_LEFT -> {
                    initialPositionX = 0 + initialLeftPadding
                    initialPositionY = height - lineHeight - initialTopPadding
                }

                TextPosition.BOTTOM_RIGHT -> {
                    initialPositionX = width - lineWidth - initialLeftPadding
                    initialPositionY = height - lineHeight - initialTopPadding
                }
            }
        }

        val offset = 1.5f
        var cursorX = 0f
        var cursorY = 0f

        // INCLUINDO O MESH DE LETRAS.
        // A variável vertexIndex avança a posição no array de vértices sempre que existir uma letra
        // para inclusão. A capacidade do array já é definida sem as quebras de linha, apenas caracteres
        // para exibição e espaço.
        // A variável charIndex considera todas as posições de caracteres no texto, mesmo os comandos
        // não exibidos como quebra de linha ('\n') e outros.
        var vertexIndexOffset = backgroundAndImageCount
        for (charIndex in charList.indices) {

            val c = charList[charIndex]

            localInitialLeftPadding = initialLeftPadding

            if (c.code == 10) {

                var pos = charIndex + 1
                var lineWidth = 0f
                initialPositionX = 0.0f

                cursorX = 0.0f
                cursorY++

                while (pos < charList.size && charList[pos].code != 10) {
                    lineWidth += glyphWidth + letterSpacing
                    pos++
                }

                when (textPosition) {
                    TextPosition.CENTER, TextPosition.BOTTOM, TextPosition.TOP -> {
                        initialPositionX = width / 2.0f - lineWidth / 2.0f
                    }

                    TextPosition.RIGHT, TextPosition.TOP_RIGHT, TextPosition.BOTTOM_RIGHT -> {
                        initialPositionX = width - lineWidth - initialLeftPadding
                    }

                    TextPosition.LEFT, TextPosition.BOTTOM_LEFT, TextPosition.TOP_LEFT -> {
                        initialPositionX = 0f + initialLeftPadding
                    }
                }

                continue
            }

            buildQuad(
                vertexIndexOffset = vertexIndexOffset,
                x = cursorX * (glyphWidth + letterSpacing) + localInitialLeftPadding + initialPositionX,
                y = cursorY * (glyphHeight + lineSpacing) + initialTopPadding + initialPositionY,
                quadWidth = glyphWidth,
                quadHeight = glyphHeight,
                lettersSpacing = offset,
                glyphTextureIndex = c.code.toFloat(),
                quadColor = textColor
            )

            cursorX++
            vertexIndexOffset++
        }
    }

    open fun updateVerticesIndicesSize() {

        verticesArraySize = vertices.size
        indicesArraySize = indices.size
    }

    open fun buildQuad(
        vertexIndexOffset: Int,
        x: Float,
        y: Float,
        quadWidth: Float,
        quadHeight: Float,
        lettersSpacing: Float,
        glyphTextureIndex: Float,
        quadColor: ColorM
    ) {

        /**
         * Quando se trata de UI component utilizando projeção ortho, deve-se
         * considerar para que direção ficaram os eixos xyz positivo ao construir
         * a matrix ortho, pois a criação dos vértices deve considerar a origenm
         * e o prolongamento dos quads ao calcular xyz, width e height.
         */

        var index = 0

        // POSITION, COLOR, UV, TEXTURE INDEX, BORDER SIZE
        vertices[vertexIndexOffset * baseVertices.size + index++] = x + quadWidth
        vertices[vertexIndexOffset * baseVertices.size + index++] = y
        vertices[vertexIndexOffset * baseVertices.size + index++] = quadColor.red
        vertices[vertexIndexOffset * baseVertices.size + index++] = quadColor.green
        vertices[vertexIndexOffset * baseVertices.size + index++] = quadColor.blue
        vertices[vertexIndexOffset * baseVertices.size + index++] = quadColor.alpha
        vertices[vertexIndexOffset * baseVertices.size + index++] = 1f
        vertices[vertexIndexOffset * baseVertices.size + index++] = 0f
        vertices[vertexIndexOffset * baseVertices.size + index++] = glyphTextureIndex
        vertices[vertexIndexOffset * baseVertices.size + index++] = cornerCurveSize

        vertices[vertexIndexOffset * baseVertices.size + index++] = x - lettersSpacing
        vertices[vertexIndexOffset * baseVertices.size + index++] = y
        vertices[vertexIndexOffset * baseVertices.size + index++] = quadColor.red
        vertices[vertexIndexOffset * baseVertices.size + index++] = quadColor.green
        vertices[vertexIndexOffset * baseVertices.size + index++] = quadColor.blue
        vertices[vertexIndexOffset * baseVertices.size + index++] = quadColor.alpha
        vertices[vertexIndexOffset * baseVertices.size + index++] = 0f
        vertices[vertexIndexOffset * baseVertices.size + index++] = 0f
        vertices[vertexIndexOffset * baseVertices.size + index++] = glyphTextureIndex
        vertices[vertexIndexOffset * baseVertices.size + index++] = cornerCurveSize

        vertices[vertexIndexOffset * baseVertices.size + index++] = x - lettersSpacing
        vertices[vertexIndexOffset * baseVertices.size + index++] = y + quadHeight
        vertices[vertexIndexOffset * baseVertices.size + index++] = quadColor.red
        vertices[vertexIndexOffset * baseVertices.size + index++] = quadColor.green
        vertices[vertexIndexOffset * baseVertices.size + index++] = quadColor.blue
        vertices[vertexIndexOffset * baseVertices.size + index++] = quadColor.alpha
        vertices[vertexIndexOffset * baseVertices.size + index++] = 0f
        vertices[vertexIndexOffset * baseVertices.size + index++] = 1f
        vertices[vertexIndexOffset * baseVertices.size + index++] = glyphTextureIndex
        vertices[vertexIndexOffset * baseVertices.size + index++] = cornerCurveSize

        vertices[vertexIndexOffset * baseVertices.size + index++] = x + quadWidth
        vertices[vertexIndexOffset * baseVertices.size + index++] = y + quadHeight
        vertices[vertexIndexOffset * baseVertices.size + index++] = quadColor.red
        vertices[vertexIndexOffset * baseVertices.size + index++] = quadColor.green
        vertices[vertexIndexOffset * baseVertices.size + index++] = quadColor.blue
        vertices[vertexIndexOffset * baseVertices.size + index++] = quadColor.alpha
        vertices[vertexIndexOffset * baseVertices.size + index++] = 1f
        vertices[vertexIndexOffset * baseVertices.size + index++] = 1f
        vertices[vertexIndexOffset * baseVertices.size + index++] = glyphTextureIndex
        vertices[vertexIndexOffset * baseVertices.size + index++] = cornerCurveSize

        for (i in baseIndices.indices) {
            indices[vertexIndexOffset * baseIndices.size + i] = baseIndices[i] + 4 * vertexIndexOffset
        }
    }

    override fun drawOpaque(dt: Float) {

    }

    override fun drawTransparent(dt: Float) {

        if (isTerminationInitiated || !isVisible) return

        VAO.bind()
        PID.bindUseProgram()

        if (!imagePath.isEmpty()) {
            TBO.activateTextureUnit()
            TBO.bind()
        }

        PID.setUniformMatrix4f(uOrthoMatrixLoc, gm.cameraManager.orthographicMatrix)
        PID.setUniformMatrix4f(uModelMatrixLoc, modelMatrix)

        val baseUIStatesIntMask = getBaseUIStatesIntMask()

        PID.setUniformInt(uBaseUIStatesLoc, baseUIStatesIntMask)

        PID.setUniformInt(uiPrimitiveTypeLoc, uiPrimitiveType.id)
        PID.setUniformInt(uIsCheckedLoc, if (isChecked)  1 else 0)

        val xValue = uiPrimitiveType.id.toFloat()
        val yValue = if (isChecked) 1f else 0f
        val zValue = gm.width.toFloat()
        val wValue = gm.height.toFloat()

        PID.setUniformVector4f(
            location = uValuesPrimitiveTypeIsCheckViewPortWidthHeightLoc,
            x = xValue,
            y = yValue,
            z = zValue,
            w = wValue,
        )
        PID.setUniformVector4f(
            location = uiPositionSizeValuesLoc,
            x = x,
            y = y,
            z = width,
            w = height
        )
        PID.setUniformVector4f(
            location = uiSliderValuesLoc,
            x = sliderNormalizedValueX,
            y = sliderNormalizedValueY,
            z = sliderButtonWidth,
            w = sliderButtonHeight
        )

        EBO.drawElementsTriangles()
    }

    override fun scheduleEntityTermination() {

        if (isTerminationInitiated) return
        isTerminationInitiated = true

        gm.rendererManager.subscribeEntityDestroy(entity = this)
        gm.logMessage(message = "UIBaseComponent: subscribe destroy.")
    }
    //    uSliderNormalizedValueX
//     [normalizedX, normalizedY, buttonWidth, buttonHeight].
//    uniform vec4 uiSliderValues;
    override fun destroyEntity() {

        VAO.destroy()
        VBO.destroy()
        EBO.destroy()
        TBO.destroy()
        //PID.destroy() // PID é static comum a todos os componentes.

        UIComponentManager.unsubscribe(component = this)
        gm.rendererManager.unsubscribeEntityUI(entity = this)
    }

    private fun getBaseUIStatesIntMask(): Int {

        var isShowingBorderResult = 0
        var isClickedResult = 0
        var isOnFocusResult = 0
        var isHoveredResult = 0

        if (isShowingBorder) isShowingBorderResult = 1
        if (isClicked) isClickedResult = 1
        if (isOnFocus) isOnFocusResult = 1
        if (isHovered) isHoveredResult = 1

        return (isShowingBorderResult shl 0) or
                (isClickedResult shl 1) or
                (isOnFocusResult shl 2) or
                (isHoveredResult shl 3)
    }

    open fun onComponentPress(posX: Float, posY: Float) {

        onPress(this, posX, posY)
    }

    open fun onComponentRelease() {

        onRelease(this)
    }

    open fun onComponentClick(posX: Float, posY: Float) {

        isChecked = !isChecked

        onClick(this, posX, posY)
    }

    open fun onComponentLongClick() {

        onLongClick(this)
    }

    open fun onComponentTyping(scancode: Int) {

        onTyping(this, scancode)
    }

    /** Called when component is dragged.  */
    open fun onComponentDrag(deltaX: Float, deltaY: Float, xPos: Float, yPos: Float) {

        onDrag(
            this,
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

    open fun onComponentUpdateViewPort() {

        onUpdateViewPort(this)
    }

    /** Called when parent layout is dragged, so all child components follow the layout.  */
    open fun onParentDrag(deltaX: Float, deltaY: Float, xPos: Float, yPos: Float) {

        // onParentDrag é sempre executado, pois é disparado pelo layout que administra os
        // componentes internos que devem se mover quando o layout é movido.
        x += deltaX
        y += deltaY

        bounds.x = x
        bounds.y = y

        modelMatrix.identity()
        modelMatrix.translate(x, y, 0f)
    }

    open fun onComponentHovered(xPos: Float, yPos: Float) {
        onHovered(this, xPos, yPos)
    }

    open fun onComponentFocus(xPos: Float, yPos: Float) {
        onFocus(this, xPos, yPos)
    }

    enum class UIPrimitiveType(val id: Int) {
        UI_BASE_COMPONENT(id = 0),
        UI_BUTTON_COMPONENT(id = 1),
        UI_SLIDER_COMPONENT(id = 2),
        UI_CHECK_BOX_COMPONENT(id = 3),
        UI_TEXT_COMPONENT(id = 4),
        UI_TEXT_FIELD_COMPONENT(id = 5),
        UI_COLOR_PICKER_COMPONENT(id = 6),
        UI_IMAGE_COMPONENT(id = 7),
        UI_LAYOUT_VERTICAL_COMPONENT(8),
        UI_LAYOUT_HORIZONTAL_COMPONENT(9),
        UI_SPACER_COMPONENT(10);
    }
}