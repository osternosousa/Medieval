package com.medieval.foundation

class CubeM(
    var x: Float = 0f,
    var y: Float = 0f,
    var z: Float = 0f,
    var width: Float = 0f,
    var length: Float = 0f,
    var height: Float = 0f,
) {


    fun contains(x: Float, y: Float, z: Float): Boolean {
        return     x >= this.x - this.width/2f && x <= this.x + this.width/2f
                && y >= this.y - this.height/2f && y <= this.y + this.height/2f
                && z >= this.z - this.length/2f && z <= this.z + this.length/2f
    }

    fun contains(x: Double, y: Double, z: Double): Boolean {
        return contains(x.toFloat(), y.toFloat(), z.toFloat())
    }

    fun intersects(cube: CubeM): Boolean {
        // Half sizes
        val halfW = width / 2.0f
        val halfH = height / 2.0f
        val halfL = length / 2.0f

        val otherHalfW = cube.width / 2.0f
        val otherHalfH = cube.height / 2.0f
        val otherHalfL = cube.length / 2.0f

        // Check overlap along each axis
        val overlapX = kotlin.math.abs(x - cube.x) <= (halfW + otherHalfW)
        val overlapY = kotlin.math.abs(y - cube.y) <= (halfH + otherHalfH)
        val overlapZ = kotlin.math.abs(z - cube.z) <= (halfL + otherHalfL)

        return overlapX && overlapY && overlapZ
    }
}