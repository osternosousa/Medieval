package com.medieval.components.factory_blocks

import com.medieval.foundation.BlockFactory

class Block005Air : BlockFactory() {

    override val vertices: FloatArray = floatArrayOf()
    override val indices: IntArray = intArrayOf()

    override val facesIndices: FloatArray = floatArrayOf(13f, 13f, 13f, 13f, 13f, 13f)

    override val faceIndexFront: Float = 13f
    override val faceIndexBack: Float = 13f
    override val faceIndexRight: Float = 13f
    override val faceIndexLeft: Float = 13f
    override val faceIndexTop: Float = 13f
    override val faceIndexBottom: Float = 13f

    override val name: String = "Air Block"
    override val description: String = "Simple Block Of Air"

    override val isSimpleCube: Boolean = true
    override val isTransparent: Boolean = true
    override val isSolid: Boolean = false
    override val isAlphaBlended: Boolean = false

}