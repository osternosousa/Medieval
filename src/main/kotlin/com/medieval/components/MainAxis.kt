package com.medieval.components

import com.medieval.foundation.Entity
import com.medieval.managers.GameManager
import com.medieval.opengl.IndexBufferObject
import com.medieval.opengl.ProgramShaderObject
import com.medieval.opengl.VertexArrayObject
import com.medieval.opengl.VertexBufferObject
import com.medieval.utility.UtilityM
import org.lwjgl.opengl.GL11

class MainAxis(
    val gm: GameManager
) : Entity() {

    private val VAO: VertexArrayObject = VertexArrayObject()
    private val VBO: VertexBufferObject = VertexBufferObject()
    private val EBO: IndexBufferObject = IndexBufferObject()
    private val PID: ProgramShaderObject = ProgramShaderObject()

    private var shaderSourceFilePath: String = "programs/mainaxis.glsl"
    private var indicesComponents: IntArray = intArrayOf(3, 3)
    private var type: Int = GL11.GL_FLOAT

    private val vertices: FloatArray = floatArrayOf(
        // XYZ            RGB
        +10_000f, +0f, +0f,    1f, 0f, 0f,   // 0
        -10_000f, +0f, +0f,    1f, 0f, 0f,   // 1

        +0f, +0f, +10_000f,    0f, 1f, 0f,   // 2
        +0f, +0f, -10_000f,    0f, 1f, 0f,   // 3

        +0f, +10_000f, +0f,    0f, 0f, 1f,   // 4
        +0f, -10_000f, +0f,    0f, 0f, 1f,   // 5
    )

    private val indices: IntArray = intArrayOf(
        0, 1, 1, 0,
        2, 3, 3, 2,
        4, 5, 5, 4,
    )


    override fun update(dt: Float) {


    }

    override fun initEntity() {

        VAO.init()
        VAO.bind()

        VBO.init()
        VBO.bind()
        VBO.bufferData(data = vertices)

        EBO.init()
        EBO.bind()
        EBO.bufferData(data = indices)

        VAO.enableAndSetAllVertexAttributes(indicesComponents = indicesComponents, type = type)

        val shaders = UtilityM.getShadersFromTextFile(path = shaderSourceFilePath)
        PID.init(vertexShader = shaders[0], fragmentShader = shaders[1])
        gm.logMessage(message = PID.log)

        VAO.unbind()
        VBO.unbind()
        EBO.unbind()
        PID.unbindUseProgram()

        gm.rendererManager.subscribeEntityGeneric(entity = this)

        gm.logMessage(message = "Main Axis InitEntity ended!")
    }

    override fun drawOpaque(dt: Float) {

        VAO.bind()
        PID.bindUseProgram()

        PID.setUniformMatrix4f("uPerspectiveMatrix", gm.cameraManager.perspectiveMatrix)
        PID.setUniformMatrix4f("uViewMatrix", gm.cameraManager.viewMatrix)

        EBO.drawElementsLines()
    }
}