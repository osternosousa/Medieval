package com.medieval.managers

import com.medieval.foundation.Entity
import com.medieval.foundation.InputManagerClient
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW.GLFW_KEY_A
import org.lwjgl.glfw.GLFW.GLFW_KEY_D
import org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE
import org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_SHIFT
import org.lwjgl.glfw.GLFW.GLFW_KEY_S
import org.lwjgl.glfw.GLFW.GLFW_KEY_W
import kotlin.math.cos
import kotlin.math.sin

class PlayerManager(
    val gm: GameManager
) : Entity(), InputManagerClient {


    var anglePlaneXZ: Double = 0.0
    var anglePlaneYZ: Double = 0.0

    var eyePosition: Vector3f = Vector3f()
    var centerTarget: Vector3f = Vector3f()
    var upVector: Vector3f = Vector3f(0f, 1f, 0f)

    var speedEye: Float = 0.05f
    var speedWalk: Float = 2f

    var isWalkingForward: Boolean = false
        get() = gm.inputManager.isKeyPressed(key = GLFW_KEY_W)
    var isWalkingBackwards: Boolean = false
        get() = gm.inputManager.isKeyPressed(key = GLFW_KEY_S)
    var isWalkingRight: Boolean = false
        get() = gm.inputManager.isKeyPressed(key = GLFW_KEY_D)
    var isWalkingLeft: Boolean = false
        get() = gm.inputManager.isKeyPressed(key = GLFW_KEY_A)


    override fun update(dt: Float) {

        if (gm.inputManager.isKeyTyped(GLFW_KEY_ESCAPE)) {

            gm.inputManager.setCursorState(isEnabled = !gm.inputManager.getCursorState())
        }

        if (gm.inputManager.getCursorState()) return

        val sinPlaneXZ = sin(Math.toRadians(anglePlaneXZ)).toFloat()
        val cosPlaneXZ = cos(Math.toRadians(anglePlaneXZ)).toFloat()
        val sinPlaneYZ = sin(Math.toRadians(anglePlaneYZ)).toFloat()
        val cosPlaneYZ = cos(Math.toRadians(anglePlaneYZ)).toFloat()

        var tempSpeedWalk: Float = speedWalk

        if (gm.inputManager.isKeyPressed(key = GLFW_KEY_LEFT_SHIFT)) tempSpeedWalk = speedWalk * 3

        if (isWalkingForward) {

            eyePosition.z += sinPlaneXZ * tempSpeedWalk * dt
            eyePosition.y += sinPlaneYZ * tempSpeedWalk * dt
            eyePosition.x += cosPlaneXZ * tempSpeedWalk * dt
        }

        if (isWalkingBackwards) {

            eyePosition.z += (sin(Math.toRadians(anglePlaneXZ + 180.0)) * tempSpeedWalk * dt).toFloat()
            eyePosition.y += (sin(Math.toRadians(anglePlaneYZ + 180.0)) * tempSpeedWalk * dt).toFloat()
            eyePosition.x += (cos(Math.toRadians(anglePlaneXZ + 180.0)) * tempSpeedWalk * dt).toFloat()
        }

        if (isWalkingRight) {

            eyePosition.z -= (sin(Math.toRadians(anglePlaneXZ - 90.0)) * tempSpeedWalk * dt).toFloat()
            eyePosition.x -= (cos(Math.toRadians(anglePlaneXZ - 90.0)) * tempSpeedWalk * dt).toFloat()
        }

        if (isWalkingLeft) {

            eyePosition.z -= (sin(Math.toRadians(anglePlaneXZ + 90.0)) * tempSpeedWalk * dt).toFloat()
            eyePosition.x -= (cos(Math.toRadians(anglePlaneXZ + 90.0)) * tempSpeedWalk * dt).toFloat()
        }

        centerTarget.z = eyePosition.z + sinPlaneXZ * cosPlaneYZ
        centerTarget.y = eyePosition.y + sinPlaneYZ
        centerTarget.x = eyePosition.x + cosPlaneXZ * cosPlaneYZ

        gm.cameraManager.updateViewMatrix(
            eyePosition = eyePosition,
            centerTarget = centerTarget,
            upVector = upVector
        )
    }

    override fun onMove(deltaX: Double, deltaY: Double, posX: Double, posY: Double, isCursorEnabled: Boolean) {

        if (isCursorEnabled) return

        anglePlaneXZ += deltaX * speedEye
        anglePlaneYZ -= deltaY * speedEye

        if (anglePlaneXZ > 360.0) anglePlaneXZ -= 360.0
        if (anglePlaneXZ < 0.0) anglePlaneXZ += 360.0
        if (anglePlaneYZ > 89.0) anglePlaneYZ = 89.0
        if (anglePlaneYZ < -89.0) anglePlaneYZ = -89.0




        //gm.logMessage(message = "anglePlaneXZ: ${anglePlaneXZ.decimalStringFormat()}, anglePlaneYZ: ${anglePlaneYZ.decimalStringFormat()}")
    }

    override fun onPress(posX: Double, posY: Double, isCursorEnabled: Boolean) {


    }

    override fun onClick(posX: Double, posY: Double, isCursorEnabled: Boolean) {


    }

    override fun onLongClick(posX: Double, posY: Double, isCursorEnabled: Boolean) {


    }

    override fun onTyping(codepoint: Int, isCursorEnabled: Boolean) {


    }

    override fun onDrag(deltaX: Double, deltaY: Double, posX: Double, posY: Double, isCursorEnabled: Boolean) {


    }

    override fun onFocus(focused: Boolean, isCursorEnabled: Boolean) {


    }
}