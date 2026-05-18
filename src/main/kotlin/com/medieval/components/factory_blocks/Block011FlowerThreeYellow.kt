package com.medieval.components.factory_blocks

import com.medieval.foundation.BlockFactory

class Block011FlowerThreeYellow : BlockFactory() {

    /*

    */
    override val vertices: FloatArray = floatArrayOf(
        +0.5f, +0.5f, +0.5f,    1f, 0f, 0f,     1f, 0f,     12f,    // 00
        -0.5f, +0.5f, -0.5f,    0f, 1f, 0f,     0f, 0f,     12f,    // 01
        -0.5f, -0.5f, -0.5f,    0f, 0f, 1f,     0f, 1f,     12f,    // 02
        +0.5f, -0.5f, +0.5f,    1f, 0f, 0f,     1f, 1f,     12f,    // 03

        +0.5f, +0.5f, -0.5f,    1f, 0f, 0f,     1f, 0f,     12f,    // 04
        -0.5f, +0.5f, +0.5f,    0f, 1f, 0f,     0f, 0f,     12f,    // 05
        -0.5f, -0.5f, +0.5f,    0f, 0f, 1f,     0f, 1f,     12f,    // 06
        +0.5f, -0.5f, -0.5f,    1f, 0f, 0f,     1f, 1f,     12f,    // 07
    )

    override val indices: IntArray = intArrayOf(
        0, 1, 2, 2, 3, 0,
        1, 0, 3, 3, 2, 1,

        4, 5, 6, 6, 7, 4,
        5, 4, 7, 7, 6, 5,
    )

    override val facesIndices: FloatArray = floatArrayOf(12f, 12f, 12f, 12f, 12f, 12f)

    override val faceIndexFront: Float = 12f
    override val faceIndexBack: Float = 12f
    override val faceIndexRight: Float = 12f
    override val faceIndexLeft: Float = 12f
    override val faceIndexTop: Float = 12f
    override val faceIndexBottom: Float = 12f

    override val name: String = "Three yello Flowers"
    override val description: String = "Three Yellos Flowers"

    override val isSimpleCube: Boolean = false
    override val isTransparent: Boolean = true
    override val isSolid: Boolean = false
    override val isAlphaBlended: Boolean = false
}