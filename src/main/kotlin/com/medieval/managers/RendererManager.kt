package com.medieval.managers

import com.medieval.components.Chunk
import com.medieval.foundation.Entity
import com.medieval.ui.foundation.UIBaseComponent
import org.joml.Vector3f
import org.lwjgl.glfw.Callbacks.glfwFreeCallbacks
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFW.GLFW_BLUE_BITS
import org.lwjgl.glfw.GLFW.GLFW_GREEN_BITS
import org.lwjgl.glfw.GLFW.GLFW_OPENGL_CORE_PROFILE
import org.lwjgl.glfw.GLFW.GLFW_RED_BITS
import org.lwjgl.glfw.GLFW.glfwCreateWindow
import org.lwjgl.glfw.GLFW.glfwDestroyWindow
import org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor
import org.lwjgl.glfw.GLFW.glfwGetTime
import org.lwjgl.glfw.GLFW.glfwGetVideoMode
import org.lwjgl.glfw.GLFW.glfwPollEvents
import org.lwjgl.glfw.GLFW.glfwSetErrorCallback
import org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose
import org.lwjgl.glfw.GLFW.glfwSwapBuffers
import org.lwjgl.glfw.GLFW.glfwSwapInterval
import org.lwjgl.glfw.GLFW.glfwTerminate
import org.lwjgl.glfw.GLFW.glfwWindowHint
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.GL_BACK
import org.lwjgl.opengl.GL11.GL_BLEND
import org.lwjgl.opengl.GL11.GL_CCW
import org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT
import org.lwjgl.opengl.GL11.GL_CULL_FACE
import org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT
import org.lwjgl.opengl.GL11.GL_DEPTH_TEST
import org.lwjgl.opengl.GL11.GL_MAX_TEXTURE_SIZE
import org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA
import org.lwjgl.opengl.GL11.GL_RENDERER
import org.lwjgl.opengl.GL11.GL_SRC_ALPHA
import org.lwjgl.opengl.GL11.GL_VENDOR
import org.lwjgl.opengl.GL11.GL_VERSION
import org.lwjgl.opengl.GL11.glBlendFunc
import org.lwjgl.opengl.GL11.glClear
import org.lwjgl.opengl.GL11.glClearColor
import org.lwjgl.opengl.GL11.glCullFace
import org.lwjgl.opengl.GL11.glDisable
import org.lwjgl.opengl.GL11.glEnable
import org.lwjgl.opengl.GL11.glFrontFace
import org.lwjgl.opengl.GL11.glGetIntegerv
import org.lwjgl.opengl.GL11.glGetString
import org.lwjgl.opengl.GL20.GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS
import org.lwjgl.opengl.GL20.GL_MAX_DRAW_BUFFERS
import org.lwjgl.opengl.GL20.GL_MAX_VERTEX_ATTRIBS
import org.lwjgl.opengl.GL20.GL_SHADING_LANGUAGE_VERSION
import org.lwjgl.opengl.GL30.GL_MAX_ARRAY_TEXTURE_LAYERS
import org.lwjgl.opengl.GL31.GL_MAX_UNIFORM_BLOCK_SIZE
import org.lwjgl.system.MemoryUtil.NULL
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

