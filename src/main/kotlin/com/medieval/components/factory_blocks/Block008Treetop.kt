package com.medieval.components.factory_blocks

import com.medieval.foundation.BlockFactory

class Block008Treetop : BlockFactory() {

    override val vertices: FloatArray = floatArrayOf()
    override val indices: IntArray = intArrayOf()

    override val facesIndices: FloatArray = floatArrayOf(9f, 9f, 9f, 9f, 9f, 9f)

    override val faceIndexFront: Float = 9f
    override val faceIndexBack: Float = 9f
    override val faceIndexRight: Float = 9f
    override val faceIndexLeft: Float = 9f
    override val faceIndexTop: Float = 9f
    override val faceIndexBottom: Float = 9f

    override val name: String = "Treeptop Of Tree A"
    override val description: String = "Simple Block Of Treetop Of Tree A"

    override val isSimpleCube: Boolean = true
    override val isTransparent: Boolean = true
    override val isSolid: Boolean = true
    override val isAlphaBlended: Boolean = false
}