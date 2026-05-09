package com.medieval.foundation

class RectangleM(
    var x: Float = 0f,
    var y: Float = 0f,
    var width: Float = 0f,
    var height: Float = 0f,
) {

    fun RectangleM(x: Float, y: Float, width: Float, height: Float) {
        this.x = x
        this.y = y
        this.width = width
        this.height = height
    }

    fun contains(x: Float, y: Float): Boolean {
        return x >= this.x && x < this.x + this.width && y >= this.y && y < this.y + this.height
    }

    fun contains(x: Double, y: Double): Boolean {
        return contains(x.toFloat(), y.toFloat())
    }

    fun intersects(rectangle: RectangleM): Boolean {
        return !((this.x + this.width < rectangle.x || this.x > rectangle.x + rectangle.width)
                || (this.y + this.height < rectangle.y || this.y > rectangle.y + rectangle.height)
                || (rectangle.x + rectangle.width < this.x || rectangle.x > this.x + this.width)
                || (rectangle.y + rectangle.height < this.y || rectangle.y > this.y + this.height))
    }
}