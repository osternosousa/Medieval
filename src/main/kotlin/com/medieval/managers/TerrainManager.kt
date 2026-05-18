package com.medieval.managers

import com.medieval.foundation.Entity
import com.medieval.foundation.Vec2M
import com.medieval.utility.MathM.mix
import com.medieval.utility.decimalFormat
import org.lwjgl.glfw.GLFW.glfwGetTime
import kotlin.math.sin

class TerrainManager(
    val gm: GameManager,
) : Entity() {

    companion object {

        const val MAX_TERRAIN_HEIGHT: Int = 256

        const val BLOCK_MAP_WIDTH: Int = 1024
        const val BLOCK_MAP_LENGTH: Int = 1024
        const val BLOCK_MAP_HEIGHT: Int = MAX_TERRAIN_HEIGHT
    }

    private val seedMultiplier = 43758.5453f

    // 🔧 Seed constants
    val seedVector = Vec2M(12.9898f, 78.233f)

    private var blockMap0: Array<Array<ShortArray>> = Array(BLOCK_MAP_LENGTH) {
        Array(BLOCK_MAP_WIDTH) {
            ShortArray(BLOCK_MAP_HEIGHT) { 5 }
        }
    }

    private var blockMap1: Array<Array<ShortArray>> = Array(BLOCK_MAP_LENGTH) {
        Array(BLOCK_MAP_WIDTH) {
            ShortArray(BLOCK_MAP_HEIGHT) { 5 }
        }
    }

    private var usedBlockMap: Int = 0
    private var freeBlockMap: Int = 1

    private val LOCK_GET_SWAP_BLOCK_MAPS: Any = Any()
    private var blockMaps: Array<Array<Array<ShortArray>>> = arrayOf(blockMap0, blockMap1)

    private var tempBlockMapOriginX: Int = 0
    private var tempBlockMapOriginZ: Int = 0

    private var blockMapOriginX: Int = 0
    private var blockMapOriginZ: Int = 0

    private var hasToSwapBlockMap: Boolean = false

    var isUpdatingBlockMap: Boolean = false
        private set

    fun scheduleBlockMapSwap() {

        gm.logMessage(message = "TERRAIN_MANAGER: block map swap scheduled")

        synchronized(LOCK_GET_SWAP_BLOCK_MAPS) {

            hasToSwapBlockMap = true
        }
    }

    /** The calling of swapBlockMaps() after the creation of the map by the calling
     * updateBlockMap() makes the last created map the current one. */
    fun swapBlockMaps() {

        synchronized(LOCK_GET_SWAP_BLOCK_MAPS) {

            gm.logMessage(message = "TERRAIN_MANAGER: swapping block maps -> USED/FREE: $usedBlockMap, $freeBlockMap")

            // SWAP BLOCK MAPS ORDER.
            val tempState = usedBlockMap
            usedBlockMap = freeBlockMap
            freeBlockMap = tempState

            // SET MAP ORIGIN
            blockMapOriginX = tempBlockMapOriginX
            blockMapOriginZ = tempBlockMapOriginZ

            // Indicates that the first map was created and is available for consulting.
            //isMapAvailable = true

            gm.logMessage(message = "TERRAIN_MANAGER: swapping block maps -> USED/FREE: $usedBlockMap, $freeBlockMap")

            hasToSwapBlockMap = false
        }
    }

    override fun initStates() {

        if (hasToSwapBlockMap) {

            swapBlockMaps()
        }
    }

    /** Retorna a ID do bloco na posção arbitrária completa de mundo xyz.
    Objetos que fazem consultas utilizando esta chamada devem considerar as
    coordenadas informadas quando estas foram dinâmicas, por exemplo, quando
    são posições de um objeto em movimento em relação a Player, pois ao se
    distanciar de player, o objeto igualmente se distancia dos valores de
    origem xy atuais do block map. Logo, caso continuem fazendo consultas
    quando player está se distanciando, em algum momento as coordenas xy do
    objeto em movimento, que estão em valores absolutos do mundo, resultarão
    em valores foram do array do block map no cálculo xz - blockMapOriginXZ. */
    fun getBlockId(x: Int, y: Int, z: Int): Int {

        if (y > BLOCK_MAP_HEIGHT - 1) return 0

        synchronized(LOCK_GET_SWAP_BLOCK_MAPS) {

            val blockPosX: Int = x - blockMapOriginX
            val blockPosZ: Int = z - blockMapOriginZ

            //if (x !in 0..1023 || z !in 0..1023) return 5

            return blockMaps[usedBlockMap][blockPosZ][blockPosX][y].toInt()
        }
    }

    fun getBlockMapOriginX(): Int {
        return synchronized(LOCK_GET_SWAP_BLOCK_MAPS) {
            blockMapOriginX
        }
    }

    fun getBlockMapOriginZ(): Int {
        return synchronized(LOCK_GET_SWAP_BLOCK_MAPS) {
            blockMapOriginZ
        }
    }

    fun initManager() {

    }

    fun updateBlockMap(posX: Int, posZ: Int) {

        isUpdatingBlockMap = true

        // Captura o blockMap não utilizado no momento (usedBlockMap X freeBlockMap).
        val blockMap: Array<Array<ShortArray>> = blockMaps[freeBlockMap]

        // Preenche todos os elementos com 5 (ar).
        for (z in 0 until BLOCK_MAP_LENGTH) {
            for (x in 0 until BLOCK_MAP_WIDTH) {
                blockMap[z][x].fill(element = 5)
            }
        }

        val timeNoiseMapInit = glfwGetTime()
        val noiseMapTerrain: Array<FloatArray> = getNoiseMap(posX = posX, posZ = posZ, frequency = 1f)
        val timeNoiseMapEnd = glfwGetTime()

        val noiseMapErosion: Array<FloatArray> = getNoiseMap(posX = posX + 1024, posZ = posZ + 1024, frequency = 0.5f)

        gm.logMessage(message = "NOISE_MAP_TIME: ${(timeNoiseMapEnd - timeNoiseMapInit).decimalFormat()}")

        val noiseMapVegetation = getNoiseMap(posX = posX, posZ = posZ, frequency = 15f)

        for (z in 0..< BLOCK_MAP_LENGTH) {
            for (x in 0..< BLOCK_MAP_WIDTH) {

                val erosion: Float = noiseMapErosion[z][x]
                val maxHeight: Int = (noiseMapTerrain[z][x] * (BLOCK_MAP_HEIGHT - 1)).toInt()
                //val maxHeight: Int = (noiseMapTerrain[z][x] * erosion * (BLOCK_MAP_HEIGHT - 1)).toInt()
                var block: Short = 5

                for (y in 0.. maxHeight) {

                    // Abaixo desta altura ficam blocos de dirt.
                    val dirtyHeight: Int = (maxHeight * 0.9f).toInt()

                    if (maxHeight < 100 && y > maxHeight) {
                        block = 4
                    } else if (y < dirtyHeight) {
                        block = 2   // Rock block.
                    } else if (y in dirtyHeight..< maxHeight ){
                        block = 0   // Dirt block.
                    } else {
                        block = 1   // Grass block.
                    }

                    blockMap[z][x][y] = block
                }

                if (maxHeight < 100) {
                    blockMap[z][x][100] = 4
                    blockMap[z][x][maxHeight] = 3
                }

                includeVegetation(blockMap = blockMap, noiseMapVegetation = noiseMapVegetation, x = x, y = maxHeight, z = z)
            }
        }

        // Neste ponto, o block map incólume está gerado. Aqui, vamos levantar todos os
        // chunks considerados e a partir das chaves dos chunks, vamos verificar se existem arquivos
        // JSON referente a cada chunk, pois os arquivos JSON armazenam as alterações no terreno
        // de blocos incluídos ou removidos.

        // Load and apply block entries.
        applyBlockEntries(blockMap = blockMap, blockMapOriginX = posX, blockMapOriginZ = posZ)

        val blockMapEnd = glfwGetTime()

        gm.logMessage(message = "BLOCK_MAP_TIME: ${(blockMapEnd - timeNoiseMapEnd).decimalFormat()}")

        tempBlockMapOriginX = posX
        tempBlockMapOriginZ = posZ

        isUpdatingBlockMap = false

        scheduleBlockMapSwap()
    }

    fun applyBlockEntries(blockMap: Array<Array<ShortArray>>, blockMapOriginX: Int, blockMapOriginZ: Int) {


    }

    fun getNoiseMap(posX: Int, posZ: Int, frequency: Float = 1f) : Array<FloatArray> {

        val noiseMap: Array<FloatArray> = Array(BLOCK_MAP_LENGTH) { FloatArray(BLOCK_MAP_WIDTH) }

        val p = Vec2M(0f, 0f)

        for (noiseMapOffsetRow in 0 until 2) {
            for (noiseMapOffsetCol in 0 until 2) {

                for (row in 0 until 512) {
                    for (col in 0 until 512) {

                        p.x = col / 512f + (posX + noiseMapOffsetCol * 512) / 512f
                        p.y = row / 512f + (posZ + noiseMapOffsetRow * 512) / 512f

                        val n = fbm(p * frequency)

                        noiseMap[noiseMapOffsetRow * 512 + row][noiseMapOffsetCol * 512 + col] = n
                    }
                }
            }
        }

        return noiseMap
    }

    private val lawnEventsA: List<Int> = listOf(0, 16, 32, 48, 64, 80, 96, 112, 128, 144, 160, 176, 192, 208, 224, 240, 256)
    private val lawnEventsB: List<Int> = listOf(2, 18, 34, 50, 66, 82, 98, 114, 130, 146, 162, 178, 194, 210, 226, 242)
    private val flowerEventsA: List<Int> = listOf<Int>(9, 25, 41, 57, 73, 89)
    private val flowerEventsB: List<Int> = listOf<Int>(13, 29, 45, 61)
    private val treeEvents = listOf<Int>(4, 20, 36, 52, 68, 84, 100, 116, 132, 148, 164, 180, 196, 212, 228)

    private val eventsA = listOf<Int>(0, 16, 32, 48, 64, 80, 96, 112, 128, 144, 160, 176, 192, 208, 224, 240, 256)
    private val eventsB = listOf<Int>(2, 18, 34, 50, 66, 82, 98, 114, 130, 146, 162, 178, 194, 210, 226, 242)
    private val eventsC = listOf<Int>(4, 20, 36, 52, 68, 84, 100, 116, 132, 148, 164, 180, 196, 212, 228)
    private val eventsD = listOf<Int>(6, 22, 38, 54, 70, 86, 102, 118, 134, 150, 166, 182, 198, 214)
    private val eventsE = listOf<Int>(8, 24, 40, 56, 72, 88, 104, 120, 136, 152, 168, 184, 200)
    private val eventsF = listOf<Int>(10, 26, 42, 58, 74, 90, 106, 122, 138, 154, 170, 186)
    private val eventsG = listOf<Int>(12, 28, 44, 60, 76, 92, 108, 124, 140, 156, 172)
    private val eventsH = listOf<Int>(14, 30, 46, 62, 78, 94, 110, 126, 142, 158)
    private val eventsI = listOf<Int>(5, 21, 37,53,	69,	85,	101, 117)
    private val eventsJ = listOf<Int>(7, 23, 39, 55, 71, 87, 103)
    private val eventsK = listOf<Int>(9, 25, 41, 57, 73, 89)
    private val eventsL = listOf<Int>(13, 29, 45, 61)
    private val eventsM = listOf<Int>(15, 31, 47)

    private fun includeVegetation(blockMap: Array<Array<ShortArray>>, noiseMapVegetation: Array<FloatArray>, x: Int, y: Int, z: Int) {

        val event: Int = (noiseMapVegetation[z][x] * (256 - 1)).toInt()

        if (y >= 100 && lawnEventsA.contains(element = event)) blockMap[z][x][y + 1] = 15
        if (y >= 100 && lawnEventsB.contains(element = event)) blockMap[z][x][y + 1] = 10
        if (y >= 100 && flowerEventsA.contains(element = event)) blockMap[z][x][y + 1] = 13
        if (y >= 100 && flowerEventsB.contains(element = event)) blockMap[z][x][y + 1] = 14
        if (y >= 100 && treeEvents.contains(element = event)) includeTree(blockMap = blockMap, x = x, y = y, z = z)
    }

    private fun includeTree(blockMap: Array<Array<ShortArray>>, x: Int, y: Int, z: Int) {

        for (top in 0..4) {
            for (row in 0..4) {
                for (col in 0..4) {

                    if ((z - 2 + row) in 0..1023 && (x - 2 + col) in 0..1023) {

                        val value: Short = treeA[row * 5 + col + top * 25]

                        if (blockMap[z - 2 + row][x - 2 + col][y + 8 - top] == 5.toShort()) {

                            blockMap[z - 2 + row][x - 2 + col][y + 8 - top] = value
                        }
                    }
                }
            }
        }

        blockMap[z][x][y + 0] = 7
        blockMap[z][x][y + 1] = 7
        blockMap[z][x][y + 2] = 7
        blockMap[z][x][y + 3] = 7
    }

    private val treeA: ShortArray = shortArrayOf(
        5, 5, 5, 5, 5,
        5, 5, 5, 5, 5,
        5, 5, 8, 5, 5,
        5, 5, 5, 5, 5,
        5, 5, 5, 5, 5,

        5, 5, 5, 5, 5,
        5, 8, 8, 8, 5,
        5, 8, 8, 8, 5,
        5, 8, 8, 8, 5,
        5, 5, 5, 5, 5,

        5, 5, 8, 5, 5,
        5, 8, 8, 8, 5,
        8, 8, 7, 8, 8,
        5, 8, 8, 8, 5,
        5, 5, 8, 5, 5,

        5, 5, 5, 5, 5,
        5, 8, 8, 8, 5,
        5, 8, 7, 8, 5,
        5, 8, 8, 8, 5,
        5, 5, 5, 5, 5,

        5, 5, 5, 5, 5,
        5, 5, 5, 5, 5,
        5, 5, 7, 5, 5,
        5, 5, 5, 5, 5,
        5, 5, 5, 5, 5,
    )

    // 🌀 Hash function
    fun hash(p: Vec2M): Float {

        var value = ((sin(p.dot(seedVector)) * seedMultiplier) % 1.0f)
        if (value < 0) value += 1.0f

//        return ((sin(p.dot(seedVector)) * seedMultiplier) % 1.0f).let {
//            if (it < 0) it + 1.0f else it
//        }

        return value
    }

    // 🧊 Value Noise
    private fun valueNoise(uv: Vec2M): Float {
        val i = uv.floor()
        var f = uv.fract()
        f = Vec2M(f.x * f.x * (3f - 2f * f.x), f.y * f.y * (3f - 2f * f.y)) // smoothstep

        val a = hash(i)
        val b = hash(i + Vec2M(1f, 0f))
        val c = hash(i + Vec2M(0f, 1f))
        val d = hash(i + Vec2M(1f, 1f))

        val ab = mix(a, b, f.x)
        val cd = mix(c, d, f.x)

        return mix(ab, cd, f.y)
    }

    // 🌊 Fractal Brownian Motion
    private fun fbm(uv: Vec2M): Float {
        var value = 0f
        var amplitude = 0.5f
        var p = uv
        repeat(5) {
            value += amplitude * valueNoise(p)
            p *= 2f
            amplitude *= 0.5f
        }

        return value
    }
}