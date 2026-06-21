package com.medieval.managers

import com.medieval.foundation.CubeM
import com.medieval.foundation.Entity
import com.medieval.foundation.InputManagerClient
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW.GLFW_KEY_1
import org.lwjgl.glfw.GLFW.GLFW_KEY_A
import org.lwjgl.glfw.GLFW.GLFW_KEY_D
import org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE
import org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_CONTROL
import org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_SHIFT
import org.lwjgl.glfw.GLFW.GLFW_KEY_S
import org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE
import org.lwjgl.glfw.GLFW.GLFW_KEY_W

class PlayerManager(
    val gm: GameManager
) : Entity(), MovingEntity, InputManagerClient {

    override var collisionWidth: Float = 0.5f
    override var collisionLength: Float = 0.5f
    override var collisionHeight: Float = 1.5f

    override var collisionBounds: CubeM = CubeM(width = collisionWidth, length = collisionLength, height = collisionHeight)

    override var isMovingForward: Boolean = false
    override var isMovingBackwards: Boolean = false
    override var isMovingRight: Boolean = false
    override var isMovingLeft: Boolean = false

    override var isRunning: Boolean = false
    override var isJumping: Boolean = false
    override var isUnderWater: Boolean = false
    override var isFreeFlying: Boolean = false

    override var walkSpeed: Float = 1f
    override var runSpeed: Float = 3f
    override var runWalkSpeedDelayMaxValue: Float = 0.35f
    override var runWalkSpeedDelayCounter: Float = 0f
    override var dragSpeed: Float = 1f

    override var anglePlaneXZ: Double = 0.0
    override var anglePlaneYZ: Double = 0.0
    override var anglePlaneXY: Double = 0.0

    override var jumpingAnglePlaneXZ: Double = 0.0

    override var jumpCounter: Float = 0f
    override var jumpInitialPositionY: Float = 0f

    override var fallCounter: Float = 0f
    override var fallInitialPositionY: Float = 0f

    override var eyePosition: Vector3f = Vector3f()
    override var centerTarget: Vector3f = Vector3f()
    override var upVector: Vector3f = Vector3f(0f, 1f, 0f)

    override var owner: MovingEntity = this

    var speedEye: Float = 0.05f
    var speedWalk: Float = 2f

    val tempEyePosition: Vector3f = Vector3f()

    override fun update(dt: Float) {

        if (gm.inputManager.isKeyTyped(GLFW_KEY_ESCAPE)) {

            gm.inputManager.setCursorState(isEnabled = !gm.inputManager.getCursorState())
        }

        if (gm.inputManager.isKeyPressed(key = GLFW_KEY_LEFT_CONTROL)) {

            if (gm.inputManager.isKeyTyped(key = GLFW_KEY_1)) isFreeFlying = !isFreeFlying
        }

        if (gm.inputManager.getCursorState()) return

        if (gm.inputManager.isKeyPressed(key = GLFW_KEY_SPACE)) isJumping = true

        isMovingForward = gm.inputManager.isKeyPressed(key = GLFW_KEY_W)
        isMovingBackwards = gm.inputManager.isKeyPressed(key = GLFW_KEY_S)
        isMovingRight = gm.inputManager.isKeyPressed(key = GLFW_KEY_D)
        isMovingLeft = gm.inputManager.isKeyPressed(key = GLFW_KEY_A)

        isRunning = gm.inputManager.isKeyPressed(key = GLFW_KEY_LEFT_SHIFT)
        //isJumping = gm.inputManager.isKeyPressed(key = GLFW_KEY_SPACE)

        MovingEntity.checkWater(gm = gm, movingEntity = this)
        MovingEntity.performSideMovement(gm = gm, movingEntity = this, deltaTime = dt)
        MovingEntity.performJump(gm = gm, movingEntity = this, deltaTime = dt)
        MovingEntity.performFall(gm = gm, movingEntity = this, deltaTime = dt)

        tempEyePosition.x = eyePosition.x
        tempEyePosition.y = eyePosition.y
        tempEyePosition.z = eyePosition.z

        gm.cameraManager.updateViewMatrix(
            eyePosition = tempEyePosition,
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
        if (anglePlaneYZ > 89.9) anglePlaneYZ = 89.9
        if (anglePlaneYZ < -89.9) anglePlaneYZ = -89.9

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