package com.medieval.components.factory_blocks

import com.medieval.foundation.BlockFactory

class Block015Bush : BlockFactory() {

    /*

    */
    override val vertices: FloatArray = floatArrayOf(
        +0.5f, +0.5f, +0.5f,    1f, 0f, 0f,     1f, 0f,     18f,    // 00
        -0.5f, +0.5f, -0.5f,    0f, 1f, 0f,     0f, 0f,     18f,    // 01
        -0.5f, -0.5f, -0.5f,    0f, 0f, 1f,     0f, 1f,     18f,    // 02
        +0.5f, -0.5f, +0.5f,    1f, 0f, 0f,     1f, 1f,     18f,    // 03

        +0.5f, +0.5f, -0.5f,    1f, 0f, 0f,     1f, 0f,     19f,    // 04
        -0.5f, +0.5f, +0.5f,    0f, 1f, 0f,     0f, 0f,     19f,    // 05
        -0.5f, -0.5f, +0.5f,    0f, 0f, 1f,     0f, 1f,     19f,    // 06
        +0.5f, -0.5f, -0.5f,    1f, 0f, 0f,     1f, 1f,     19f,    // 07
    )

    override val indices: IntArray = intArrayOf(
        0, 1, 2, 2, 3, 0,
        1, 0, 3, 3, 2, 1,

        4, 5, 6, 6, 7, 4,
        5, 4, 7, 7, 6, 5,
    )

    override val facesIndices: FloatArray = floatArrayOf(18f, 18f, 18f, 18f, 18f, 18f)

    override val faceIndexFront: Float = 18f
    override val faceIndexBack: Float = 18f
    override val faceIndexRight: Float = 18f
    override val faceIndexLeft: Float = 18f
    override val faceIndexTop: Float = 18f
    override val faceIndexBottom: Float = 18f

    override val name: String = "Bush"
    override val description: String = "Bush"

    override val isSimpleCube: Boolean = false
    override val isTransparent: Boolean = true
    override val isSolid: Boolean = false
    override val isAlphaBlended: Boolean = false
}