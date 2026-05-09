package com.medieval.opengl

import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL15
import java.nio.FloatBuffer
import java.nio.IntBuffer

class VertexBufferObject {

    var VBO_ID = -1
        private set

    fun init() {

        VBO_ID = GL15.glGenBuffers()
    }

    fun bind() {

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBO_ID)
    }

    fun unbind() {

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0)
    }

    fun bufferData(data: FloatArray) {

        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, data, GL15.GL_DYNAMIC_DRAW)
    }

    fun bufferData(data: FloatBuffer) {

        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, data, GL15.GL_DYNAMIC_DRAW)
    }

    fun bufferData(data: IntArray) {

        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, data, GL15.GL_DYNAMIC_DRAW)
    }

    fun bufferData(data: IntBuffer) {

        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, data, GL15.GL_DYNAMIC_DRAW)
    }

    fun bufferDataOrphaning(data: FloatArray, drawMethod: Int) {

        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, data.size * 4L, drawMethod)
        GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, data)
    }

    fun bufferDataOrphaning(data: FloatBuffer, drawMethod: Int) {

        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, data.capacity() * 4L, drawMethod)
        GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, data)
    }

    fun destroy() {

        GL15.glDeleteBuffers(VBO_ID)
    }

    fun drawTriangles(first: Int, count: Int) {

        GL11.glDrawArrays(GL11.GL_TRIANGLES, first, count)
    }
}