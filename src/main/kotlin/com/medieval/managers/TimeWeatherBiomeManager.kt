package com.medieval.managers

import org.joml.Vector3f

class TimeWeatherBiomeManager(
    val gm: GameManager,
) {

    val skyColor: Vector3f = Vector3f(0.3f, 0.5f, 0.8f)
    val dayLight: Vector3f = Vector3f(1.0f, 1.0f, 1.0f)
    var ambientLight: Float = 0.8f

    fun initManager() {

    }
}