package com.medieval.components.factory_blocks

import com.medieval.foundation.BlockFactory

class Block000DirtTerrain : BlockFactory() {

    override val vertices: FloatArray = floatArrayOf()
    override val indices: IntArray = intArrayOf()

    override val facesIndices: FloatArray = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f)

    override val faceIndexFront: Float = 0f
    override val faceIndexBack: Float = 0f
    override val faceIndexRight: Float = 0f
    override val faceIndexLeft: Float = 0f
    override val faceIndexTop: Float = 0f
    override val faceIndexBottom: Float = 0f

    override val name: String = "Dirt Terrain Block"
    override val description: String = "Simple Block Of Terrain Dirt"

    override val isSimpleCube: Boolean = true
    override val isTransparent: Boolean = false
    override val isSolid: Boolean = true
    override val isAlphaBlended: Boolean = false
}