package com.medieval.opengl

import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL15
import java.nio.FloatBuffer
import java.nio.IntBuffer

class IndexBufferObject {

    private companion object var CURRENT_ACTIVE_EBO: Int = -1

    var EBO_ID = -1
        private set
    var elementsQtd = 0
        private set

    fun init() {
        EBO_ID = GL15.glGenBuffers()
    }

    fun bind() {

        if (CURRENT_ACTIVE_EBO != EBO_ID) {
            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, EBO_ID)
            CURRENT_ACTIVE_EBO = EBO_ID
        }
    }

    fun unbind() {

        if (CURRENT_ACTIVE_EBO != -1) {
            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0)
            CURRENT_ACTIVE_EBO = -1
        }
    }

    fun bufferData(data: FloatArray) {

        elementsQtd = data.size

        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, data, GL15.GL_DYNAMIC_DRAW)
    }

    fun bufferData(data: FloatBuffer) {

        elementsQtd = data.capacity()

        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, data, GL15.GL_DYNAMIC_DRAW)
    }

    fun bufferData(data: IntArray) {

        elementsQtd = data.size

        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, data, GL15.GL_DYNAMIC_DRAW)
    }

    fun bufferData(data: IntBuffer) {

        elementsQtd = data.capacity()

        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, data, GL15.GL_DYNAMIC_DRAW)
    }

    fun destroy() {

        GL15.glDeleteBuffers(EBO_ID)
    }

    fun bufferDataOrphaning(data: IntArray, drawMethod: Int) {

        elementsQtd = data.size

        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, data.size * 4L, drawMethod)
        GL15.glBufferSubData(GL15.GL_ELEMENT_ARRAY_BUFFER, 0, data)
    }

    /** Draw all elements from position of index. */
    fun drawElementsTriangles(index: Int) {

        GL11.glDrawElements(GL11.GL_TRIANGLES, elementsQtd, GL11.GL_UNSIGNED_INT, index * 4L)
    }

    /** Draw all elements from the index 0. */
    fun drawElementsTriangles() {

        drawElementsTriangles(0)
    }

    fun drawElementsLines(index: Int) {

        GL11.glDrawElements(GL11.GL_LINES, elementsQtd, GL11.GL_UNSIGNED_INT, index * 4L)
    }

    fun drawElementsLines() {

        drawElementsLines(0)
    }
}