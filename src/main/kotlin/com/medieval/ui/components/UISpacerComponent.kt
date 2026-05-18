package com.medieval.ui.components

import com.medieval.foundation.ColorM
import com.medieval.managers.GameManager
import com.medieval.ui.foundation.UIBaseComponent

class UISpacerComponent(
    gm: GameManager,
    x: Float = 5f,
    y: Float = 5f,
    width: Float = 250f,
    height: Float = 20f,
) : UIBaseComponent(
    gm = gm,
    x = x,
    y = y,
    width = width,
    height = height,
    backgroundColor = ColorM.TRANSPARENT()
) {

    override var uiPrimitiveType: UIPrimitiveType = UIPrimitiveType.UI_SPACER_COMPONENT

    override fun initEntity() {

        super.initEntity()

        this@UISpacerComponent.gm.logMessage(message = "UI Spacer Component created!")
    }

    override fun createUpdateBackgroundAndGlyphMesh() {

        if (imagePath.isNotEmpty()) imagePath = ""
        if (text.isNotEmpty()) text.clear()

        super.createUpdateBackgroundAndGlyphMesh()
    }
}