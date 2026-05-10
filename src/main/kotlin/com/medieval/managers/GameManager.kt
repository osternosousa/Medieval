package com.medieval.managers

import com.medieval.foundation.LogManager
import org.lwjgl.glfw.GLFW.glfwGetTime

class GameManager(
    var title: String,
    var width: Int,
    var height: Int,
    var fullScreen: Boolean
) {


    companion object {

    }

    val rendererManager: RendererManager = RendererManager(gm = this)
    val inputManager: InputManager = InputManager(gm = this)
    val resourcesManager: ResourcesManager = ResourcesManager(gm = this)
    val sceneManager: SceneManager = SceneManager(gm = this)
    val cameraManager: CameraManager = CameraManager(gm = this)
    val soundManager: SoundManager = SoundManager(gm = this)
    val terrainManager: TerrainManager = TerrainManager(gm = this)
    val timeWeatherManager: TimeWeatherBiomeManager = TimeWeatherBiomeManager(gm = this)
    val playerManager: PlayerManager = PlayerManager(gm = this)
    val mainAxis: MainAxis = MainAxis(gm = this)
    val chunkManager: ChunkManager = ChunkManager(gm = this)

    private val logClients: MutableList<LogManager> = mutableListOf()

    fun subscribeLogger(client: LogManager) {

        logClients.add(client)
    }

    fun unsubscribeLogger(client: LogManager) {

        logClients.remove(client)
    }

    fun logMessage(message: String) {

        println("LOG_MESSAGE: $message")

        synchronized(logClients) {
            for (logClient in logClients) {
                logClient.onLogMessage(timesTamp = glfwGetTime().toFloat(), message = message)
            }
        }
    }

    fun initResources(block:() -> Unit = { }) {

        inputManager.initManager()
        resourcesManager.initManager()
        sceneManager.initManager()
        cameraManager.initManager()
        soundManager.initManager()
        terrainManager.initManager()
        timeWeatherManager.initManager()
        chunkManager.initManager()

        block()
    }
}