package com.medieval.components.factory_blocks

import com.medieval.foundation.BlockFactory

class Block014FlowerOneYellow : BlockFactory() {

    /*

    */
    override val vertices: FloatArray = floatArrayOf(
        +0.5f, +0.5f, +0.5f,    1f, 0f, 0f,     1f, 0f,     17f,    // 00
        -0.5f, +0.5f, -0.5f,    0f, 1f, 0f,     0f, 0f,     17f,    // 01
        -0.5f, -0.5f, -0.5f,    0f, 0f, 1f,     0f, 1f,     17f,    // 02
        +0.5f, -0.5f, +0.5f,    1f, 0f, 0f,     1f, 1f,     17f,    // 03

        +0.5f, +0.5f, -0.5f,    1f, 0f, 0f,     1f, 0f,     17f,    // 04
        -0.5f, +0.5f, +0.5f,    0f, 1f, 0f,     0f, 0f,     17f,    // 05
        -0.5f, -0.5f, +0.5f,    0f, 0f, 1f,     0f, 1f,     17f,    // 06
        +0.5f, -0.5f, -0.5f,    1f, 0f, 0f,     1f, 1f,     17f,    // 07
    )

    override val indices: IntArray = intArrayOf(
        0, 1, 2, 2, 3, 0,
        1, 0, 3, 3, 2, 1,

        4, 5, 6, 6, 7, 4,
        5, 4, 7, 7, 6, 5,
    )

    override val facesIndices: FloatArray = floatArrayOf(17f, 17f, 17f, 17f, 17f, 17f)

    override val faceIndexFront: Float = 17f
    override val faceIndexBack: Float = 17f
    override val faceIndexRight: Float = 17f
    override val faceIndexLeft: Float = 17f
    override val faceIndexTop: Float = 17f
    override val faceIndexBottom: Float = 17f

    override val name: String = "One yellow Flower"
    override val description: String = "One Yellow Flower"

    override val isSimpleCube: Boolean = false
    override val isTransparent: Boolean = true
    override val isSolid: Boolean = false
    override val isAlphaBlended: Boolean = false
}