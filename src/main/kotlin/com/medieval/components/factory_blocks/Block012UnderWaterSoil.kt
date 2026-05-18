package com.medieval.components.factory_blocks

import com.medieval.foundation.BlockFactory

class Block012UnderWaterSoil : BlockFactory() {

    override val vertices: FloatArray = floatArrayOf()
    override val indices: IntArray = intArrayOf()

    override val facesIndices: FloatArray = floatArrayOf(14f, 14f, 14f, 14f, 14f, 14f)

    override val faceIndexFront: Float = 14f
    override val faceIndexBack: Float = 14f
    override val faceIndexRight: Float = 14f
    override val faceIndexLeft: Float = 14f
    override val faceIndexTop: Float = 14f
    override val faceIndexBottom: Float = 14f

    override val name: String = "Under Water Soil Block"
    override val description: String = "Simple Block Of Under Water Soil"

    override val isSimpleCube: Boolean = true
    override val isTransparent: Boolean = false
    override val isSolid: Boolean = true
    override val isAlphaBlended: Boolean = false
}