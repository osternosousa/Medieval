package com.medieval.managers

import org.joml.Matrix4f
import org.joml.Vector3f

class CameraManager(
    val gm: GameManager
) {

    val perspectiveMatrix: Matrix4f = Matrix4f()
    val orthographicMatrix: Matrix4f = Matrix4f()
    val viewMatrix: Matrix4f = Matrix4f()

    var fov: Float = 90f
    var ratio: Float = 1f
    var zNear: Float = 0.1f
    var zFar: Float = 10000f

    fun initManager() {

        ratio = gm.width / gm.height.toFloat()

        perspectiveMatrix.setPerspective(
            /* fovy = */ fov,
            /* aspect = */ ratio,
            /* zNear = */ zNear,
            /* zFar = */ zFar
        )

        viewMatrix.setLookAt(
            /* eye = */ Vector3f(0f, 1f, 2f),
            /* center = */ Vector3f(0f, 0f, 0f),
            /* up = */ Vector3f(0f, 1f, 0f),
        )

        orthographicMatrix.setOrtho(
            /* left = */ 0f,
            /* right = */ gm.width.toFloat(),
            /* bottom = */ gm.height.toFloat(),
            /* top = */ 0f,
            /* zNear = */ 1f,
            /* zFar = */ -1f
        )
    }

    fun updateViewMatrix(
        eyePosition: Vector3f,
        centerTarget: Vector3f,
        upVector: Vector3f,
    ) {

        viewMatrix.setLookAt(
            /* eyeX = */ eyePosition.x,
            /* eyeY = */ eyePosition.y,
            /* eyeZ = */ eyePosition.z,
            /* centerX = */ centerTarget.x,
            /* centerY = */ centerTarget.y,
            /* centerZ = */ centerTarget.z,
            /* upX = */ upVector.x,
            /* upY = */ upVector.y,
            /* upZ = */ upVector.z,
        )
    }
}