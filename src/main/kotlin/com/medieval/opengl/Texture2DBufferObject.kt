package com.medieval.opengl

import com.medieval.utility.UtilityM
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL12
import org.lwjgl.opengl.GL13
import org.lwjgl.opengl.GL30
import java.awt.image.BufferedImage

class Texture2DBufferObject {

    companion object {

        private var CURRENT_ACTIVE_TEXTURE_2D: Int = -1
    }

    private val IMAGE_INTERNAL_FORMAT = GL11.GL_RGBA8

    private lateinit var imageFile: BufferedImage
    private var isGeneratingMipMap = false
    private var textureUnit = 0

    var TBO_ID = -1
        private set

    fun init(
        resourcesImageFilePath: String,
        isGeneratingMipmap: Boolean,
        textureUnit: Int
    ) {

        this.imageFile = UtilityM.getImageFromSystemResourcesFolderFile(resourcesImageFilePath)
        this.isGeneratingMipMap = isGeneratingMipmap
        this.textureUnit = textureUnit

        init()
    }

    fun init(
        imageFile: BufferedImage,
        isGeneratingMipmap: Boolean,
        textureUnit: Int
    ) {

        this.imageFile = imageFile
        this.isGeneratingMipMap = isGeneratingMipmap
        this.textureUnit = textureUnit

        init()
    }

    private fun init() {

        TBO_ID = GL11.glGenTextures()

        val pixels: IntArray = UtilityM.getImagePixels(imageFile)

        TBO_ID = GL11.glGenTextures()

        GL13.glActiveTexture(textureUnit)
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, TBO_ID)

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST)

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT)

        GL11.glTexImage2D(
            GL11.GL_TEXTURE_2D,
            0,
            IMAGE_INTERNAL_FORMAT,
            imageFile.width,
            imageFile.height,
            0,
            GL12.GL_BGRA,
            GL11.GL_UNSIGNED_BYTE,
            pixels
        )

        if (isGeneratingMipMap) GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D)

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0)

        imageFile.flush()
    }

    fun bind() {

        if (CURRENT_ACTIVE_TEXTURE_2D != TBO_ID) {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, TBO_ID)
            CURRENT_ACTIVE_TEXTURE_2D = TBO_ID
        }
    }

    fun activateTextureUnit() {

        GL13.glActiveTexture(textureUnit)
    }

    fun unbind() {

        if (CURRENT_ACTIVE_TEXTURE_2D != -1) {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0)
        }
    }

    fun destroy() {

        GL11.glDeleteTextures(TBO_ID)
    }
}