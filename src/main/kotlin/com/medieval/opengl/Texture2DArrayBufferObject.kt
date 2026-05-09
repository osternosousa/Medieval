package com.medieval.opengl

import com.medieval.utility.UtilityM
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL12
import org.lwjgl.opengl.GL13C
import org.lwjgl.opengl.GL30
import org.lwjgl.opengl.GL42.glTexStorage3D
import java.awt.image.BufferedImage

class Texture2DArrayBufferObject {

    private val IMAGE_INTERNAL_FORMAT = GL11.GL_RGBA8

    private var textureUnit = 0
    private var uniformID = 0

    private var TBO_ID = -1

    fun init(
        imageFiles: List<String>,
        imageWidth: Int,
        imageHeight: Int,
        textureUnit: Int,
        uniformID: Int,
        minFilter: Int,
        magFilter: Int,
        isCreatingMipmap: Boolean
    ) {

        this.textureUnit = textureUnit
        this.uniformID = uniformID

        TBO_ID = GL11.glGenTextures()
        val totalDepth = imageFiles.size

        activateTextureUnit()
        bind()

        glTexStorage3D(
            GL30.GL_TEXTURE_2D_ARRAY,
            1,
            IMAGE_INTERNAL_FORMAT,
            imageWidth,
            imageHeight,
            totalDepth
        )

        GL11.glTexParameteri(GL30.GL_TEXTURE_2D_ARRAY, GL11.GL_TEXTURE_MIN_FILTER, minFilter)
        GL11.glTexParameteri(GL30.GL_TEXTURE_2D_ARRAY, GL11.GL_TEXTURE_MAG_FILTER, magFilter)

        GL11.glTexParameteri(GL30.GL_TEXTURE_2D_ARRAY, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT)
        GL11.glTexParameteri(GL30.GL_TEXTURE_2D_ARRAY, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT)

        if (isCreatingMipmap) GL30.glGenerateMipmap(GL30.GL_TEXTURE_2D_ARRAY)

        for (imageIndex in imageFiles.indices) {

            //val image: BufferedImage = Utility.getImageFromResourcesFolderFile(imageFiles[imageIndex])
            val image: BufferedImage = UtilityM.getImageFromSystemResourcesFolderFile(imageFiles[imageIndex])
            val pixels: IntArray = UtilityM.getImagePixels(image)

            GL12.glTexSubImage3D(
                GL30.GL_TEXTURE_2D_ARRAY,
                0,
                0,
                0,
                imageIndex,
                imageWidth,
                imageHeight,
                1,
                GL12.GL_BGRA,
                GL11.GL_UNSIGNED_BYTE,
                pixels
            )

            image.flush()
        }

        GL11.glBindTexture(GL30.GL_TEXTURE_2D_ARRAY, 0)
    }

    fun getTextureUnit(): Int {

        return textureUnit
    }

    fun getUniformID(): Int {

        return uniformID
    }

    fun activateTextureUnit() {

        GL13C.glActiveTexture(textureUnit)
    }

    fun bind() {

        GL11.glBindTexture(GL30.GL_TEXTURE_2D_ARRAY, TBO_ID)
    }

    fun unbind() {

        GL11.glBindTexture(GL30.GL_TEXTURE_2D_ARRAY, 0)
    }
}