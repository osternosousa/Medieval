package com.medieval.foundation

data class Vec2M(var x: Float, var y: Float) {

    operator fun plus(v: Vec2M) = Vec2M(x + v.x, y + v.y)
    operator fun plus(scalar: Float) = Vec2M(x + scalar, y + scalar)
    operator fun minus(v: Vec2M) = Vec2M(x - v.x, y - v.y)
    operator fun minus(scalar: Float) = Vec2M(x - scalar, y - scalar)
    operator fun times(v: Vec2M) = Vec2M(x * v.x, y * v.y)
    operator fun times(scalar: Float) = Vec2M(x * scalar, y * scalar)
    operator fun div(scalar: Float) = Vec2M(x / scalar, y / scalar)
    operator fun div(v: Vec2M) = Vec2M(x / v.x, y / v.y)

    fun dot(other: Vec2M) = x * other.x + y * other.y
    fun floor() = Vec2M(kotlin.math.floor(x), kotlin.math.floor(y))
    fun fract() = Vec2M(x - kotlin.math.floor(x), y - kotlin.math.floor(y))
}