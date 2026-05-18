package com.medieval.foundation

class ColorM(
    r: Int = 0,
    g: Int = 0,
    b: Int = 0,
    a: Int = 255,
) {

    companion object {
        fun BLACK(): ColorM = ColorM(0, 0, 0, 255)
        fun BLUE(): ColorM = ColorM(0, 0, 255, 255)
        fun LIGHT_GRAY(): ColorM = ColorM(192, 192, 192, 255)
        fun MEDIUM_GRAY(): ColorM = ColorM(128, 128, 128, 255)
        fun DARK_GRAY(): ColorM = ColorM(64, 64, 64, 255)
        fun RED(): ColorM = ColorM(255, 0, 0, 255)
        fun GREEN(): ColorM = ColorM(0, 255, 0, 255)
        fun YELLOW(): ColorM = ColorM(255, 255, 0, 255)
        fun WHITE(): ColorM = ColorM(255, 255, 255, 255)
        fun TRANSPARENT(): ColorM = ColorM(0, 0, 0, 0)

        val RED: ColorM
            get() {
                return ColorM(255, 0, 0, 255)
            }
    }

    constructor(r: Float, g: Float, b: Float, a: Float)
            : this(r = (r * 255).toInt(), g = (g * 255).toInt(), b = (b * 255).toInt(), a = (a * 255).toInt())

    private val limitRange: IntRange = 0..255

    var r: Int = r
        set(value) {
            if (value !in limitRange) throwException()
            field = value
            computeValues()
        }
    var g: Int = g
        set(value) {
            if (value !in limitRange) throwException()
            field = value
            computeValues()
        }
    var b: Int = b
        set(value) {
            if (value !in limitRange) throwException()
            field = value
            computeValues()
        }
    var a: Int = a
        set(value) {
            if (value !in limitRange) throwException()
            field = value
            computeValues()
        }

    var red: Float = 0f
        private set
    var green: Float = 0f
        private set
    var blue: Float = 0f
        private set
    var alpha: Float = 0f
        private set

    var rgb: Int = 0
        private set

    var hexRGB: String = ""
        private set

    init {
        computeValues()
    }

    /** Set rgba color components to a value in the range of 0..255. */
    fun setColor(r: Int, g: Int, b: Int, a: Int = 255) {

        if (r !in limitRange || g !in limitRange || b !in limitRange || a !in limitRange) throwException()

        this.r = r
        this.g = g
        this.b = b
        this.a = a

        computeValues()
    }

    /** Set rgb and a color components to a value in the range of 0..255. */
    fun setColor(rgb: Int, a: Int = 255) {

        if (rgb !in limitRange) throwException()

        this.r = rgb
        this.g = rgb
        this.b = rgb
        this.a = a

        computeValues()
    }

    /** Set rgb and a color components to a value in the range of 0..255. */
    fun setIntMaskColor(rgba: Int) {

        //if (rgb !in limitRange) throwException()
        // a            r           g           b
        // 00000000     00000000    00000000    00000000

        this.a = rgba.shr(24) and 0xff
        this.r = rgba.shr(16) and 0xff
        this.g = rgba.shr(8) and 0xff
        this.b = rgba.shr(0) and 0xff

        computeValues()
    }

    private fun computeValues() {
        red = r / 255f
        green = g / 255f
        blue = b / 255f
        alpha = a / 255f

        rgb = a.shl(24) + r.shl(16) + g.shl(8) + b
        hexRGB = rgb.toHexString().uppercase()
    }

    private fun throwException() {
        throw IllegalArgumentException("value(s) out of range, cannot be set to a color")
    }
}