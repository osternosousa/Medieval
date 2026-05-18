import com.medieval.managers.GameManager


fun main() {

    val gm: GameManager = GameManager(title = "Medieval", width = 1280, height = 720, fullScreen = false)

    gm.rendererManager.initWindow()

    gm.initResources {

        gm.sceneManager.runSceneA()
    }

    gm.rendererManager.initRenderingLoop()
}