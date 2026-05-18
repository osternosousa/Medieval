package com.medieval.managers

import com.medieval.components.Chunk
import com.medieval.foundation.ColorM
import com.medieval.ui.components.UILayoutVerticalComponent
import com.medieval.ui.components.UITextFieldComponent
import com.medieval.ui.components.uiButtonComponent
import com.medieval.ui.components.uiSliderComponent
import com.medieval.ui.components.uiTextFieldComponent
import com.medieval.ui.foundation.TextPosition
import com.medieval.ui.foundation.UIBaseComponent
import com.medieval.utility.decimalFormat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SceneManager(
    val gm: GameManager,
) {

    companion object {
        private const val TEXT_TITLE: String = "RENDERING METRICS"
        private const val TEXT_DIVIDER: String = "============================================="
        private const val TEXT_FPS_NOW: String = "FPS -> NOW: "
        private const val TEXT_FPS_MAX: String = " | MAX: "
        private const val TEXT_AVERAGE_FPS: String = " | AVR: "
        private const val TEXT_POSITION: String = "POSITION: "
    }
    val sb: StringBuilder = StringBuilder(2000)

    fun initManager() {

    }

    fun runSceneA() {

        UILayoutVerticalComponent(gm = gm, x = 5f, y = 5f, width = 400f) {

            uiTextFieldComponent(
                textColor = ColorM.BLACK(),
                textPosition = TextPosition.CENTER,
                backgroundColor = ColorM.YELLOW()
            ).text.append("Scene A")

            uiTextFieldComponent(
                textPosition = TextPosition.TOP_LEFT,
                textSize = 7f,
                height = 100f,
                onUpdate = { ui: UIBaseComponent, dx: Float ->

                    buildRenderingMetrics()
                    ui.text.clear()
                    ui.text.append(sb)
                    ui.scheduleComponentUpdate()
                }
            ).text.append("Rendering Metrics")

        }.isDraggable = true

        UILayoutVerticalComponent(gm = gm, x = gm.width - 405f, y = 5f, width = 400f) {

            uiTextFieldComponent(
                textColor = ColorM.BLACK(),
                textPosition = TextPosition.CENTER,
                backgroundColor = ColorM.YELLOW()
            ).text.append("SCENE CONTROLS")

            uiTextFieldComponent(backgroundColor = ColorM.DARK_GRAY()).text.append("Sky Color")

            val sliderRedText: String = "R: "
            uiSliderComponent(
                initialValueX = gm.timeWeatherManager.skyColor.x,
                onDrag = { ui: UIBaseComponent, normalizedValueX: Float, normalizedValueY: Float, dx: Float, dy: Float ->

                    gm.timeWeatherManager.skyColor.x = normalizedValueX
                    setClearColor()
                    formatSliderSkyColor(ui = ui, text = sliderRedText, value = normalizedValueX)
                }
            ).text.append(sliderRedText).append(gm.timeWeatherManager.skyColor.x)

            val sliderGreenText: String = "G: "
            uiSliderComponent(
                initialValueX = gm.timeWeatherManager.skyColor.y,
                onDrag = { ui: UIBaseComponent, normalizedValueX: Float, normalizedValueY: Float, dx: Float, dy: Float ->

                    gm.timeWeatherManager.skyColor.y = normalizedValueX
                    setClearColor()
                    formatSliderSkyColor(ui = ui, text = sliderGreenText, value = normalizedValueX)
                }
            ).text.append(sliderGreenText).append(gm.timeWeatherManager.skyColor.y)

            val sliderBlueText: String = "B: "
            uiSliderComponent(
                initialValueX = gm.timeWeatherManager.skyColor.z,
                onDrag = { ui: UIBaseComponent, normalizedValueX: Float, normalizedValueY: Float, dx: Float, dy: Float ->

                    gm.timeWeatherManager.skyColor.z = normalizedValueX
                    setClearColor()
                    formatSliderSkyColor(ui = ui, text = sliderBlueText, value = normalizedValueX)
                }
            ).text.append(sliderBlueText).append(gm.timeWeatherManager.skyColor.z)

            uiTextFieldComponent(backgroundColor = ColorM.DARK_GRAY()).text.append("Ambient Light")

            val sliderAmbientLightText: String = "AMBIENT LIGHT: "
            uiSliderComponent(
                initialValueX = gm.timeWeatherManager.ambientLight.x,
                onDrag = { ui: UIBaseComponent, normalizedValueX: Float, normalizedValueY: Float, dx: Float, dy: Float ->

                    gm.timeWeatherManager.ambientLight.set(normalizedValueX)
                    formatSliderSkyColor(ui = ui, text = sliderAmbientLightText, value = normalizedValueX)
                }
            ).text.append(sliderRedText).append(gm.timeWeatherManager.ambientLight.x)

            uiTextFieldComponent(backgroundColor = ColorM.DARK_GRAY()).text.append("Horizon Fade")

            val fadeMaxValue: Float = 256f
            val sliderChunkFadeDistanceStartText: String = "FADE INIT: "
            uiSliderComponent(
                initialValueX = Chunk.CHUNK_DISTANCE_INIT_FADE / fadeMaxValue,
                onDrag = { ui: UIBaseComponent, normalizedValueX: Float, normalizedValueY: Float, dx: Float, dy: Float ->

                    Chunk.CHUNK_DISTANCE_INIT_FADE = normalizedValueX * fadeMaxValue
                    formatSliderSkyColor(ui = ui, text = sliderChunkFadeDistanceStartText, value = Chunk.CHUNK_DISTANCE_INIT_FADE)
                }
            ).text.append(sliderChunkFadeDistanceStartText).append(Chunk.CHUNK_DISTANCE_INIT_FADE)

            val sliderChunkFadeDistanceEndText: String = "FADE END: "
            uiSliderComponent(
                initialValueX = Chunk.CHUNK_DISTANCE_END_FADE / fadeMaxValue,
                onDrag = { ui: UIBaseComponent, normalizedValueX: Float, normalizedValueY: Float, dx: Float, dy: Float ->

                    Chunk.CHUNK_DISTANCE_END_FADE = normalizedValueX * fadeMaxValue
                    formatSliderSkyColor(ui = ui, text = sliderChunkFadeDistanceEndText, value = Chunk.CHUNK_DISTANCE_END_FADE)
                }
            ).text.append(sliderChunkFadeDistanceEndText).append(Chunk.CHUNK_DISTANCE_END_FADE)

            uiTextFieldComponent(
                textColor = ColorM.BLACK(),
                textPosition = TextPosition.CENTER,
                backgroundColor = ColorM.YELLOW()
            ).text.append("ACTIONS")

            uiButtonComponent(
                onClick = { ui: UIBaseComponent, posX: Float, posY: Float -> Unit

                    val newWindow = GameManager(title = "New Window", width = 1280, height = 720, fullScreen = false)
                    newWindow.rendererManager.initWindow()
                    newWindow.initResources { newWindow.sceneManager.runSceneA() }
                    newWindow.rendererManager.initRenderingLoop()
                }
            ).text.append("Open New Window")

            uiButtonComponent(
                onClick = { ui: UIBaseComponent, posX: Float, posY: Float -> Unit

                    initGame()
                }
            ).text.append("Init Game")

        }.isDraggable = true

        setClearColor()
    }

    private fun buildRenderingMetrics() {

        sb.clear()
        sb.appendLine(TEXT_TITLE)
        sb.appendLine(TEXT_DIVIDER)
        sb.append(TEXT_FPS_NOW)
        sb.append(gm.rendererManager.FPS_NOW)
        sb.append(TEXT_FPS_MAX)
        sb.append(gm.rendererManager.FPS_MAX)
        sb.append(TEXT_AVERAGE_FPS)
        sb.append(gm.rendererManager.averageFPS.decimalFormat())
        sb.append('\n')
        sb.append(TEXT_POSITION)
        sb.append(gm.playerManager.eyePosition.x.decimalFormat())
        sb.append(", ")
        sb.append(gm.playerManager.eyePosition.y.decimalFormat())
        sb.append(", ")
        sb.append(gm.playerManager.eyePosition.z.decimalFormat())
    }

    private fun formatSliderSkyColor(ui: UIBaseComponent, text: String, value: Float) {

        var v = value * 100f
        v = v.toInt() / 100f

        ui.text.clear()
        ui.text.append(text)
        ui.text.append(v)
        ui.scheduleComponentUpdate()
    }

    private fun setClearColor() {

        gm.rendererManager.setClearColor(
            r = gm.timeWeatherManager.skyColor.x,
            g = gm.timeWeatherManager.skyColor.y,
            b = gm.timeWeatherManager.skyColor.z,
        )
    }

    private fun initGame() {

        val messageField = UITextFieldComponent(
            gm = gm,
            x = gm.width / 2f - 100f,
            y = gm.height / 2f - 25f,
            backgroundColor = ColorM(50, 100, 10, 155),
            width = 200f,
            height = 50f
        )

        messageField.text.append("Criando Terreno")
        messageField.scheduleComponentUpdate()

        CoroutineScope(Dispatchers.Default).launch {

            val playerPositionX = 880
            val playerPositionY = 102
            val playerPositionZ = 190

            gm.playerManager.eyePosition.x = playerPositionX.toFloat()
            gm.playerManager.eyePosition.y = playerPositionY.toFloat()
            gm.playerManager.eyePosition.z = playerPositionZ.toFloat()

            gm.inputManager.subscribeClient(client = gm.playerManager)
            gm.rendererManager.subscribeEntityGeneric(entity = gm.playerManager)
            gm.rendererManager.subscribeEntityInit(entity = gm.mainAxis)

            gm.inputManager.setCursorState(isEnabled = false)

            // Utilizando o mapa disponível para criar novo terreno.
            gm.terrainManager.updateBlockMap(posX = playerPositionX - 512, posZ = playerPositionZ - 512)
            // Faz o swap dos mapass, colocando o que foi criado como o ativo.
            gm.terrainManager.swapBlockMaps()

            messageField.scheduleEntityTermination()

            gm.chunkManager.originPosition.x = gm.playerManager.eyePosition.x
            gm.chunkManager.originPosition.z = gm.playerManager.eyePosition.z

            gm.rendererManager.subscribeEntityInitClearStates(entity = gm.terrainManager)
            gm.rendererManager.subscribeEntityGeneric(entity = gm.chunkManager)
        }
    }
}