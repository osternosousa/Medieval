package com.medieval.components.factory_blocks

import com.medieval.foundation.BlockFactory

class Block004WaterSurface : BlockFactory() {

    override val vertices: FloatArray = floatArrayOf(
        // Bloco irregular em que exibe apenas a face superior, mas
        // repetida com rotação inversa para exibir a mesma face quando
        // vista de baixo.
        // top face seen from up.
        +0.5f, +0.5f, -0.5f,    0f, 1f, 0f,     0f, 0f,     5f,
        -0.5f, +0.5f, -0.5f,    1f, 0f, 0f,     1f, 0f,     5f,
        -0.5f, +0.5f, +0.5f,    1f, 0f, 0f,     1f, 1f,     5f,
        +0.5f, +0.5f, +0.5f,    0f, 0f, 1f,     0f, 1f,     5f,
        // top face seen from bottom.
        +0.5f, +0.5f, +0.5f,    0f, 1f, 0f,     0f, 0f,     5f,
        -0.5f, +0.5f, +0.5f,    1f, 0f, 0f,     1f, 0f,     5f,
        -0.5f, +0.5f, -0.5f,    1f, 0f, 0f,     1f, 1f,     5f,
        +0.5f, +0.5f, -0.5f,    0f, 0f, 1f,     0f, 1f,     5f,
    )
    override val indices: IntArray = intArrayOf(0, 1, 2, 2, 3, 0, 4, 5, 6, 6, 7, 4)

    override val facesIndices: FloatArray = floatArrayOf(5f, 5f, 5f, 5f, 5f, 5f)

    override val faceIndexFront: Float = 5f
    override val faceIndexBack: Float = 5f
    override val faceIndexRight: Float = 5f
    override val faceIndexLeft: Float = 5f
    override val faceIndexTop: Float = 5f
    override val faceIndexBottom: Float = 5f

    override val name: String = "Water Surface"
    override val description: String = "Simple Block Of Water Surface"

    override val isSimpleCube: Boolean = false
    override val isTransparent: Boolean = true
    override val isSolid: Boolean = false
    override val isAlphaBlended: Boolean = true
}