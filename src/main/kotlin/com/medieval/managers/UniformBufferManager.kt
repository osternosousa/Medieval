package com.medieval.managers

import com.medieval.foundation.DataType
import com.medieval.opengl.UniformBufferObject
import org.joml.Matrix4f
import org.joml.Vector2f
import org.joml.Vector3f
import org.joml.putMatrix4f
import org.joml.putVector2f
import org.joml.putVector3f
import org.lwjgl.opengl.GL11.glGetInteger
import org.lwjgl.opengl.GL30.glBindBufferBase
import org.lwjgl.opengl.GL31.GL_MAX_UNIFORM_BLOCK_SIZE
import org.lwjgl.opengl.GL31.GL_UNIFORM_BUFFER
import java.nio.ByteBuffer

/** Creates and manages one uniform buffer tha can be updated permanently without
the need for map and unmap during frames, using glBufferStorage with
GL_DYNAMIC_STORAGE_BIT when creating the buffer for persistent mapping.

- If you use GL_MAP_COHERENT_BIT, writes are automatically visible to the GPU.
- If you don’t, you must call glFlushMappedBufferRange to make CPU writes visible
before the GPU reads.

Synchronization
- Even with persistent mapping, you must avoid writing into memory the GPU is
currently reading.
- Common solution: double buffering or ring buffers. You allocate a larger buffer
and advance a write pointer each frame, so CPU and GPU don’t fight over the same region.
 */
class UniformBufferManager(
    val gm: GameManager,
    val bindingPointNumber: Int,
) {

    var bufferSize: Long = 0
    var bufferId: Int = -1
    private lateinit var mappedBuffer: ByteBuffer

    private lateinit var UBO: UniformBufferObject

    /* SIZE, OFFSET
    Here, each property is build its initial offset considering the offset information of the previous one. */
    private val viewMatrix: Pair<DataType, Int> = Pair(DataType.MAT4, 0)
    private val projMatrix: Pair<DataType, Int> = Pair(DataType.MAT4, viewMatrix.second + viewMatrix.first.size)
    private val orthoMatrix: Pair<DataType, Int> = Pair(DataType.MAT4, projMatrix.second + projMatrix.first.size)
    private val modelMatrix: Pair<DataType, Int> = Pair(DataType.MAT4, orthoMatrix.second + orthoMatrix.first.size)
    private val ambientLight: Pair<DataType, Int> = Pair(DataType.VEC3, modelMatrix.second + modelMatrix.first.size)
    private val skyColor: Pair<DataType, Int> = Pair(DataType.VEC3, ambientLight.second + ambientLight.first.size)
    private val screenResolution: Pair<DataType, Int> = Pair(DataType.VEC2, skyColor.second + skyColor.first.size)
    private val time: Pair<DataType, Int> = Pair(DataType.FLOAT, screenResolution.second + screenResolution.first.size)
    private val playerPosition: Pair<DataType, Int> = Pair(DataType.VEC3, time.second + time.first.size)
    private val chunkDistanceInitFade: Pair<DataType, Int> = Pair(DataType.FLOAT, playerPosition.second + playerPosition.first.size)
    private val chunkDistanceEndFade: Pair<DataType, Int> = Pair(DataType.FLOAT, chunkDistanceInitFade.second + chunkDistanceInitFade.first.size)


    fun init() {

        bufferSize = (0 + viewMatrix.first.size
                + projMatrix.first.size
                + orthoMatrix.first.size
                + modelMatrix.first.size
                + ambientLight.first.size
                + skyColor.first.size
                + screenResolution.first.size
                + time.first.size
                + playerPosition.first.size
                + chunkDistanceInitFade.first.size
                + chunkDistanceEndFade.first.size).toLong()

        if (bufferSize > glGetInteger(GL_MAX_UNIFORM_BLOCK_SIZE)) {

            throw RuntimeException("UBO too large for this GPU!")
        }

        UBO = UniformBufferObject(bindingPointNumber = bindingPointNumber, bufferSize = bufferSize)
        UBO.init()

//        val result = OpenGLM.createReturnIdMappedBufferUBO(
//            bindingPointNumber = bindingPointNumber,
//            bufferSize = bufferSize
//        )

//        if (result.second == null) throw RuntimeException("Failed to initialize mapped buffer")

//        bufferId = result.first
//        mappedBuffer = result.second!!

        bufferId = UBO.UBO_ID
        mappedBuffer = UBO.BUFFER
    }

    fun bind() {
        glBindBufferBase(GL_UNIFORM_BUFFER, bindingPointNumber, bufferId)
    }

    fun setViewMatrix(matrix: Matrix4f) {
        mappedBuffer.putMatrix4f(viewMatrix.second, matrix)
    }

    fun setProjMatrix(matrix: Matrix4f) {
        mappedBuffer.putMatrix4f(projMatrix.second, matrix)
    }

    fun setOrthoMatrix(matrix: Matrix4f) {
        mappedBuffer.putMatrix4f(orthoMatrix.second, matrix)
    }

    fun setModelMatrix(matrix: Matrix4f) {
        mappedBuffer.putMatrix4f(modelMatrix.second, matrix)
    }

    fun setAmbientLight(vector: Vector3f) {
        mappedBuffer.putVector3f(ambientLight.second, vector)
    }

    fun setSkyColor(vector: Vector3f) {
        mappedBuffer.putVector3f(skyColor.second, vector)
    }

    fun setScreenResolution(vector: Vector2f) {
        mappedBuffer.putVector2f(screenResolution.second, vector)
    }

    fun setTime(value: Float) {
        mappedBuffer.putFloat(time.second, value)
    }

    fun setPlayerPosition(vector: Vector3f) {
        mappedBuffer.putVector3f(playerPosition.second, vector)
    }

    fun setChunkDistanceInitFade(value: Float) {
        mappedBuffer.putFloat(chunkDistanceInitFade.second, value)
    }

    fun setChunkDistanceEndFade(value: Float) {
        mappedBuffer.putFloat(chunkDistanceEndFade.second, value)
    }
}