package com.medieval.opengl

import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30

class VertexArrayObject {

    private companion object {

        private var CURRENT_ACTIVE_VERTEX_ARRAY: Int = -1
    }

    var VAO_ID = -1
        private set

    fun init() {

        VAO_ID = GL30.glGenVertexArrays()
    }

    /**
     * indices components -> how many components each attribute has.
     *
     *
     * type -> GL_SHORT, GL_UNSIGNED_SHORT, GL_INT, GL_UNSIGNED_INT, GL_FLOAT.  */
    fun enableAndSetAllVertexAttributes(indicesComponents: IntArray, type: Int) {

        val size: Int = when (type) {
            GL11.GL_BYTE, GL11.GL_UNSIGNED_BYTE -> 1
            GL11.GL_SHORT, GL11.GL_UNSIGNED_SHORT -> 2
            GL11.GL_INT, GL11.GL_UNSIGNED_INT, GL11.GL_FLOAT -> 4
            else -> 1
        }

        var stride = 0

        for (index in indicesComponents.indices) {
            stride += indicesComponents[index]
        }

        stride *= size

        var pointer = 0L

        for (index in indicesComponents.indices) {

            GL20.glEnableVertexAttribArray(index)
            GL20.glVertexAttribPointer(index, indicesComponents[index], type, false, stride, pointer)
            pointer += indicesComponents[index].toLong() * size
        }
    }

    /** type -> GL_SHORT, GL_UNSIGNED_SHORT, GL_INT, GL_UNSIGNED_INT, GL_FLOAT.  */
    fun enableAndSetOneVertexAttribute(index: Int, indexComponentsQtd: Int, type: Int, stride: Int, pointer: Long) {

        GL20.glEnableVertexAttribArray(index)
        GL20.glVertexAttribPointer(index, indexComponentsQtd, type, false, stride, pointer)
    }

    fun enableOneVertexAttribute(index: Int) {

        GL20.glEnableVertexAttribArray(index)
    }

    /** type -> GL_SHORT, GL_UNSIGNED_SHORT, GL_INT, GL_UNSIGNED_INT, GL_FLOAT.  */
    fun setOneVertexAttribute(index: Int, indexComponentsQtd: Int, type: Int, stride: Int, pointer: Long) {

        GL20.glVertexAttribPointer(index, indexComponentsQtd, type, false, stride, pointer)
    }

    fun bind() {

        if (CURRENT_ACTIVE_VERTEX_ARRAY != VAO_ID) {
            GL30.glBindVertexArray(VAO_ID)
            CURRENT_ACTIVE_VERTEX_ARRAY = VAO_ID
        }
    }

    fun unbind() {

        if (CURRENT_ACTIVE_VERTEX_ARRAY != -1) {
            GL30.glBindVertexArray(0)
            CURRENT_ACTIVE_VERTEX_ARRAY = -1
        }
    }

    fun destroy() {

        GL30.glDeleteVertexArrays(VAO_ID)
    }
}