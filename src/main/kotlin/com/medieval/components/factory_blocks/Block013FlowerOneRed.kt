package com.medieval.components.factory_blocks

import com.medieval.foundation.BlockFactory

class Block013FlowerOneRed : BlockFactory() {

    /*

    */
    override val vertices: FloatArray = floatArrayOf(
        +0.5f, +0.5f, +0.5f,    1f, 0f, 0f,     1f, 0f,     16f,    // 00
        -0.5f, +0.5f, -0.5f,    0f, 1f, 0f,     0f, 0f,     16f,    // 01
        -0.5f, -0.5f, -0.5f,    0f, 0f, 1f,     0f, 1f,     16f,    // 02
        +0.5f, -0.5f, +0.5f,    1f, 0f, 0f,     1f, 1f,     16f,    // 03

        +0.5f, +0.5f, -0.5f,    1f, 0f, 0f,     1f, 0f,     16f,    // 04
        -0.5f, +0.5f, +0.5f,    0f, 1f, 0f,     0f, 0f,     16f,    // 05
        -0.5f, -0.5f, +0.5f,    0f, 0f, 1f,     0f, 1f,     16f,    // 06
        +0.5f, -0.5f, -0.5f,    1f, 0f, 0f,     1f, 1f,     16f,    // 07
    )

    override val indices: IntArray = intArrayOf(
        0, 1, 2, 2, 3, 0,
        1, 0, 3, 3, 2, 1,

        4, 5, 6, 6, 7, 4,
        5, 4, 7, 7, 6, 5,
    )

    override val facesIndices: FloatArray = floatArrayOf(16f, 16f, 16f, 16f, 16f, 16f)

    override val faceIndexFront: Float = 16f
    override val faceIndexBack: Float = 16f
    override val faceIndexRight: Float = 16f
    override val faceIndexLeft: Float = 16f
    override val faceIndexTop: Float = 16f
    override val faceIndexBottom: Float = 16f

    override val name: String = "One Red Flower"
    override val description: String = "One Red Flower"

    override val isSimpleCube: Boolean = false
    override val isTransparent: Boolean = true
    override val isSolid: Boolean = false
    override val isAlphaBlended: Boolean = false
}