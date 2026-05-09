package com.medieval.utility

import com.medieval.foundation.Vec2M
import com.medieval.foundation.Vec3M
import kotlin.math.pow

object MathM {

    const val PI: Double = 3.141592653589793
    const val DEGREES_TO_RADIANS: Float = (PI / 180.0).toFloat()

    fun smoothstep(edge0: Float, edge1: Float, x: Float): Float {
        val t = ((x - edge0) / (edge1 - edge0)).coerceIn(0f, 1f)
        return t * t * (3f - 2f * t)
    }

    fun mix(a: Float, b: Float, t: Float): Float {
        return a * (1 - t) + b * t
    }

    fun mix(a: Vec3M, b: Vec3M, t: Float): Vec3M {
        return a * (1 - t) + b * t
    }

    fun mix(v1: Vec2M, v2: Vec2M, a: Float): Vec2M {
        return Vec2M(
            v1.x * (1f - a) + v2.x * a,
            v1.y * (1f - a) + v2.y * a
        )
    }

    fun Float.fromDegreeToRadians() = this * DEGREES_TO_RADIANS
    fun Double.fromDegreeToRadians() = this * DEGREES_TO_RADIANS
}

fun Double.decimalStringFormat(digits: Int = 2): String {
    val factor = 10.0.pow(digits).toFloat()
    val rounded = (this * factor).toInt() / factor
    return rounded.toString()
//return String.format("%.${digits}f", this)
}

fun Float.decimalStringFormat(digits: Int = 2): String {
    val factor = 10.0.pow(digits).toFloat()
    val rounded = (this * factor).toInt() / factor
    return rounded.toString()
//return String.format("%.${digits}f", this)
}

/**
🔹 fract(x) — Fractional Part
=============================================
📘 What it does:
Returns the fractional part of a number. In other words, it subtracts the integer part and leaves the decimal.
🧮 Formula:
fract(x) = x - floor(x)
🧠 Intuition:
Imagine slicing a cake into whole pieces and crumbs. fract gives you just the crumbs.
🧪 Examples:
fract(3.75) → 0.75
fract(-2.3) → 0.7  // Because floor(-2.3) = -3


🔹 floor(x) — Round Down
=============================================
📘 What it does:
Returns the largest integer less than or equal to x.
🧮 Behavior:
floor(2.9) → 2.0
floor(-1.2) → -2.0


🔹 step(edge, x) — Binary Threshold
=============================================
📘 What it does:
Returns 0.0 if x < edge, and 1.0 otherwise.
🧮 Formula:
step(edge, x) = x < edge ? 0.0 : 1.0
🧠 Intuition:
It’s like a light switch: off below the threshold, on above it.
🧪 Examples:
step(0.5, 0.3) → 0.0
step(0.5, 0.7) → 1.0


🔹 smoothstep(edge0, edge1, x) — Smooth Transition
=============================================
📘 What it does:
Returns a smooth interpolation between 0.0 and 1.0 as x moves from edge0 to edge1.
🧮 Formula:
t = clamp((x - edge0) / (edge1 - edge0), 0.0, 1.0)
smoothstep = t * t * (3.0 - 2.0 * t)
🧠 Intuition:
Imagine a fade-in effect: starts slow, speeds up, then slows down again.
🧪 Examples:
smoothstep(0.0, 1.0, 0.0) → 0.0
smoothstep(0.0, 1.0, 0.5) → 0.5
smoothstep(0.0, 1.0, 1.0) → 1.0

=============================================
=============================================
=============================================
 */