package com.medieval.opengl

import org.lwjgl.opengl.ARBUniformBufferObject.GL_UNIFORM_BUFFER
import org.lwjgl.opengl.GL15.glBindBuffer
import org.lwjgl.opengl.GL15.glGenBuffers
import org.lwjgl.opengl.GL30.GL_MAP_WRITE_BIT
import org.lwjgl.opengl.GL30.glBindBufferBase
import org.lwjgl.opengl.GL30.glMapBufferRange
import org.lwjgl.opengl.GL44.GL_MAP_COHERENT_BIT
import org.lwjgl.opengl.GL44.GL_MAP_PERSISTENT_BIT
import org.lwjgl.opengl.GL44.glBufferStorage
import java.nio.ByteBuffer

class UniformBufferObject(
    val bindingPointNumber: Int,
    val bufferSize: Long
) {

    var UBO_ID = -1
        private set

    lateinit var BUFFER: ByteBuffer
        private set

    fun init() {

        UBO_ID = glGenBuffers()

        glBindBuffer(
            /* target = */ GL_UNIFORM_BUFFER,
            /* buffer = */ UBO_ID
        )

        // Allocate with persistent mapping
        glBufferStorage(
            /* target = */ GL_UNIFORM_BUFFER,
            /* size = */ bufferSize,
            /* flags = */ GL_MAP_WRITE_BIT or GL_MAP_PERSISTENT_BIT or GL_MAP_COHERENT_BIT
        )

        // Bind buffer to binding point.
        glBindBufferBase(
            /* target = */ GL_UNIFORM_BUFFER,
            /* index = */ bindingPointNumber,
            /* buffer = */ UBO_ID
        )

        // Map once
        val buffer = glMapBufferRange(
            /* target = */ GL_UNIFORM_BUFFER,
            /* offset = */ 0,
            /* length = */ bufferSize,
            /* access = */ GL_MAP_WRITE_BIT or GL_MAP_PERSISTENT_BIT or GL_MAP_COHERENT_BIT
        )

        if (buffer != null) {

            BUFFER = buffer
        } else {

            throw RuntimeException("Buffer is null")
        }

        //val buffer = MemoryUtil.memByteBuffer(ptr, bufferSize.toInt()).asFloatBuffer()

        //glBindBuffer(GL_UNIFORM_BUFFER, 0)

        //if (GameManager.isDebugModeOn) checkError(location = "OPENGLM_CALL")
    }
}