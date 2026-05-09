package com.medieval.foundation

data class Vec3M(var x: Float, var y: Float, var z: Float) {

    operator fun plus(other: Vec3M): Vec3M {
        return Vec3M(x + other.x, y + other.y, z + other.z)
    }
    operator fun plus(scalar: Float): Vec3M {
        return Vec3M(x + scalar, y + scalar, z + scalar)
    }
    operator fun minus(other: Vec3M): Vec3M {
        return Vec3M(x - other.x, y - other.y, z - other.z)
    }
    operator fun minus(scalar: Float): Vec3M {
        return Vec3M(x - scalar, y - scalar, z - scalar)
    }
    operator fun times(scalar: Float): Vec3M {
        return Vec3M(x * scalar, y * scalar, z * scalar)
    }
    operator fun div(scalar: Float): Vec3M {
        return Vec3M(x / scalar, y / scalar, z / scalar)
    }
}