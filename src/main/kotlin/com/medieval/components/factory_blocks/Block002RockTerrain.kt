package com.medieval.components.factory_blocks

import com.medieval.foundation.BlockFactory

class Block002RockTerrain : BlockFactory() {

    override val vertices: FloatArray = floatArrayOf()
    override val indices: IntArray = intArrayOf()

    override val facesIndices: FloatArray = floatArrayOf(3f, 3f, 3f, 3f, 3f, 3f)

    override val faceIndexFront: Float = 3f
    override val faceIndexBack: Float = 3f
    override val faceIndexRight: Float = 3f
    override val faceIndexLeft: Float = 3f
    override val faceIndexTop: Float = 3f
    override val faceIndexBottom: Float = 3f

    override val name: String = "Rock Terrain Block"
    override val description: String = "Simple Block Of Terrain Rock"

    override val isSimpleCube: Boolean = true
    override val isTransparent: Boolean = false
    override val isSolid: Boolean = true
    override val isAlphaBlended: Boolean = false
}