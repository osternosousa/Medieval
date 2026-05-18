package com.medieval.components.factory_blocks

import com.medieval.foundation.BlockFactory

class Block007WoodA : BlockFactory() {

    override val vertices: FloatArray = floatArrayOf()
    override val indices: IntArray = intArrayOf()

    override val facesIndices: FloatArray = floatArrayOf(7f, 7f, 7f, 7f, 8f, 8f)

    override val faceIndexFront: Float = 7f
    override val faceIndexBack: Float = 7f
    override val faceIndexRight: Float = 7f
    override val faceIndexLeft: Float = 7f
    override val faceIndexTop: Float = 8f
    override val faceIndexBottom: Float = 8f

    override val name: String = "Wood A Block"
    override val description: String = "Simple Block Of Wood A"

    override val isSimpleCube: Boolean = true
    override val isTransparent: Boolean = false
    override val isSolid: Boolean = true
    override val isAlphaBlended: Boolean = false
}