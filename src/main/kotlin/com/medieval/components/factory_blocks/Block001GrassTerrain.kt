package com.medieval.components.factory_blocks

import com.medieval.foundation.BlockFactory

class Block001GrassTerrain : BlockFactory() {

    override val vertices: FloatArray = floatArrayOf()
    override val indices: IntArray = intArrayOf()

    override val facesIndices: FloatArray = floatArrayOf(2f, 2f, 2f, 2f, 1f, 0f)

    override val faceIndexFront: Float = 2f
    override val faceIndexBack: Float = 2f
    override val faceIndexRight: Float = 2f
    override val faceIndexLeft: Float = 2f
    override val faceIndexTop: Float = 1f
    override val faceIndexBottom: Float = 0f

    override val name: String = "Grass Terrain Block"
    override val description: String = "Simple Block Of Dirt Terrain"

    override val isSimpleCube: Boolean = true
    override val isTransparent: Boolean = false
    override val isSolid: Boolean = true
    override val isAlphaBlended: Boolean = false
}