package com.medieval.managers

import com.medieval.foundation.Entity
import com.medieval.foundation.InputManagerClient
import org.lwjgl.BufferUtils
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFW.GLFW_CURSOR
import org.lwjgl.glfw.GLFW.GLFW_CURSOR_DISABLED
import org.lwjgl.glfw.GLFW.GLFW_CURSOR_NORMAL
import org.lwjgl.glfw.GLFW.glfwSetInputMode
import org.lwjgl.glfw.GLFWCharCallback
import org.lwjgl.glfw.GLFWCursorPosCallback
import org.lwjgl.glfw.GLFWKeyCallback
import org.lwjgl.glfw.GLFWMouseButtonCallback
import org.lwjgl.glfw.GLFWScrollCallback
import org.lwjgl.glfw.GLFWWindowFocusCallback
import java.nio.DoubleBuffer
import java.util.concurrent.CopyOnWriteArrayList

class InputManager(
    val gm: GameManager
) : Entity() {

    private val keysPressed = BooleanArray(350)
    private val keysTyped = BooleanArray(350)
    private val mousePressed = BooleanArray(3)
    private val mouseClicked = BooleanArray(3)
    private var previousPosX = 0.0
    private var previousPosY = 0.0
    private var scrollOffsetX = 0.0
    private var scrollOffsetY = 0.0

    private var xPos: DoubleBuffer = BufferUtils.createDoubleBuffer(1)
    private var yPos: DoubleBuffer = BufferUtils.createDoubleBuffer(1)

    private val clients: CopyOnWriteArrayList<InputManagerClient> = CopyOnWriteArrayList<InputManagerClient>()

    private var isCursorEnabled = true
    private var isMouseDragging = false

    private var keyCallback: GLFWKeyCallback? = null
    private var cursorPosCallback: GLFWCursorPosCallback? = null
    private var mouseButtonCallback: GLFWMouseButtonCallback? = null
    private var windowFocusCallback: GLFWWindowFocusCallback? = null
    private var charCallback: GLFWCharCallback? = null
    private var scrollCallback: GLFWScrollCallback? = null

    fun initManager() {

        gm.rendererManager.subscribeEntityInitClearStates(entity = this)

        keyCallback = GLFW.glfwSetKeyCallback(gm.rendererManager.windowId, this::keyCallback)
        cursorPosCallback = GLFW.glfwSetCursorPosCallback(gm.rendererManager.windowId, this::cursorPosCallback)
        mouseButtonCallback = GLFW.glfwSetMouseButtonCallback(gm.rendererManager.windowId, this::mouseButtonCallback)
        windowFocusCallback = GLFW.glfwSetWindowFocusCallback(gm.rendererManager.windowId,this::windowsFocusCallback)
        charCallback = GLFW.glfwSetCharCallback(gm.rendererManager.windowId,this::charCallback)
        scrollCallback = GLFW.glfwSetScrollCallback(gm.rendererManager.windowId,this::mouseScrollCallback)
    }

    private fun windowsFocusCallback(window: Long, focused: Boolean) {

        gm.logMessage(message = "LOCAL: INPUT_MANAGER -> \nWindows Focus Callback: focused -> $focused")

        //synchronized(clients) {}
        if (!focused) {
            for (client in clients) {
                client.onFocus(focused = false, isCursorEnabled = isCursorEnabled)
            }
        }

        setCursorState(isEnabled = true)
    }

    fun setCursorState(isEnabled: Boolean) {

        gm.logMessage(message = "LOCAL: INPUT_MANAGER -> \nSetting Cursor State: state -> $isEnabled")

        isCursorEnabled = isEnabled

        val action = if (gm.inputManager.isCursorEnabled) GLFW_CURSOR_NORMAL else GLFW_CURSOR_DISABLED

        /*
        Input Mode somente pode ser gerenciado da main thread. Este callback é chamado
        pela main thread. Sugestão: Caso seja necessário realizar a alteração a partir
        de outra thread, pode ser capturado o disparo na thread de interesse e encaminhada
        a ação para a main thread, ou através de uma dist flag na main thread ou através
        de uma queue na main thread.

        Some GLFW functions (like glfwSetInputMode, glfwSetWindowShouldClose,
        glfwSetCursorPos) are windowing calls, not GL calls. They must be executed on
        the main thread (the one that owns the window/event loop). If you try to call
        them from the render thread, they can silently fail or behave inconsistently.
        */
        glfwSetInputMode(gm.rendererManager.windowId, GLFW_CURSOR, action)

        if (!isEnabled) {
            for (client in clients) {
                client.onFocus(focused = false, isCursorEnabled = isCursorEnabled)
            }
        }
    }

    fun cursorPosCallback(window: Long, xpos: Double, ypos: Double) {

        //glfwGetCursorPos(window, xPos, yPos);

        val deltaX = xpos - previousPosX
        val deltaY = ypos - previousPosY
        previousPosX = xpos
        previousPosY = ypos

        //if (isCursorEnabled) { }

        //synchronized(clients) {}
        for (client in clients) {
            client.onMove(isCursorEnabled = isCursorEnabled, deltaX = deltaX, deltaY = deltaY, posX = xpos, posY = ypos)
        }

        isMouseDragging = mousePressed[0] || mousePressed[1] || mousePressed[2]

        if (isMouseDragging) {
            //synchronized(clients) {}
            for (client in clients) {
                client.onDrag(isCursorEnabled = isCursorEnabled, deltaX = deltaX, deltaY = deltaY, posX = xpos, posY = ypos)
            }
        }
    }

    fun keyCallback(window: Long, key: Int, scancode: Int, action: Int, mods: Int) {

        if (key < 0) return

        when (action) {
            GLFW.GLFW_PRESS -> {
                keysPressed[key] = true
            }

            GLFW.GLFW_RELEASE -> {
                keysPressed[key] = false
                keysTyped[key] = true
            }

            else -> {}
        }
    }

    fun charCallback(window: Long, codepoint: Int) {

        // Dispara onTyping para o InputManagerCliente realizar o onComponentTyping sobre
        // o component ativo. Aqui, o charCallback vai disparar apenas quando possuir valores
        // de codepoint válidos com caracteres válidos, nunca quando for teclado um botão
        // que não corresponde a um caractere válido, logo aqui enviamos key = -1.

        //synchronized(clients) {}
        for (client in clients) {
            client.onTyping(codepoint = codepoint, isCursorEnabled = isCursorEnabled)
        }
    }

    fun mouseButtonCallback(window: Long, button: Int, action: Int, mods: Int) {

        when (action) {
            GLFW.GLFW_PRESS -> {
                mousePressed[button] = true

                GLFW.glfwGetCursorPos(window, xPos, yPos)

                //synchronized(clients) {}
                for (client in clients) {
                    client.onPress(posX = xPos.get(0), posY = yPos.get(0), isCursorEnabled = isCursorEnabled)
                }
            }

            GLFW.GLFW_RELEASE -> {
                mousePressed[button] = false
                mouseClicked[button] = true
                isMouseDragging = false

                GLFW.glfwGetCursorPos(window, xPos, yPos)

                //synchronized(clients) {}
                for (client in clients) {
                    client.onClick(posX = xPos.get(0), posY = yPos.get(0), isCursorEnabled = isCursorEnabled)
                }
            }

            else -> {}
        }
    }

    fun getCursorState(): Boolean {

        return isCursorEnabled
    }

    fun mouseScrollCallback(window: Long, xoffset: Double, yoffset: Double) {

        scrollOffsetX = xoffset
        scrollOffsetY = yoffset
    }

    /** Returns true if the key was pressed and not released.  */
    fun isKeyPressed(key: Int): Boolean {

        return keysPressed[key]
    }

    /** Returns true if the key was pressed and released.  */
    fun isKeyTyped(key: Int): Boolean {

        //val result = keysTyped[key]
        //keysTyped[key] = false
        //return result

        return keysTyped[key]
    }

    fun isMousePressed(key: Int): Boolean {

        return mousePressed[key]
    }

    fun getScrollOffsetX(): Double {

        return scrollOffsetX
    }

    fun getScrollOffsetY(): Double {

        return scrollOffsetY
    }

    /**
     * GLFW_MOUSE_BUTTON_1      = 0,
     * GLFW_MOUSE_BUTTON_2      = 1,
     * GLFW_MOUSE_BUTTON_3      = 2,
     * GLFW_MOUSE_BUTTON_4      = 3,
     * GLFW_MOUSE_BUTTON_5      = 4,
     * GLFW_MOUSE_BUTTON_6      = 5,
     * GLFW_MOUSE_BUTTON_7      = 6,
     * GLFW_MOUSE_BUTTON_8      = 7,
     * GLFW_MOUSE_BUTTON_LAST   = GLFW_MOUSE_BUTTON_8,
     * GLFW_MOUSE_BUTTON_LEFT   = GLFW_MOUSE_BUTTON_1,
     * GLFW_MOUSE_BUTTON_RIGHT  = GLFW_MOUSE_BUTTON_2,
     * GLFW_MOUSE_BUTTON_MIDDLE = GLFW_MOUSE_BUTTON_3;
     */
    fun isMouseButtonClicked(button: Int): Boolean {
        //val result = mouseClicked[button]
        //keysTyped[button] = false
        //return result

        return mouseClicked[button]
    }

    /** Clients who implements interface ImputManagerClient will receive input update
     * from mouse dragging and mouse position trough functions:
     *
     *
     * fun onMove(deltaX: Double, deltaY: Double, posX: Double, posY: Double, isCursorEnabled: Boolean)
     *
     * fun onPress(posX: Double, posY: Double, isCursorEnabled: Boolean)
     *
     * fun onClick(posX: Double, posY: Double, isCursorEnabled: Boolean)
     *
     * fun onLongClick(posX: Double, posY: Double, isCursorEnabled: Boolean)
     *
     * fun onTyping(codepoint: Int, isCursorEnabled: Boolean)
     *
     * fun onDrag(deltaX: Double, deltaY: Double, posX: Double, posY: Double, isCursorEnabled: Boolean)
     *
     * fun onFocus(focused: Boolean, isCursorEnabled: Boolean)
     */
    fun subscribeClient(client: InputManagerClient) {
        //synchronized(clients) {}
        //if (!clients.contains(client)) { }
        clients.addIfAbsent(client)
    }

    fun unsubscribeClient(client: InputManagerClient) {
        //synchronized(clients) {}
        clients.remove(client)
    }

    /** Ao final do frame, seta todos os valores de key e mouse pressed
    e clicked para false. Assim, todos os componentes observando estes
    estados podem realizar suas ações uma vez cada um e então o estado
    retorna para false aguardando outra mudança para true.

    Estados de keysPressed devem permanecer entre os frames, pois
    somente são registrados durante o press. Caso seja removido aqui,
    não vai permanecer no próximo frame, caso ainda ainda não tenha
    sido realizado o GLFW_RELEASE, do contrário assim vai agir como
    um typed. O mesmo para os estados de mouseClicked.
     */
    override fun clearStates() {

        keysTyped.fill(false)
        mouseClicked.fill(false)

        scrollOffsetX = 0.0
        scrollOffsetY = 0.0
    }

    fun cleanup() {

        keyCallback?.free()
        cursorPosCallback?.free()
        mouseButtonCallback?.free()
        windowFocusCallback?.free()
        charCallback?.free()
        scrollCallback?.free()
    }
}