class RendererManager(
    val gm: GameManager,
) {


    // Window handle.
    // =======================================================================
    var windowId: Long = -1
        private set

    private val ACTIVE_CONTEXT_VERSION_MAJOR = 4
    private val ACTIVE_CONTEXT_VERSION_MINOR = 5

    private val ACTIVE_CONTEXT_VERSION_COMPATIBILITY: Int = GLFW_OPENGL_CORE_PROFILE
    private val ACTIVE_CONTEXT_VERSION_COMPATIBILITY_name = "GLFW_OPENGL_CORE_PROFILE"

    private val entityInit: MutableList<Entity> = mutableListOf()
    private val LOCK_ENTITY_INIT: Any = Any()

    private val entityDestroy: MutableList<Entity> = mutableListOf()
    private val LOCK_ENTITY_DESTROY: Any = Any()

    private val entityInitClearStates: MutableList<Entity> = mutableListOf()
    private val LOCK_ENTITY_INIT_CLEAR_STATES: Any = Any()

    private val entityGeneric: MutableList<Entity> = mutableListOf()
    private val LOCK_ENTITY_GENERIC: Any = Any()

    private val entityChunk: MutableList<Entity> = mutableListOf()
    private val LOCK_ENTITY_CHUNKS: Any = Any()

    private val entityUI: MutableList<Entity> = mutableListOf()
    private val LOCK_ENTITY_UI: Any = Any()

    private val entitySleep: MutableList<Entity> = mutableListOf()
    private val LOCK_ENTITY_SLEEP: Any = Any()

    private val entityBaseNpc: MutableList<Entity> = mutableListOf()
    private val LOCK_ENTITY_BASE_NPC: Any = Any()

    // Limits
    // =======================================================================
    var maxTextureSize: Int = -1
    var maxArrayLayers: Int = -1
    var maxUniformBufferSize: Int = -1
    var maxVertexAttribs: Int = -1
    var maxCombinedTextureUnits: Int = -1
    var maxDrawBuffers: Int = -1

    // Extensions / Features
    // =======================================================================
    var supportsInstancing: Boolean = false
    var supportsGeometryShaders: Boolean = false
    var supportsComputeShaders: Boolean = false
    var supportsTessellation: Boolean = false

    // Info
    // =======================================================================
    var version: String = "no_info"
    var renderer: String = "no_info"
    var vendor: String = "no_info"
    var glslVersion: String = "no_info"

    // RENDERING METRICS
    // =======================================================================
    private val windowSize = 100
    private val frameTimes = LongArray(windowSize)
    private val index = AtomicInteger(0)
    private val count = AtomicInteger(0)
    private val totalTime = AtomicLong(0)

    var FPS_NOW: Int = 0
    var FPS_MAX: Int = 0

    // Average FPS over the last N frames
    val averageFPS: Double
        get() {
            val frames = count.get()
            val total = totalTime.get()
            return if (frames > 0 && total > 0) {
                1_000_000_000.0 * frames / total
            } else 0.0
        }

    private val clearColor: Vector3f = Vector3f(0f, 0f, 0f)

    fun initWindow() {

        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set()

        if (!GLFW.glfwInit()) throw IllegalStateException("Unable to initialize GLFW")

        // Request OpenGL 4.5 core profile
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, ACTIVE_CONTEXT_VERSION_MAJOR)
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, ACTIVE_CONTEXT_VERSION_MINOR)
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, ACTIVE_CONTEXT_VERSION_COMPATIBILITY)

        // Configure GLFW
        GLFW.glfwDefaultWindowHints(); // optional, window hints are already the default
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE) // window hidden after creation
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_FALSE) // window will not be resizable
//        GLFW.glfwWindowHint(GLFW.GLFW_SAMPLES, 8)

        if(gm.fullScreen) {
            //GLFW.glfwWindowHint(GLFW.GLFW_DECORATED, GLFW.GLFW_FALSE)
            val monitor = glfwGetPrimaryMonitor()
            val vidMode = glfwGetVideoMode(monitor) ?: throw RuntimeException("No video mode available.")

            gm.width = vidMode.width()
            gm.height = vidMode.height()

            glfwWindowHint(GLFW_RED_BITS, vidMode.redBits())
            glfwWindowHint(GLFW_GREEN_BITS, vidMode.greenBits())
            glfwWindowHint(GLFW_BLUE_BITS, vidMode.blueBits())
            //glfwWindowHint(GLFW_REFRESH_RATE, vidMode.refreshRate())
            //glfwWindowHint(GLFW_REFRESH_RATE, 60)

            windowId = glfwCreateWindow(vidMode.width(), vidMode.height(), gm.title, monitor, 0)

        } else {

            // Create the window
            windowId = glfwCreateWindow(gm.width, gm.height, gm.title, NULL, NULL)
        }

        if (windowId == NULL)  throw IllegalStateException("Failed to create the GLFW window")

        // Show window.
        GLFW.glfwShowWindow(windowId)
        // Make the OpenGL context current
        GLFW.glfwMakeContextCurrent(windowId)
        // Create OpenGL capabilities.
        GL.createCapabilities()

        getGPURenderingInfo()
    }

    private fun getGPURenderingInfo() {

        // Make sure GL.createCapabilities() has already been called
        version = glGetString(GL_VERSION) ?: "not_found"
        renderer = glGetString(GL_RENDERER) ?: "not_found"
        vendor = glGetString(GL_VENDOR) ?: "not_found"
        glslVersion = glGetString(GL_SHADING_LANGUAGE_VERSION) ?: "not_found"

        maxTextureSize = getInt(GL_MAX_TEXTURE_SIZE)
        maxArrayLayers = getInt(GL_MAX_ARRAY_TEXTURE_LAYERS)
        maxUniformBufferSize = getInt(GL_MAX_UNIFORM_BLOCK_SIZE)
        maxVertexAttribs = getInt(GL_MAX_VERTEX_ATTRIBS)
        maxCombinedTextureUnits = getInt(GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS)
        maxDrawBuffers = getInt(GL_MAX_DRAW_BUFFERS)

        val caps = GL.getCapabilities()

        supportsInstancing = caps.GL_ARB_instanced_arrays
        supportsGeometryShaders = caps.GL_ARB_geometry_shader4
        supportsComputeShaders = caps.GL_ARB_compute_shader
        supportsTessellation = caps.GL_ARB_tessellation_shader

        //GameManager.logMessage(message = "Instancing supported: ${caps.GL_ARB_instanced_arrays}")
        //GameManager.logMessage(message = "Geometry shaders supported: ${caps.GL_ARB_geometry_shader4}")
        //GameManager.logMessage(message = "Compute shaders supported: ${caps.GL_ARB_compute_shader}")
        //GameManager.logMessage(message = "Tesselation shaders supported: ${caps.GL_ARB_tessellation_shader}")
        //GameManager.logMessage(message = "OpenGL version: " + glGetString(GL_VERSION))
        //GameManager.logMessage(message = "Renderer: " + glGetString(GL_RENDERER))
        //GameManager.logMessage(message = "Vendor: " + glGetString(GL_VENDOR))

        gm.logMessage(message = """
            LOCAL: RENDERER_MANAGER -> 
            GPU RENDERING CAPABILITIES INFO
            =========================================================================
            OpenGL version: $version
            Renderer: $renderer
            Vendor: $vendor
            -------------------------------------------------------------------------
            Instancing supported: ${caps.GL_ARB_instanced_arrays}
            Geometry shaders supported: ${caps.GL_ARB_geometry_shader4}
            Compute shaders supported: ${caps.GL_ARB_compute_shader}
            Tesselation shaders supported: ${caps.GL_ARB_tessellation_shader}
            -------------------------------------------------------------------------
            -> maxTextureSize: $maxTextureSize
            -> maxArrayLayers: $maxArrayLayers
            -> maxUniformBufferSize: $maxUniformBufferSize
            -> maxVertexAttribs: $maxVertexAttribs
            -> maxCombinedTextureUnits: $maxCombinedTextureUnits
            -> maxDrawBuffers: $maxDrawBuffers
            =========================================================================
        """.trimIndent())
    }

    private fun getInt(pname: Int): Int {
        val tmp = IntArray(1)
        glGetIntegerv(pname, tmp)
        return tmp[0]
    }

    // Record a frame render time in nanoseconds
    fun recordFrame(timeNs: Long) {
        val i = index.getAndIncrement() % windowSize
        val old = frameTimes[i]
        frameTimes[i] = timeNs

        // Update rolling totals atomically
        totalTime.addAndGet(timeNs - old)
        if (count.get() < windowSize) count.incrementAndGet()
    }

    fun initRenderingLoop() {

        // Enable v-sync
        glfwSwapInterval(0)

        var lastFrameTime: Double = glfwGetTime()
        var deltaTime: Float = 0f
        var timeCounter: Float = 0f

        var elapsedTime: Double = glfwGetTime()
        var FPS: Int = 0

//        glEnable(GL_MULTISAMPLE)
//        glDisable(GL_MULTISAMPLE)

        glEnable(GL_DEPTH_TEST)
        glEnable(GL_CULL_FACE)
        glFrontFace(GL_CCW)
        glCullFace(GL_BACK)

        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

        while (!GLFW.glfwWindowShouldClose(windowId)) {

            val initTime = glfwGetTime()
            deltaTime = (initTime - lastFrameTime).toFloat()
            lastFrameTime = initTime

            glfwPollEvents()

            if (!gm.inputManager.getCursorState()) timeCounter += deltaTime

            // UPDATE UNIFORM BUFFER AFTER UDPATE, BECAUSE ENTITIES MAY HAVE CHANGE
            // SOME OF ITS VALUES.
            gm.globalData.setViewMatrix(matrix = gm.cameraManager.viewMatrix)
            gm.globalData.setProjMatrix(matrix = gm.cameraManager.perspectiveMatrix)
            gm.globalData.setAmbientLight(vector = gm.timeWeatherManager.ambientLight)
            gm.globalData.setSkyColor(vector = gm.timeWeatherManager.skyColor)
            gm.globalData.setTime(value = timeCounter)
            gm.globalData.setPlayerPosition(vector = gm.playerManager.eyePosition)
            gm.globalData.setChunkDistanceInitFade(value = Chunk.CHUNK_DISTANCE_INIT_FADE)
            gm.globalData.setChunkDistanceEndFade(value = Chunk.CHUNK_DISTANCE_END_FADE)

            FPS++

            // Set the clear color
            glClearColor(
                clearColor.x,
                clearColor.y,
                clearColor.z,
                1f,
            )

            glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT) // clear the framebuffer

            // UPDATE AND DRAWING
            // ================================================================
            initSleep()
            initStates()
            //
            destroyEntities()
            initEntities()
            //
            updateEntities(dt = deltaTime)
            drawEntities(dt = deltaTime)
            //
            clearStates()
            // ================================================================

            val endTime = glfwGetTime()

            recordFrame(timeNs = ((endTime - initTime) * 1_000_000_000).toLong())

            if (endTime - elapsedTime >= 1.0) {
                elapsedTime = endTime
                FPS_NOW = FPS
                FPS_MAX = averageFPS.toInt()
                FPS = 0
            }

            // swap the color buffers
            glfwSwapBuffers(windowId)
        }

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(windowId)
        glfwDestroyWindow(windowId)

        // Terminate GLFW and free the error callback
        glfwTerminate()
        glfwSetErrorCallback(null)?.free()
    }

    fun initSleep() {
        synchronized(entitySleep) {
            for (entity in entitySleep) {
                entity.sleep()
            }
            entitySleep.clear()
        }
    }

    fun initStates() {
        synchronized(entityInitClearStates) {
            for (entity in entityInitClearStates) {
                entity.initStates()
            }
        }
    }

    fun clearStates() {
        synchronized(entityInitClearStates) {
            for (entity in entityInitClearStates) {
                entity.clearStates()
            }
        }
    }

    fun destroyEntities() {

        synchronized(entityDestroy) {
            for (entity in entityDestroy) {
                entity.destroyEntity()
            }
            entityDestroy.clear()
        }
    }

    fun initEntities() {

        synchronized(entityInit) {
            for (entity in entityInit) {
                entity.initEntity()
            }
            entityInit.clear()
        }
    }

    fun updateEntities(dt: Float) {

        synchronized(entityGeneric) {
            for (entity in entityGeneric) {
                entity.update(dt)
            }
        }

        synchronized(entityChunk) {
            for (entity in entityChunk) {
                entity.update(dt)
            }
        }

        synchronized(entityUI) {
            for (entity in entityUI) {
                entity.update(dt)
            }
        }
    }

    fun drawEntities(dt: Float) {

        synchronized(entityGeneric) {
            for (entity in entityGeneric) {
                entity.drawOpaque(dt)
            }
            for (entity in entityGeneric) {
                entity.drawTransparent(dt)
            }
        }

        synchronized(entityChunk) {
            for (entity in entityChunk) {
                entity.drawOpaque(dt)
            }
            for (entity in entityChunk) {
                entity.drawTransparent(dt)
            }
        }

        glEnable(GL_BLEND)
        glDisable(GL_DEPTH_TEST)

        synchronized(entityUI) {
            for (entity in entityUI) {
                entity.drawOpaque(dt)
            }
            for (entity in entityUI) {
                entity.drawTransparent(dt)
            }
        }

        glDisable(GL_BLEND)
        glEnable(GL_DEPTH_TEST)
    }

    fun stopRenderingLoop() {

        glfwSetWindowShouldClose(windowId, true)
    }

    fun subscribeEntityInit(entity: Entity) {
        synchronized(entityInit) {
            entityInit.add(element = entity)
        }
    }

    fun subscribeEntityGeneric(entity: Entity) {
        synchronized(entityGeneric) {
            entityGeneric.add(element = entity)
        }
    }

    fun unsubscribeEntityGeneric(entity: Entity) {
        synchronized(entityGeneric) {
            entityGeneric.remove(element = entity)
        }
    }

    fun subscribeEntityChunk(entity: Entity) {
        synchronized(entityChunk) {
            entityChunk.add(element = entity)
        }
    }

    fun unsubscribeEntityChunk(entity: Entity) {
        synchronized(entityChunk) {
            entityChunk.remove(element = entity)
        }
    }

    fun subscribeEntityUI(entity: Entity) {
        synchronized(entityUI) {
            entityUI.add(element = entity)
        }
    }

    fun unsubscribeEntityUI(entity: Entity) {
        synchronized(entityUI) {
            entityUI.remove(element = entity)
        }
    }

    fun subscribeEntityInitClearStates(entity: Entity) {
        synchronized(entityInitClearStates) {
            entityInitClearStates.add(element = entity)
        }
    }

    fun unsubscribeEntityInitClearStates(entity: Entity) {
        synchronized(entityInitClearStates) {
            entityInitClearStates.remove(element = entity)
        }
    }

    fun subscribeEntityDestroy(entity: UIBaseComponent) {
        synchronized(entityDestroy) {
            entityDestroy.add(element = entity)
        }
    }

    fun setClearColor(r: Float, g: Float, b: Float) {

        clearColor.set(r, g, b)
    }

    fun subscribeEntitySleeping(entity: Entity) {
        synchronized(entitySleep) {
            entitySleep.add(element = entity)
        }
    }
}