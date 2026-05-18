package com.medieval.foundation

abstract class BlockFactory {

    abstract val vertices: FloatArray
    abstract val indices: IntArray

    abstract val facesIndices: FloatArray

    abstract val faceIndexFront: Float
    abstract val faceIndexBack: Float
    abstract val faceIndexRight: Float
    abstract val faceIndexLeft: Float
    abstract val faceIndexTop: Float
    abstract val faceIndexBottom: Float

    abstract val name: String
    abstract val description: String

    /** The block is a class 6 sides regular cube. Ex.: rock block. */
    abstract val isSimpleCube: Boolean

    open val collisionBounds: CubeM = CubeM(width = 1f, length = 1f, height = 1f)

    /** The block allow sight of objects on the other side, either because
    it is transparent or because it has irregular shape, so it is possible
    to see the other side via the irregularities. Used, for example, when
    calculating visible faces around the bloc, because when transparent,
    the block lets the terrain on the other side visible. Ex.: water block,
    glass block, flowers. */
    abstract val isTransparent: Boolean

    /** It is possible to stand over the block. Ex.: sand block. */
    abstract val isSolid: Boolean

    /** If the block is alpha blended, it needs to be rendered with the
    drawTransparentEntity() function. */
    abstract val isAlphaBlended: Boolean
}