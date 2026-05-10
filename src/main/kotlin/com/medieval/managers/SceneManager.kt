package com.medieval.managers

import com.medieval.foundation.ColorM
import com.medieval.ui.components.UILayoutVerticalComponent
import com.medieval.ui.components.uiSliderComponent
import com.medieval.ui.components.uiTextFieldComponent
import com.medieval.ui.foundation.TextPosition
import com.medieval.ui.foundation.UIBaseComponent

class SceneManager(
    val gm: GameManager,
) {

    fun initManager() {

    }

    fun runSceneA() {

        UILayoutVerticalComponent(
            gm = gm,
            x = 5f,
            y = 5f,
            width = 400f,
        ) {

            uiTextFieldComponent(
                text = "Scene A",
                textColor = ColorM.BLACK(),
                textPosition = TextPosition.CENTER,
                backgroundColor = ColorM.YELLOW()
            )

            uiTextFieldComponent(
                text = "Rendering Metrics",
                textPosition = TextPosition.TOP_LEFT,
                height = 100f
            )

        }.isDraggable = true

        UILayoutVerticalComponent(
            gm = gm,
            x = gm.width - 405f,
            y = 5f,
            width = 400f,
        ) {

            uiTextFieldComponent(
                text = "SCENE CONTROLS",
                textColor = ColorM.BLACK(),
                textPosition = TextPosition.CENTER,
                backgroundColor = ColorM.YELLOW()
            )

            uiTextFieldComponent(text = "Clear Color")

            uiSliderComponent(
                text = "R: 0.00",
                onDrag = { ui: UIBaseComponent, normalizedValueX: Float, normalizedValueY: Float, dx: Float, dy: Float ->

                    gm.timeWeatherManager.skyColor.x = normalizedValueX

                    gm.rendererManager.setClearColor(
                        r = gm.timeWeatherManager.skyColor.x,
                        g = gm.timeWeatherManager.skyColor.y,
                        b = gm.timeWeatherManager.skyColor.z,
                    )
                }
            )
            uiSliderComponent(
                text = "G: 0.00",
                onDrag = { ui: UIBaseComponent, normalizedValueX: Float, normalizedValueY: Float, dx: Float, dy: Float ->

                    gm.timeWeatherManager.skyColor.y = normalizedValueX

                    gm.rendererManager.setClearColor(
                        r = gm.timeWeatherManager.skyColor.x,
                        g = gm.timeWeatherManager.skyColor.y,
                        b = gm.timeWeatherManager.skyColor.z,
                    )
                }
            )

            uiSliderComponent(
                text = "B: 0.00",
                onDrag = { ui: UIBaseComponent, normalizedValueX: Float, normalizedValueY: Float, dx: Float, dy: Float ->

                    gm.timeWeatherManager.skyColor.z = normalizedValueX

                    gm.rendererManager.setClearColor(
                        r = gm.timeWeatherManager.skyColor.x,
                        g = gm.timeWeatherManager.skyColor.y,
                        b = gm.timeWeatherManager.skyColor.z,
                    )
                }
            )

        }.isDraggable = true
    }
}