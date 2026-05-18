package com.medieval.managers

import com.medieval.components.Chunk
import com.medieval.foundation.Entity
import com.medieval.utility.MathM.DEGREES_TO_RADIANS
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW.glfwGetTime
import kotlin.math.cos
import kotlin.math.sin

class ChunkManager(
    val gm: GameManager,
) : Entity() {

    companion object {

        private const val TEXT_LOAD_CHUNKS_INIT: String = "LOAD_CHUNKS_INIT: "
        private const val TEXT_LOAD_CHUNKS_END: String = "LOAD_CHUNKS_END: "
        private const val TEXT_ARROW: String = " -> "
        private const val TEXT_CHUNK_KEYS: String = " chunk keys."
    }

    private val mapOfChunks: HashMap<String, Chunk> = HashMap(1500)
    private val chunksCreated: MutableList<String> = mutableListOf()

    private val chunkPool: ArrayDeque<Chunk> = ArrayDeque()

    private var isLoadingChunks: Boolean = false
    private var isCreatingTerrain: Boolean = false
    private var hasToLoadChunks: Boolean = true

    val originPosition: Vector3f = Vector3f()
    val walkedPosition: Vector3f = Vector3f()

    private var loadChunksJob: Job? = null
    private var updateTerrainJob: Job? = null


    fun initManager() {


    }

    override fun update(dt: Float) {

        walkedPosition.x = gm.playerManager.eyePosition.x - originPosition.x
        walkedPosition.z = gm.playerManager.eyePosition.z - originPosition.z

        val distance = walkedPosition.length()

        if (hasToLoadChunks && !isLoadingChunks) {

            isLoadingChunks = true

            loadChunksJob?.cancel()
            loadChunksJob = CoroutineScope(Dispatchers.Default).launch {

                loadChunks()
            }
        }

        if (distance > 32 && !isCreatingTerrain && !isLoadingChunks && !hasToLoadChunks && !gm.terrainManager.isUpdatingBlockMap) {

            isCreatingTerrain = true

            updateTerrainJob?.cancel()
            updateTerrainJob = CoroutineScope(Dispatchers.Default).launch {

                updateBlockMap()
            }
        }
    }

    private fun updateBlockMap() {

        // Zerando a posição original de medição do deslocamento.
        originPosition.x = gm.playerManager.eyePosition.x
        originPosition.z = gm.playerManager.eyePosition.z

        // Posição top left da coordenada do primeiro elemento do terreno do block map
        // de tamanho 1024x1024, com player no centro e distante 512 blocos das extremidades.
        val blockMapOriginX = originPosition.x.toInt() - 512
        val blockMapOriginZ =  originPosition.z.toInt() - 512

        // Criando o block map.
        gm.terrainManager.updateBlockMap(posX = blockMapOriginX, posZ = blockMapOriginZ)

        hasToLoadChunks = true
        isCreatingTerrain = false
    }

    fun loadChunks() {

        chunksCreated.clear()

        var message = TEXT_LOAD_CHUNKS_INIT + glfwGetTime()
        gm.logMessage(message = message)

        var angle: Float = 0f
        var radius: Float = 1f
        val maxViewSight: Float = 16f * Chunk.CHUNK_WIDTH
        val maxRadius: Int = (maxViewSight / Chunk.CHUNK_WIDTH).toInt()

        val mapOriginX = gm.terrainManager.getBlockMapOriginX()
        val mapOriginZ = gm.terrainManager.getBlockMapOriginZ()

        var x = 0
        var z = 0

        while (radius <= maxRadius) {

            while (angle <= 180) {

                val initX: Int = (cos(angle * DEGREES_TO_RADIANS) * radius).toInt()
                val initZ: Int = (sin(angle * DEGREES_TO_RADIANS) * radius).toInt()

                // Terrain.originX + 512 posiciona no centro do block map, pois a varição
                // do grau (angle) faz xz circular em torno da origem.
                // -> initX * Chunk.CHUNK_WIDTH constrói as coordenadas dos chunks de forma
                //      genérica a partir de um centro [0, 0], indo das coordenadas -256 a
                //      +256 em xz.
                // -> TerrainManager.getBlockMapOriginX() + 512 desloca a posição inicial
                //      dos chunks exatamente para a origem do chunk em que o player estava
                //      localizado no momento da criação do blockMap com as informações de
                //      originPosition.xz, combinando também a origem do array de blocos.
                x = initX * Chunk.CHUNK_WIDTH + mapOriginX + 512
                z = initZ * Chunk.CHUNK_LENGTH + mapOriginZ + 512

                createChunk(initX = x, initY = 0, initZ = z)

                z = -(initZ) * Chunk.CHUNK_LENGTH + mapOriginZ + 512

                createChunk(initX = x, initY = 0, initZ = z)

                angle += 1.0f
            }

            angle = 0f
            radius += 1.0f
        }

        isLoadingChunks = false
        hasToLoadChunks = false

        message = TEXT_LOAD_CHUNKS_END + glfwGetTime() + TEXT_ARROW + getChunksQtdInMapOfChunks() + TEXT_CHUNK_KEYS

        gm.logMessage(message = message)
    }

    // A informação que chega aqui de xyz é arbitrária. É necessário ajustar para origem do chunk.
    fun createChunk(initX: Int, initY: Int, initZ: Int) {

        val chunkInitX = getInitChunkKeyByWorldPositionX(x = initX.toFloat())
        val chunkInitY = getInitChunkKeyByWorldPositionY(y = initY.toFloat())
        val chunkInitZ = getInitChunkKeyByWorldPositionZ(z = initZ.toFloat())

        val chunkKey = getChunkKeyByOriginValues(initX = chunkInitX, initY = chunkInitY, initZ = chunkInitZ)

        val resultA = mapOfChunksCreatedContains(chunkKey = chunkKey)
        val resultB = mapOfChunksContainsKey(chunkKey = chunkKey)

        //gm.logMessage(message = chunkKey)

        if (resultA || resultB) return

        // Get new chunk from the pool.
        val chunk = acquireChunk()
//        val chunk = Chunk(
//            gm = gm,
//            initX = chunkInitX,
//            initY = chunkInitY,
//            initZ = chunkInitZ,
//        )

        // Está ocorrendo alguma falta de sincronia entre a criação e sleeping do chunk.
        // Aparentemente aqueles chunks que estão bem no limite das bordas da área de criação podem
        // estar sendo criados e destruídos em seguida e solicitada a criação logo em seguida e
        // não está sendo criado. Talvez porque a key ainda nestá no mapa, mas ele já está agendado
        // para sleep.
        chunk.initX = chunkInitX
        chunk.initY = chunkInitY
        chunk.initZ = chunkInitZ

        chunk.awake()

        addChunkCreated(chunkKey)
        addChunkOnMapOfChunks(chunkKey = chunkKey, chunk = chunk)

        chunk.createUpdateMesh()
        gm.rendererManager.subscribeEntityInit(entity = chunk)
    }

    fun getInitChunkKeyByWorldPositionX(x: Float): Int {

        var posX: Float = x

        posX = posX.toInt().toFloat()

        posX = (x - if (x < 0) Chunk.CHUNK_WIDTH - 1 else 0) / Chunk.CHUNK_WIDTH

        return posX.toInt() * Chunk.CHUNK_WIDTH
    }

    fun getInitChunkKeyByWorldPositionY(y: Float): Int {

        // Y is never negative.
        val posY: Float = y / Chunk.CHUNK_HEIGHT
        return posY.toInt() * Chunk.CHUNK_HEIGHT
    }

    fun getInitChunkKeyByWorldPositionZ(z: Float): Int {

        var posZ: Float = z

        posZ = posZ.toInt().toFloat()

        posZ = (z - if (z < 0) Chunk.CHUNK_LENGTH - 1 else 0) / Chunk.CHUNK_LENGTH

        return posZ.toInt() * Chunk.CHUNK_LENGTH
    }

    /** Return the chunk key created with the initial xyz values from the chunk component,
    which match the chunk key values, as those are the same as chunk init xyz values.  */
    fun getChunkKey(chunk: Chunk): String {
        return "ROW:${chunk.initZ}-COL:${chunk.initX}-TOP:${chunk.initY}"
    }

    fun getChunkKeyByOriginValues(initX: Int, initY: Int, initZ: Int): String {

        return "ROW:${initZ}-COL:${initX}-TOP:${initY}"
    }

    fun getChunksQtdInMapOfChunks(): Int {
        synchronized(mapOfChunks) {
            return mapOfChunks.size
        }
    }

    fun removeChunkFromMapOfChunks(chunk: Chunk) {
        synchronized(mapOfChunks) {
            //val key = getChunkKey(chunk = chunk)
            mapOfChunks.remove(key = chunk.chunkKey)
        }
    }

    fun addChunkOnMapOfChunks(chunkKey: String, chunk: Chunk) {
        synchronized(mapOfChunks) {
            mapOfChunks.put(key = chunkKey, value = chunk)
        }
    }

    private fun addChunkCreated(chunkKey: String) {
        synchronized(chunksCreated) {
            chunksCreated.add(chunkKey)
        }
    }

    private fun mapOfChunksContainsKey(chunkKey: String): Boolean {
        synchronized(mapOfChunks) {
            return mapOfChunks.containsKey(chunkKey)
        }
    }

    private fun mapOfChunksCreatedContains(chunkKey: String): Boolean {
        synchronized(chunksCreated) {
            return chunksCreated.contains(chunkKey)
        }
    }

    fun getChunkByChunkKey(chunkKey: String): Chunk? {
        synchronized(mapOfChunks) {
            return mapOfChunks[chunkKey]
        }
    }

    fun acquireChunk(): Chunk {
        synchronized(chunkPool) {
            if (chunkPool.isEmpty()) {
                val chunk = Chunk(gm = gm) // create new
                return chunk
            }

            val chunk = chunkPool.removeLast()
            return chunk
        }
    }

    fun releaseChunk(chunk: Chunk) {
        synchronized(chunkPool) {
            if (chunkPool.size < 1500) {
                //chunk.sleep()
                chunkPool.addLast(chunk)
            } else {
                // discard or let GC handle it.
                chunk.scheduleEntityTermination()
            }
        }
    }
}