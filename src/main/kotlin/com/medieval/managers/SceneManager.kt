package com.medieval.managers

import com.medieval.ui.components.UILayoutVerticalComponent
import com.medieval.ui.components.uiTextFieldComponent

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
            width = 100f,
        ) {


            uiTextFieldComponent(
                text = "Scene A",
                height = 60f
            )
        }
    }
}