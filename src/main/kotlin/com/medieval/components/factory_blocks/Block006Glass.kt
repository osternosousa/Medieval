package com.medieval.components.factory_blocks

import com.medieval.foundation.BlockFactory

class Block006Glass : BlockFactory() {

    override val vertices: FloatArray = floatArrayOf(
        +0.5f, +0.5f, +0.5f,    1f, 0f, 0f,     1f, 0f,     6f,     // 00 -
        -0.5f, +0.5f, +0.5f,    0f, 1f, 0f,     0f, 0f,     6f,     // 01 -
        -0.5f, -0.5f, +0.5f,    0f, 0f, 1f,     0f, 1f,     6f,     // 02 -
        +0.5f, -0.5f, +0.5f,    1f, 0f, 0f,     1f, 1f,     6f,     // 03 -

        -0.5f, +0.5f, -0.5f,    0f, 1f, 0f,     0f, 0f,     6f,     // 04 -
        +0.5f, +0.5f, -0.5f,    1f, 0f, 0f,     1f, 0f,     6f,     // 05 -
        +0.5f, -0.5f, -0.5f,    1f, 0f, 0f,     1f, 1f,     6f,     // 06 -
        -0.5f, -0.5f, -0.5f,    0f, 0f, 1f,     0f, 1f,     6f,     // 07 -

        +0.5f, +0.5f, -0.5f,    0f, 1f, 0f,     0f, 0f,     6f,     // 08 -
        +0.5f, +0.5f, +0.5f,    1f, 0f, 0f,     1f, 0f,     6f,     // 09 -
        +0.5f, -0.5f, +0.5f,    1f, 0f, 0f,     1f, 1f,     6f,     // 10 -
        +0.5f, -0.5f, -0.5f,    0f, 0f, 1f,     0f, 1f,     6f,     // 11 -

        -0.5f, +0.5f, +0.5f,    0f, 1f, 0f,     0f, 0f,     6f,     // 12 -
        -0.5f, +0.5f, -0.5f,    1f, 0f, 0f,     1f, 0f,     6f,     // 13 -
        -0.5f, -0.5f, -0.5f,    1f, 0f, 0f,     1f, 1f,     6f,     // 14 -
        -0.5f, -0.5f, +0.5f,    0f, 0f, 1f,     0f, 1f,     6f,     // 15 -

        +0.5f, +0.5f, -0.5f,    0f, 1f, 0f,     0f, 0f,     6f,     // 16 -
        -0.5f, +0.5f, -0.5f,    1f, 0f, 0f,     1f, 0f,     6f,     // 17 -
        -0.5f, +0.5f, +0.5f,    1f, 0f, 0f,     1f, 1f,     6f,     // 18 -
        +0.5f, +0.5f, +0.5f,    0f, 0f, 1f,     0f, 1f,     6f,     // 19 -

        +0.5f, -0.5f, +0.5f,    0f, 1f, 0f,     0f, 0f,     6f,     // 20 -
        -0.5f, -0.5f, +0.5f,    1f, 0f, 0f,     1f, 0f,     6f,     // 21 -
        -0.5f, -0.5f, -0.5f,    1f, 0f, 0f,     1f, 1f,     6f,     // 22 -
        +0.5f, -0.5f, -0.5f,    0f, 0f, 1f,     0f, 1f,     6f,     // 23 -
    )
    override val indices: IntArray = intArrayOf(
        0, 1, 2, 2, 3, 0,
        1, 0, 3, 3, 2, 1,

        4, 5, 6, 6, 7, 4,
        5, 4, 7, 7, 6, 5,

        8, 9, 10, 10, 11, 8,
        9, 8, 11, 11, 10, 9,

        12, 13, 14, 14, 15, 12,
        13, 12, 15, 15, 14, 13,

        16, 17, 18, 18, 19, 16,
        17, 16, 19, 19, 18, 17,

        20, 21, 22, 22, 23, 20,
        21, 20, 23, 23, 22, 21,
    )

    override val facesIndices: FloatArray = floatArrayOf(6f, 6f, 6f, 6f, 6f, 6f)

    override val faceIndexFront: Float = 6f
    override val faceIndexBack: Float = 6f
    override val faceIndexRight: Float = 6f
    override val faceIndexLeft: Float = 6f
    override val faceIndexTop: Float = 6f
    override val faceIndexBottom: Float = 6f

    override val name: String = "Glass Block"
    override val description: String = "Simple Block Of Glass"

    override val isSimpleCube: Boolean = false
    override val isTransparent: Boolean = true
    override val isSolid: Boolean = true
    override val isAlphaBlended: Boolean = false
}