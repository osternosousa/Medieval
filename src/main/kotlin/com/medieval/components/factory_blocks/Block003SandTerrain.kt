package com.medieval.components.factory_blocks

import com.medieval.foundation.BlockFactory

class Block003SandTerrain : BlockFactory() {

    override val vertices: FloatArray = floatArrayOf()
    override val indices: IntArray = intArrayOf()

    override val facesIndices: FloatArray = floatArrayOf(4f, 4f, 4f, 4f, 4f, 4f)

    override val faceIndexFront: Float = 4f
    override val faceIndexBack: Float = 4f
    override val faceIndexRight: Float = 4f
    override val faceIndexLeft: Float = 4f
    override val faceIndexTop: Float = 4f
    override val faceIndexBottom: Float = 4f

    override val name: String = "Sand Terrain Block"
    override val description: String = "Simple Block Of Terrain Sand"

    override val isSimpleCube: Boolean = true
    override val isTransparent: Boolean = false
    override val isSolid: Boolean = true
    override val isAlphaBlended: Boolean = false
}