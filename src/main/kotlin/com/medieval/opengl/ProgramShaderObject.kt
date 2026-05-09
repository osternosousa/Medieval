package com.medieval.opengl

import org.joml.Matrix4f
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL20
import java.nio.FloatBuffer

class ProgramShaderObject {

    // Shared states.
    private companion object {

        private var CURRENT_BOUND_PROGRAM: Int = -1
        private const val MATRICES_POOL_MAX_SIZE: Int = 10
        private const val MATRIX_SIZE: Int = 16
        private val matricesPool: ArrayDeque<FloatBuffer> = ArrayDeque<FloatBuffer>(MATRICES_POOL_MAX_SIZE)
    }

    // Unique states per ProgramShaderObject.
    private val UNIFORMS_LOCATIONS: MutableMap<String, Int> = HashMap<String, Int>()
    var PID_ID = -1
        private set

    var log: String = ""
        private set

    fun init(vertexShader: String, fragmentShader: String) {

        log = ""

        val vsID = GL20.glCreateShader(GL20.GL_VERTEX_SHADER)
        val fsID = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER)

        GL20.glShaderSource(vsID, vertexShader)
        GL20.glShaderSource(fsID, fragmentShader)

        var result: String

        GL20.glCompileShader(vsID)
        result = GL20.glGetShaderInfoLog(vsID)
        if (result.isEmpty()) result = "Vertex Shader Compiled Successfully!"
        //println("VER_SHAD_INFO_LOG: $result")
        log += "VER_SHAD_INFO_LOG: $result"

        GL20.glCompileShader(fsID)
        result = GL20.glGetShaderInfoLog(fsID)
        if (result.isEmpty()) result = "Fragment Shader Compiled Successfully!"
        //println("FAG_SHAD_INFO_LOG: $result")
        log += "\nFAG_SHAD_INFO_LOG: $result"

        PID_ID = GL20.glCreateProgram()

        GL20.glAttachShader(PID_ID, vsID)
        GL20.glAttachShader(PID_ID, fsID)

        GL20.glLinkProgram(PID_ID)
        result = GL20.glGetProgramInfoLog(PID_ID)
        if (result.isEmpty()) result = "Program Linked Successfully!"
        //println("PROGRAM_INFO_LOG: $result")
        log += "\nPROGRAM_INFO_LOG: $result"

        GL20.glDeleteShader(vsID)
        GL20.glDeleteShader(fsID)
    }

    fun bindUseProgram() {

        if (CURRENT_BOUND_PROGRAM != PID_ID) {
            GL20.glUseProgram(PID_ID)
            CURRENT_BOUND_PROGRAM = PID_ID
        }
    }

    fun unbindUseProgram() {

        if (CURRENT_BOUND_PROGRAM != -1) {
            GL20.glUseProgram(0)
            CURRENT_BOUND_PROGRAM = -1
        }
    }

    fun getUniformLocation(name: String): Int {

        val locationID = UNIFORMS_LOCATIONS.get(name)

        if (locationID != null) return locationID

        val loc = GL20.glGetUniformLocation(PID_ID, name)
        UNIFORMS_LOCATIONS[name] = loc
        return loc
    }

    fun setUniformFloat(location: Int, value: Float) {

        GL20.glUniform1f(location, value)
    }

    fun setUniformInt(location: Int, value: Int) {

        GL20.glUniform1i(location, value)
    }

    fun setUniformVector2f(location: Int, x: Float, y: Float) {

        GL20.glUniform2f(location, x, y)
    }

    fun setUniformVector3f(location: Int, x: Float, y: Float, z: Float) {

        GL20.glUniform3f(location, x, y, z)
    }

    fun setUniformVector3f(location: Int, value: FloatArray) {

        GL20.glUniform3fv(location, value)
    }

    fun setUniformVector4f(location: Int, x: Float, y: Float, z: Float, w: Float) {

        GL20.glUniform4f(location, x, y, z, w)
    }

    fun setUniformMatrix4f(location: Int, matrix: FloatBuffer) {

        GL20.glUniformMatrix4fv(location, false, matrix)
    }

    fun setUniformMatrix4f(location: Int, matrix: Matrix4f) {

        val mat = acquireFloatBufferMat4()
        matrix.get(mat)

        GL20.glUniformMatrix4fv(location, false, mat)

        releaseFloatBufferMat4(mat)
    }

    fun setUniformFloat(name: String, value: Float) {

        GL20.glUniform1f(getUniformLocation(name), value)
    }

    fun setUniformInt(name: String, value: Int) {

        GL20.glUniform1i(getUniformLocation(name), value)
    }

    fun setUniformVector2f(name: String, x: Float, y: Float) {

        GL20.glUniform2f(getUniformLocation(name), x, y)
    }

    fun setUniformVector3f(name: String, x: Float, y: Float, z: Float) {

        GL20.glUniform3f(getUniformLocation(name), x, y, z)
    }

    fun setUniformVector3f(name: String, value: FloatArray) {

        GL20.glUniform3fv(getUniformLocation(name), value)
    }

    fun setUniformVector4f(name: String, x: Float, y: Float, z: Float, w: Float) {

        GL20.glUniform4f(getUniformLocation(name), x, y, z, w)
    }

    fun setUniformMatrix4f(name: String, matrix: FloatBuffer) {

        GL20.glUniformMatrix4fv(getUniformLocation(name), false, matrix)
    }

    fun setUniformMatrix4f(name: String, matrix: Matrix4f) {

        val mat = acquireFloatBufferMat4()
        matrix.get(mat)

        GL20.glUniformMatrix4fv(getUniformLocation(name), false, mat)

        releaseFloatBufferMat4(mat)
    }

    private fun acquireFloatBufferMat4(): FloatBuffer {

        if (matricesPool.isEmpty()) {
            return BufferUtils.createFloatBuffer(MATRIX_SIZE)
        }

        return matricesPool.removeFirst()
    }

    private fun releaseFloatBufferMat4(mat: FloatBuffer) {

        mat.clear()
        if (matricesPool.size < MATRICES_POOL_MAX_SIZE) matricesPool.add(mat)
    }

    fun destroy() {

        GL20.glDeleteProgram(PID_ID)
    }
}