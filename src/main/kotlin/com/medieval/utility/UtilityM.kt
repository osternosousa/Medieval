package com.medieval.utility

import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import java.util.Scanner
import javax.imageio.ImageIO

object UtilityM {

    /** Returns a size 2 String array from a unified shader program, where element 0 is the
     * vertex shader and element 1 is the fragment shader. They are separated in the text
     * file by the mark //FRAGMENT_SHADER.  */
    fun getShadersFromTextFile(path: String): Array<String> {

        val path = ClassLoader.getSystemResource(path).path// .toString().drop(6)

        val file = File(path)

        val vs = StringBuilder()
        val fs = StringBuilder()

        var currentShader = "//VERTEX_SHADER"

        try {

            Scanner(file).use { sc ->
                while (sc.hasNextLine()) {
                    val line = sc.nextLine()

                    if (line.startsWith("//FRAGMENT_SHADER")) currentShader = "//FRAGMENT_SHADER"

                    if (currentShader == "//VERTEX_SHADER") {
                        vs.append(line).append("\n")
                    } else {
                        fs.append(line).append("\n")
                    }
                }
            }
        } catch (e: Exception) {

            throw RuntimeException("Failed to load shader file: $path", e)
        }

        return arrayOf<String>(vs.toString(), fs.toString())
    }

    /** Returns an image from a complete file path beginning from driver letter (Ex.: C:/).  */
    fun getImageFromCompleteFilePath(imageFilePath: String): BufferedImage {

        val file = File(imageFilePath)

        try {
            return ImageIO.read(file)
        } catch (e: IOException) {
            throw RuntimeException("Failed to load image. File path: $imageFilePath. ", e)
        }
    }

    /** File path must be informed after the resources folder name. This version is used
     * when manually creating the project folders structure, when resources folder is sided
     * with the src folder. */
    fun getImageFromResourcesFolderFile(imageFilePath: String): BufferedImage {

        return getImageFromCompleteFilePath("./resources/$imageFilePath")
    }

    /** This version is used when Intellij created the project folders structure, making
     * available the resources folder in the project structure. */
    fun getImageFromSystemResourcesFolderFile(imageFilePath: String): BufferedImage {

        val path = ClassLoader.getSystemResource(imageFilePath).path

        return getImageFromCompleteFilePath(path)
    }

    fun getImagePixels(image: BufferedImage): IntArray {

        val pixels = IntArray(image.width * image.height)

        //image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());
        for (y in 0..<image.height) {
            for (x in 0..<image.width) {
                pixels[y * image.width + x] = image.getRGB(x, y)
            }
        }

        return pixels
    }
}