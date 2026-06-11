package com.medieval.components

import com.medieval.foundation.BlockFactory
import com.medieval.foundation.Entity
import com.medieval.managers.BlockManager
import com.medieval.managers.GameManager
import com.medieval.managers.TerrainManager
import com.medieval.opengl.IndexBufferObject
import com.medieval.opengl.ProgramShaderObject
import com.medieval.opengl.VertexArrayObject
import com.medieval.opengl.VertexBufferObject
import com.medieval.utility.UtilityM
import org.joml.Vector3f
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.GL_BLEND
import org.lwjgl.opengl.GL11.glDisable
import org.lwjgl.opengl.GL11.glEnable
import java.nio.FloatBuffer
import java.nio.IntBuffer

class Chunk(
    val gm: GameManager,
    var initX: Int = 0,
    var initZ: Int = 0,
    var initY: Int = 0,
) : Entity() {

    companion object {

        var chunkCount: Int = 0
        var opaqueCount: Int = 0
        var transparentCount: Int = 0

        const val CHUNK_WIDTH: Int = 16
        const val CHUNK_HEIGHT: Int = TerrainManager.MAX_TERRAIN_HEIGHT
        const val CHUNK_LENGTH: Int = 16

        var CHUNK_DISTANCE_INIT_FADE: Float = 64f
        var CHUNK_DISTANCE_END_FADE: Float = 128f

        var CHUNK_DISTANCE_OUT_OF_SIGHT: Float = 208f
        var CHUNK_DISTANCE_INIT_TERMINATION: Float = 256f

        val EMPTY_FLOAT_BUFFER: FloatBuffer = BufferUtils.createFloatBuffer(0)
        val EMPTY_INT_BUFFER: IntBuffer = BufferUtils.createIntBuffer(0)

        private var shaderSourceFilePath: String = "programs/chunk.glsl"
        private var PID_CHUNK: ProgramShaderObject = ProgramShaderObject()
        private val ATTRIBUTES_INDICES_COMPONENTS: IntArray = intArrayOf(3, 3, 2, 1)
        private const val ATTRIBUTES_TYPE: Int = GL11.GL_FLOAT
    }

    // Must be set on when xyz chunck position is set. And must
    // be made clean when chunk is disposed or put to sleep.
    var chunkKey: String = ""

    private var VAO_0_OPAQUE: VertexArrayObject = VertexArrayObject()
    private var VBO_0_OPAQUE: VertexBufferObject = VertexBufferObject()
    private var EBO_0_OPAQUE: IndexBufferObject = IndexBufferObject()
    private var VAO_0_TRANSPARENT: VertexArrayObject = VertexArrayObject()
    private var VBO_0_TRANSPARENT: VertexBufferObject = VertexBufferObject()
    private var EBO_0_TRANSPARENT: IndexBufferObject = IndexBufferObject()

    private var verticesOpaque: FloatBuffer = EMPTY_FLOAT_BUFFER
    private var indicesOpaque: IntBuffer = EMPTY_INT_BUFFER
    var verticesOpaqueQTD = 0
    var indicesOpaqueQTD = 0

    private var verticesTransparent: FloatBuffer = EMPTY_FLOAT_BUFFER
    private var indicesTransparent: IntBuffer = EMPTY_INT_BUFFER
    var verticesTransparentQTD = 0
    var indicesTransparentQTD = 0

    private var hasToUpdateMesh: Boolean = false

    private val chunkPosition: Vector3f = Vector3f(
        /* x = */ initX + CHUNK_WIDTH / 2f,
        ///* y = */ initY + CHUNK_HEIGHT / 2f,
        /* y = */ 0f,
        /* z = */ initZ + CHUNK_LENGTH / 2f
    )

    private val chunkDistanceFromPlayer: Vector3f = Vector3f()

    // Vetores auxiliares para o cálculodo cosseno entre os vetores chunkPosition, playerPosition e playerTarget.
    private val vecDirection0: Vector3f = Vector3f()
    private val vecDirection1: Vector3f = Vector3f()

    override fun update(dt: Float) {

        if (isTerminationInitiated || isSleeping || !isAvailableToDraw) return

        if (hasToUpdateMesh) {
            hasToUpdateMesh = false
            updateBuffersData()
        }

        // CÁLCULOS DE EXIBIÇÃO DO CHUNK CONFORME DIREÇÃO DE TARGET DO PLAYER.
        // =============================================================================================
        //chunkDistanceFromPlayer.x = chunkPosition.x - gm.playerManager.eyePosition.x
        //chunkDistanceFromPlayer.z = chunkPosition.z - gm.playerManager.eyePosition.z
        chunkDistanceFromPlayer.set(
            /* x = */ chunkPosition.x - gm.playerManager.eyePosition.x,
            ///* y = */ chunkPosition.y - gm.playerManager.eyePosition.y,
            /* y = */ 0f,
            /* z = */ chunkPosition.z - gm.playerManager.eyePosition.z,
        )
        // Calculando a distância do chunk ao player.
        val distanceFromPlayer = chunkDistanceFromPlayer.length()
        // Inicia sleeping quando ultrapassa distância definida
        if (distanceFromPlayer > CHUNK_DISTANCE_INIT_TERMINATION) {
            isOutOfSight = true
            initSleeping()
            return
        }
        // Calcula direção da visão do player.
        val angleCosine = getAngleCosineBetweenVectors(
            vec0 = chunkPosition,
            vec1 = gm.playerManager.centerTarget,
            origin = gm.playerManager.eyePosition,
        )
        // Inicia verificação de exibição do chunk.
        isOutOfSight = true
        // Caso distãncia seja menor que 64, exibe o chunk, pois está bem perto do player.
        // Caso direção da visão ultrapsse +-90º, não exibe o chunk.
        if (distanceFromPlayer < 96f) {
            isOutOfSight = false
        } else if (angleCosine > 0f && distanceFromPlayer < CHUNK_DISTANCE_OUT_OF_SIGHT) {
            isOutOfSight = false
        }
        // =============================================================================================
    }

    /** Calcula o cosseno entre dois vetores vec0 e vec1 com origem em origin. Considerando apenas
     * as coordenadas x e z dos vetores, unificando a coordenada y em 0. */
    private fun getAngleCosineBetweenVectors(vec0: Vector3f, vec1: Vector3f, origin: Vector3f): Float {

        vecDirection0.set(
            /* x = */ vec0.x - origin.x,
            ///* y = */ vec0.y - origin.y,
            /* y = */ 0f,
            /* z = */ vec0.z - origin.z,
        )

        vecDirection1.set(
            /* x = */ vec1.x - origin.x,
            ///* y = */ vec1.y - origin.y,
            /* y = */ 0f,
            /* z = */ vec1.z - origin.z,
        )

        // Segundo o Copilot, fazer o inlne destas três linhas em uma só para calcular o cosseno é menos eficiente.
        // Um teste com um milhão de cálculos mostrou que o inline é realmente até 70% mais lento.
        vecDirection0.normalize()
        vecDirection1.normalize()
        val angleCosine = vecDirection0.dot(vecDirection1)

        return angleCosine
    }

    override fun initEntity() {

        if (PID_CHUNK.PID_ID == -1) createUpdateProgram()

        initResources()

        // Faz o upload dos buffers na GPU.
        updateBuffersData()

        if (verticesOpaqueQTD > 0 || verticesTransparentQTD > 0) {

            isAvailableToDraw = true
            isOutOfSight = false

            gm.rendererManager.subscribeEntityChunk(entity = this)
        } else {

            initSleeping()
        }
    }

    fun createUpdateProgram() {

        if (PID_CHUNK.PID_ID != -1) PID_CHUNK.destroy()

        val shaders = UtilityM.getShadersFromTextFile(path = shaderSourceFilePath)

        PID_CHUNK.init(
            vertexShader = shaders[0],
            fragmentShader = shaders[1]
        )

        PID_CHUNK.setUniformInt(
            name = "textureSampler",
            value = gm.resourcesManager.TBO_0_2D_ARRAY_TILES_ARRAY_SAMPLER_VALUE
        )

        val globalDataLoc = PID_CHUNK.getUniformBlockLocationByName(name = "GlobalData")

        PID_CHUNK.performProgramUniformBlockBindingUBO(
            uniformLocation = globalDataLoc,
            bindingPointNumber = gm.resourcesManager.BINDING_POINT_GLOBAL_DATA
        )

        gm.logMessage(message = PID_CHUNK.log)

        PID_CHUNK.unbindUseProgram()
    }

    fun initResources() {

        if (VAO_0_OPAQUE.VAO_ID == -1) {

            VAO_0_OPAQUE.init()
            VAO_0_OPAQUE.bind()

            VBO_0_OPAQUE.init()
            VBO_0_OPAQUE.bind()

            EBO_0_OPAQUE.init()
            EBO_0_OPAQUE.bind()

            VAO_0_OPAQUE.enableAndSetAllVertexAttributes(
                indicesComponents = ATTRIBUTES_INDICES_COMPONENTS,
                type = ATTRIBUTES_TYPE
            )

            VAO_0_OPAQUE.unbind()
            VBO_0_OPAQUE.unbind()
            EBO_0_OPAQUE.unbind()
        }

        if (VAO_0_TRANSPARENT.VAO_ID == -1) {

            VAO_0_TRANSPARENT.init()
            VAO_0_TRANSPARENT.bind()

            VBO_0_TRANSPARENT.init()
            VBO_0_TRANSPARENT.bind()

            EBO_0_TRANSPARENT.init()
            EBO_0_TRANSPARENT.bind()

            VAO_0_TRANSPARENT.enableAndSetAllVertexAttributes(
                indicesComponents = ATTRIBUTES_INDICES_COMPONENTS,
                type = ATTRIBUTES_TYPE
            )

            VAO_0_TRANSPARENT.unbind()
            VBO_0_TRANSPARENT.unbind()
            EBO_0_TRANSPARENT.unbind()

        }

        gm.logMessage(message = "Chunk InitEntity ended! -> [z: $initZ, x: $initX, y: $initY]")
    }

    fun updateBuffersData() {

        if (verticesOpaqueQTD > 0) {

            VAO_0_OPAQUE.bind()
            VBO_0_OPAQUE.bind()
            EBO_0_OPAQUE.bind()
            VBO_0_OPAQUE.bufferData(data = verticesOpaque)
            EBO_0_OPAQUE.bufferData(data = indicesOpaque)

            // Esta etapa é de extrema importância, pois do contrário, algum state ainda ativo pode
            // entrar em conflito com states de construções realizadas posteriormente e resultar
            // em renderizações incorretas.
            VAO_0_OPAQUE.unbind()
            VBO_0_OPAQUE.unbind()
            EBO_0_OPAQUE.unbind()

            verticesOpaque = EMPTY_FLOAT_BUFFER
            indicesOpaque = EMPTY_INT_BUFFER
        }

        if (verticesTransparentQTD > 0) {

            VAO_0_TRANSPARENT.bind()
            VBO_0_TRANSPARENT.bind()
            EBO_0_TRANSPARENT.bind()
            VBO_0_TRANSPARENT.bufferData(data = verticesTransparent)
            EBO_0_TRANSPARENT.bufferData(data = indicesTransparent)

            // Esta etapa é de extrema importância, pois do contrário, algum state ainda ativo pode
            // entrar em conflito com states de construções realizadas posteriormente e resultar
            // em renderizações incorretas.
            VAO_0_TRANSPARENT.unbind()
            VBO_0_TRANSPARENT.unbind()
            EBO_0_TRANSPARENT.unbind()

            verticesTransparent = EMPTY_FLOAT_BUFFER
            indicesTransparent = EMPTY_INT_BUFFER
        }
    }

    fun createUpdateMesh() {

        // verticesQtdOpaque vai acumular a quantidade de floats necessários para montar o array do mesh.
        // indicesQtdOpaque vai acumular a quantidade de ints no array de indices de vértices.
        // indicesOffsetOpaque vai marcar o avanço do valor de cada índice de vértice no array de índices.
        var verticesQtdOpaque = 0
        var indicesQtdOpaque = 0
        var indicesOffsetOpaque = 0

        // Vão avançar as posições do array de floats do mesh de vértices e do array de índices conforme
        // vão sendo inluídos os elementos em cada array.
        var verticesPositionOpaque = 0
        var indicesPositionOpaque = 0

        var verticesQtdTransparent = 0
        var indicesQtdTransparent = 0
        var indicesOffsetTransparent = 0

        var verticesPositionTransparent = 0
        var indicesPositionTransparent = 0

        val blockManagerEntries = BlockManager.entries

        // Visibibilidade para faces NA ORDEM [FRONT, BACK, RIGHT, LEFT, TOP, BOTTOM].
        val facesVisibility: BooleanArray = booleanArrayOf(false, false, false, false, false, false)

        // Coletando informações da quantidade de vértices e índices.
        for (row in 0..< CHUNK_LENGTH) {
            for (col in 0..<CHUNK_WIDTH) {
                for (top in 0..<CHUNK_HEIGHT) {

                    val blockId: Int = gm.terrainManager.getBlockId(initX + col, initY + top, initZ + row)

                    if (blockId != 5) {

                        val blockFactory = blockManagerEntries[blockId].block

                        // Blocos que vão ficar no grupo draw transparent, como
                        // água (ID 4 etc).
                        if (blockFactory.isAlphaBlended) {

                            if (blockFactory.isSimpleCube) {

                                facesVisibility.fill(element = false)

                                checkFacesVisibility(
                                    resultHolder = facesVisibility,
                                    x = col + initX,
                                    y = top + initY,
                                    z = row + initZ
                                )

                                for (result in facesVisibility) {
                                    if (result) {
                                        verticesQtdTransparent += 36
                                        indicesQtdTransparent += 6
                                    }
                                }
                            } else {

                                verticesQtdTransparent += blockFactory.vertices.size
                                indicesQtdTransparent += blockFactory.indices.size
                            }

                            // Blocos que vão ficar no grupo draw opaque, como
                        } else {

                            if (blockFactory.isSimpleCube) {

                                facesVisibility.fill(element = false)
                                checkFacesVisibility(
                                    resultHolder = facesVisibility,
                                    x = col + initX,
                                    y = top + initY,
                                    z = row + initZ
                                )

                                for (result in facesVisibility) {
                                    if (result) {
                                        verticesQtdOpaque += 36
                                        indicesQtdOpaque += 6
                                    }
                                }
                            } else {

                                verticesQtdOpaque += blockFactory.vertices.size
                                indicesQtdOpaque += blockFactory.indices.size
                            }
                        }
                    }

                }
            }
        }

        // Inicializando buffers e criando o mesh.

        if (verticesQtdOpaque > 0) {
            verticesOpaque = BufferUtils.createFloatBuffer(verticesQtdOpaque)
            indicesOpaque = BufferUtils.createIntBuffer(indicesQtdOpaque)
            verticesOpaqueQTD = verticesQtdOpaque
            indicesOpaqueQTD = indicesQtdOpaque
        }

        if (verticesQtdTransparent > 0) {
            verticesTransparent = BufferUtils.createFloatBuffer(verticesQtdTransparent)
            indicesTransparent = BufferUtils.createIntBuffer(indicesQtdTransparent)
            verticesTransparentQTD = verticesQtdTransparent
            indicesTransparentQTD = indicesQtdTransparent
        }

        for (row in 0..< CHUNK_LENGTH) {
            for (col in 0..< CHUNK_WIDTH) {
                for (top in 0..< CHUNK_HEIGHT) {

                    val blockId: Int = gm.terrainManager.getBlockId(initX + col, top, initZ + row)

                    //if (distanceFromPlayer > 96 && vegetation.contains(blockId.toShort())) continue

                    if (blockId != 5) {

                        val blockFactory = blockManagerEntries[blockId].block

                        // Blocos que vão ficar no grupo draw transparent, como
                        // água (ID 4 etc).
                        if (blockFactory.isAlphaBlended) {

                            if (blockFactory.isSimpleCube) {

                                facesVisibility.fill(element = false)
                                checkFacesVisibility(
                                    resultHolder = facesVisibility,
                                    x = col + initX,
                                    y = top,
                                    z = row + initZ
                                )

                                for (index in 0..5) {
                                    if (facesVisibility[index]) {
                                        addSimpleCubeFaces(
                                            block = blockFactory,
                                            faceIndex = index,
                                            verticesArray = verticesTransparent,
                                            indicesArray = indicesTransparent,
                                            verticesPosition = verticesPositionTransparent,
                                            indicesPosition = indicesPositionTransparent,
                                            indicesOffset = indicesOffsetTransparent,
                                            x = col + initX,
                                            y = top,
                                            z = row + initZ,
                                        )

                                        verticesPositionTransparent += 36
                                        indicesPositionTransparent += 6


                                        // fullCubeVertices tem 24 linhas de dados de 9 components, logo a
                                        // divisão por 9 informa quantos vertices cada cubo utiliza, de modo
                                        // que o offset de indices para o próximo cubo tem que avançar 24.
                                        indicesOffsetTransparent += 4
                                    }
                                }
                            } else {

                                addNotSimpleCubeFaces(
                                    block = blockFactory,
                                    verticesArray = verticesTransparent,
                                    indicesArray = indicesTransparent,
                                    verticesPosition = verticesPositionTransparent,
                                    indicesPosition = indicesPositionTransparent,
                                    indicesOffset = indicesOffsetTransparent,
                                    x = col + initX,
                                    y = top,
                                    z = row + initZ,
                                )

                                verticesPositionTransparent += blockFactory.vertices.size
                                indicesPositionTransparent += blockFactory.indices.size

                                indicesOffsetTransparent += blockFactory.vertices.size / 9
                            }

                            // Blocos que vão ficar no grupo draw opaque.
                        } else {

                            if (blockFactory.isSimpleCube) {

                                facesVisibility.fill(element = false)
                                checkFacesVisibility(
                                    resultHolder = facesVisibility,
                                    x = col + initX,
                                    y = top,
                                    z = row + initZ
                                )

                                for (index in 0..5) {
                                    if (facesVisibility[index]) {

                                        addSimpleCubeFaces(
                                            block = blockFactory,
                                            faceIndex = index,
                                            verticesArray = verticesOpaque,
                                            indicesArray = indicesOpaque,
                                            verticesPosition = verticesPositionOpaque,
                                            indicesPosition = indicesPositionOpaque,
                                            indicesOffset = indicesOffsetOpaque,
                                            x = col + initX,
                                            y = top,
                                            z = row + initZ,
                                        )

                                        verticesPositionOpaque += 36
                                        indicesPositionOpaque += 6

                                        indicesOffsetOpaque += 4
                                    }
                                }
                            } else {

                                addNotSimpleCubeFaces(
                                    block = blockFactory,
                                    verticesArray = verticesOpaque,
                                    indicesArray = indicesOpaque,
                                    verticesPosition = verticesPositionOpaque,
                                    indicesPosition = indicesPositionOpaque,
                                    indicesOffset = indicesOffsetOpaque,
                                    x = col + initX,
                                    y = top,
                                    z = row + initZ,
                                )

                                verticesPositionOpaque += blockFactory.vertices.size
                                indicesPositionOpaque += blockFactory.indices.size

                                indicesOffsetOpaque += blockFactory.vertices.size / 9
                            }
                        }
                    }
                }
            }
        }
    }

    private fun checkFacesVisibility(resultHolder: BooleanArray, x: Int, y: Int, z: Int) {

        val transparentBlocks = BlockManager.transparentBlocks

        resultHolder[0] = transparentBlocks.contains(gm.terrainManager.getBlockId(x + 0,y + 0, z + 1).toShort())
        resultHolder[1] = transparentBlocks.contains(gm.terrainManager.getBlockId(x + 0,y + 0, z - 1).toShort())
        resultHolder[2] = transparentBlocks.contains(gm.terrainManager.getBlockId(x + 1,y + 0, z + 0).toShort())
        resultHolder[3] = transparentBlocks.contains(gm.terrainManager.getBlockId(x - 1,y + 0, z + 0).toShort())
        resultHolder[4] = if (y == TerrainManager.MAX_TERRAIN_HEIGHT - 1) false else transparentBlocks.contains(
            gm.terrainManager.getBlockId(x + 0, y + 1, z + 0).toShort())
        resultHolder[5] = if (y == 0) false else transparentBlocks.contains(gm.terrainManager.getBlockId(x + 0, y - 1, z + 0).toShort())
    }

    private fun addSimpleCubeFaces(
        block: BlockFactory,
        faceIndex: Int,
        verticesArray: FloatBuffer,
        indicesArray: IntBuffer,
        verticesPosition: Int,
        indicesPosition: Int,
        indicesOffset: Int,
        x: Int,
        y: Int,
        z: Int
    ) {

        // Coletar a ambient occlusion da face por seu índice.
        // Montar a bit mask com as informações de UV, texture index e face occlusion
        // FACE_OCCLUSION -> 8 bits.
        // UV -> 2 bit
        // TEXTURE_INDEX -> 12 bits

        // BITS:
        //  31  30  29  28  27  26  25  24  23  22  21  20  19  18  17  16  15  14  13  12  11  10  9   8   7   6   5   4   3   2   1   0
        //                                          FO0 FO1 FO2 FO3 FO4 FO5 FO6 FO7 UVX UVY TEX TEX TEX TEX TEX TEX TEX TEX TEX TEX TEX TEX
        // [top, bottom, right, left, topRight, topLeft, bottomLeft, bottomRight]

        val faceOcclusion = intArrayOf(0, 0, 0, 0, 0, 0, 0, 0) // getFaceOcclusion()
        setFaceOcclusionOnHolder(holder = faceOcclusion, faceIndex = faceIndex, x = x, y = y, z = z)

        val faces: Array<FloatArray> = BlockManager.cubeFaces

        for (step in BlockManager.faceFront.indices step 9) {

            // xyz
            verticesArray.put(verticesPosition + step + 0, faces[faceIndex][step + 0] + x)
            verticesArray.put(verticesPosition + step + 1, faces[faceIndex][step + 1] + y)
            verticesArray.put(verticesPosition + step + 2, faces[faceIndex][step + 2] + z)

            // rgb
            verticesArray.put(verticesPosition + step + 3, 0.8f)
            verticesArray.put(verticesPosition + step + 4, 1.0f)
            verticesArray.put(verticesPosition + step + 5, 0.5f)

            // uv
            verticesArray.put(verticesPosition + step + 6, faces[faceIndex][step + 6])
            verticesArray.put(verticesPosition + step + 7, faces[faceIndex][step + 7])


            val uvX = faces[faceIndex][step + 6].toInt()
            val uvY = faces[faceIndex][step + 7].toInt()
            val textureIndex = block.facesIndices[faceIndex].toInt()

            val bits: Int = (textureIndex shl 0) or
                    (uvY shl 12) or
                    (uvX shl 13) or
                    (faceOcclusion[7] shl 14) or
                    (faceOcclusion[6] shl 15) or
                    (faceOcclusion[5] shl 16) or
                    (faceOcclusion[4] shl 17) or
                    (faceOcclusion[3] shl 18) or
                    (faceOcclusion[2] shl 19) or
                    (faceOcclusion[1] shl 20) or
                    (faceOcclusion[0] shl 21)

            val packedFloat: Float = Float.fromBits(bits)

            // texture index
            //verticesArray[verticesPosition + step + 8] = block.facesIndices[faceIndex]
            verticesArray.put(verticesPosition + step + 8, packedFloat)
        }

        for (index in BlockManager.fullFaceIndices.indices) {
            indicesArray.put(indicesPosition + index, BlockManager.fullFaceIndices[index] + indicesOffset)
        }
    }

    private fun addNotSimpleCubeFaces(
        block: BlockFactory,
        verticesArray: FloatBuffer,
        indicesArray: IntBuffer,
        verticesPosition: Int,
        indicesPosition: Int,
        indicesOffset: Int,
        x: Int,
        y: Int,
        z: Int
    ) {

        for (step in block.vertices.indices step 9) {

            // xyz
            verticesArray.put(verticesPosition + step + 0, block.vertices[step + 0] + x)
            verticesArray.put(verticesPosition + step + 1, block.vertices[step + 1] + y)
            verticesArray.put(verticesPosition + step + 2, block.vertices[step + 2] + z)

            // rgb
            verticesArray.put(verticesPosition + step + 3, 0.8f)
            verticesArray.put(verticesPosition + step + 4, 1.0f)
            verticesArray.put(verticesPosition + step + 5, 0.5f)

            // uv
            verticesArray.put(verticesPosition + step + 6, block.vertices[step + 6])
            verticesArray.put(verticesPosition + step + 7, block.vertices[step + 7])

            val uvX = block.vertices[step + 6].toInt()
            val uvY = block.vertices[step + 7].toInt()
            val textureIndex = block.vertices[step + 8].toInt()

            val bits: Int = (textureIndex shl 0) or
                    (uvY shl 12) or
                    (uvX shl 13) //or
            //(faceOcclusion[7] shl 14) or
            //(faceOcclusion[6] shl 15) or
            //(faceOcclusion[5] shl 16) or
            //(faceOcclusion[4] shl 17) or
            //(faceOcclusion[3] shl 18) or
            //(faceOcclusion[2] shl 19) or
            //(faceOcclusion[1] shl 20) or
            //(faceOcclusion[0] shl 21)

            val packedFloat: Float = Float.fromBits(bits)

            // texture index
            verticesArray.put(verticesPosition + step + 8, packedFloat)
        }

        for (index in block.indices.indices) {
            indicesArray.put(indicesPosition + index, block.indices[index] + indicesOffset)
        }
    }

    private fun setFaceOcclusionOnHolder(holder: IntArray, faceIndex: Int, x: Int, y: Int, z: Int): IntArray {

        var topBlock: Short = 0
        var botBlock: Short = 0
        var rigBlock: Short = 0
        var lefBlock: Short = 0
        var topRigBlock: Short = 0
        var topLefBlock: Short = 0
        var botLefBlock: Short = 0
        var botRigBlock: Short = 0

        val occludingBlocks = BlockManager.solidBlocks
        // FRONT FACE.
        if (faceIndex == 0) {

            topBlock = gm.terrainManager.getBlockId(x = x + 0, y = y + 1, z = z + 1).toShort()
            botBlock = gm.terrainManager.getBlockId(x = x + 0, y = y - 1, z = z + 1).toShort()
            rigBlock = gm.terrainManager.getBlockId(x = x + 1, y = y + 0, z = z + 1).toShort()
            lefBlock = gm.terrainManager.getBlockId(x = x - 1, y = y + 0, z = z + 1).toShort()
            topRigBlock = gm.terrainManager.getBlockId(x = x + 1, y = y + 1, z = z + 1).toShort()
            topLefBlock = gm.terrainManager.getBlockId(x = x - 1, y = y + 1, z = z + 1).toShort()
            botLefBlock = gm.terrainManager.getBlockId(x = x - 1, y = y - 1, z = z + 1).toShort()
            botRigBlock = gm.terrainManager.getBlockId(x = x + 1, y = y - 1, z = z + 1).toShort()

            if (occludingBlocks.contains(topBlock)) holder[0] = 1
            if (occludingBlocks.contains(botBlock)) holder[1] = 1
            if (occludingBlocks.contains(rigBlock)) holder[2] = 1
            if (occludingBlocks.contains(lefBlock)) holder[3] = 1
            if (occludingBlocks.contains(topRigBlock)) holder[4] = 1
            if (occludingBlocks.contains(topLefBlock)) holder[5] = 1
            if (occludingBlocks.contains(botLefBlock)) holder[6] = 1
            if (occludingBlocks.contains(botRigBlock)) holder[7] = 1

        }
        // BACK FACE.
        else if (faceIndex == 1) {

            topBlock = gm.terrainManager.getBlockId(x = x + 0, y = y + 1, z = z - 1).toShort()
            botBlock = gm.terrainManager.getBlockId(x = x + 0, y = y - 1, z = z - 1).toShort()
            rigBlock = gm.terrainManager.getBlockId(x = x - 1, y = y + 0, z = z - 1).toShort()
            lefBlock = gm.terrainManager.getBlockId(x = x + 1, y = y + 0, z = z - 1).toShort()
            topRigBlock = gm.terrainManager.getBlockId(x = x - 1, y = y + 1, z = z - 1).toShort()
            topLefBlock = gm.terrainManager.getBlockId(x = x + 1, y = y + 1, z = z - 1).toShort()
            botLefBlock = gm.terrainManager.getBlockId(x = x + 1, y = y - 1, z = z - 1).toShort()
            botRigBlock = gm.terrainManager.getBlockId(x = x - 1, y = y - 1, z = z - 1).toShort()

            if (occludingBlocks.contains(topBlock)) holder[0] = 1
            if (occludingBlocks.contains(botBlock)) holder[1] = 1
            if (occludingBlocks.contains(rigBlock)) holder[2] = 1
            if (occludingBlocks.contains(lefBlock)) holder[3] = 1
            if (occludingBlocks.contains(topRigBlock)) holder[4] = 1
            if (occludingBlocks.contains(topLefBlock)) holder[5] = 1
            if (occludingBlocks.contains(botLefBlock)) holder[6] = 1
            if (occludingBlocks.contains(botRigBlock)) holder[7] = 1

        }
        // RIGHT FACE.
        else if (faceIndex == 2) {

            topBlock = gm.terrainManager.getBlockId(x = x + 1, y = y + 1, z = z + 0).toShort()
            botBlock = gm.terrainManager.getBlockId(x = x + 1, y = y - 1, z = z + 0).toShort()
            rigBlock = gm.terrainManager.getBlockId(x = x + 1, y = y + 0, z = z - 1).toShort()
            lefBlock = gm.terrainManager.getBlockId(x = x + 1, y = y + 0, z = z + 1).toShort()
            topRigBlock = gm.terrainManager.getBlockId(x = x + 1, y = y + 1, z = z - 1).toShort()
            topLefBlock = gm.terrainManager.getBlockId(x = x + 1, y = y + 1, z = z + 1).toShort()
            botLefBlock = gm.terrainManager.getBlockId(x = x + 1, y = y - 1, z = z + 1).toShort()
            botRigBlock = gm.terrainManager.getBlockId(x = x + 1, y = y - 1, z = z - 1).toShort()

            if (occludingBlocks.contains(topBlock)) holder[0] = 1
            if (occludingBlocks.contains(botBlock)) holder[1] = 1
            if (occludingBlocks.contains(rigBlock)) holder[2] = 1
            if (occludingBlocks.contains(lefBlock)) holder[3] = 1
            if (occludingBlocks.contains(topRigBlock)) holder[4] = 1
            if (occludingBlocks.contains(topLefBlock)) holder[5] = 1
            if (occludingBlocks.contains(botLefBlock)) holder[6] = 1
            if (occludingBlocks.contains(botRigBlock)) holder[7] = 1

        }
        // LEFT FACE.
        else if (faceIndex == 3) {

            topBlock = gm.terrainManager.getBlockId(x = x - 1, y = y + 1, z = z + 0).toShort()
            botBlock = gm.terrainManager.getBlockId(x = x - 1, y = y - 1, z = z + 0).toShort()
            rigBlock = gm.terrainManager.getBlockId(x = x - 1, y = y + 0, z = z + 1).toShort()
            lefBlock = gm.terrainManager.getBlockId(x = x - 1, y = y + 0, z = z - 1).toShort()
            topRigBlock = gm.terrainManager.getBlockId(x = x - 1, y = y + 1, z = z + 1).toShort()
            topLefBlock = gm.terrainManager.getBlockId(x = x - 1, y = y + 1, z = z - 1).toShort()
            botLefBlock = gm.terrainManager.getBlockId(x = x - 1, y = y - 1, z = z - 1).toShort()
            botRigBlock = gm.terrainManager.getBlockId(x = x - 1, y = y - 1, z = z + 1).toShort()

            if (occludingBlocks.contains(topBlock)) holder[0] = 1
            if (occludingBlocks.contains(botBlock)) holder[1] = 1
            if (occludingBlocks.contains(rigBlock)) holder[2] = 1
            if (occludingBlocks.contains(lefBlock)) holder[3] = 1
            if (occludingBlocks.contains(topRigBlock)) holder[4] = 1
            if (occludingBlocks.contains(topLefBlock)) holder[5] = 1
            if (occludingBlocks.contains(botLefBlock)) holder[6] = 1
            if (occludingBlocks.contains(botRigBlock)) holder[7] = 1

        }
        // TOP FACE.
        else if (faceIndex == 4) {

            topBlock = gm.terrainManager.getBlockId(x = x + 0, y = y + 1, z = z - 1).toShort()
            botBlock = gm.terrainManager.getBlockId(x = x + 0, y = y + 1, z = z + 1).toShort()
            rigBlock = gm.terrainManager.getBlockId(x = x + 1, y = y + 1, z = z + 0).toShort()
            lefBlock = gm.terrainManager.getBlockId(x = x - 1, y = y + 1, z = z + 0).toShort()
            topRigBlock = gm.terrainManager.getBlockId(x = x + 1, y = y + 1, z = z - 1).toShort()
            topLefBlock = gm.terrainManager.getBlockId(x = x - 1, y = y + 1, z = z - 1).toShort()
            botLefBlock = gm.terrainManager.getBlockId(x = x - 1, y = y + 1, z = z + 1).toShort()
            botRigBlock = gm.terrainManager.getBlockId(x = x + 1, y = y + 1, z = z + 1).toShort()

            if (occludingBlocks.contains(topBlock)) holder[0] = 1
            if (occludingBlocks.contains(botBlock)) holder[1] = 1
            if (occludingBlocks.contains(rigBlock)) holder[2] = 1
            if (occludingBlocks.contains(lefBlock)) holder[3] = 1
            if (occludingBlocks.contains(topRigBlock)) holder[4] = 1
            if (occludingBlocks.contains(topLefBlock)) holder[5] = 1
            if (occludingBlocks.contains(botLefBlock)) holder[6] = 1
            if (occludingBlocks.contains(botRigBlock)) holder[7] = 1

        }
        // BOTTOM FACE.
        else if (faceIndex == 5) {

            topBlock = gm.terrainManager.getBlockId(x = x + 0, y = y - 1, z = z + 1).toShort()
            botBlock = gm.terrainManager.getBlockId(x = x + 0, y = y - 1, z = z - 1).toShort()
            rigBlock = gm.terrainManager.getBlockId(x = x + 1, y = y - 1, z = z + 0).toShort()
            lefBlock = gm.terrainManager.getBlockId(x = x - 1, y = y - 1, z = z + 0).toShort()
            topRigBlock = gm.terrainManager.getBlockId(x = x + 1, y = y - 1, z = z + 1).toShort()
            topLefBlock = gm.terrainManager.getBlockId(x = x - 1, y = y - 1, z = z + 1).toShort()
            botLefBlock = gm.terrainManager.getBlockId(x = x - 1, y = y - 1, z = z - 1).toShort()
            botRigBlock = gm.terrainManager.getBlockId(x = x + 1, y = y - 1, z = z - 1).toShort()

            if (occludingBlocks.contains(topBlock)) holder[0] = 1
            if (occludingBlocks.contains(botBlock)) holder[1] = 1
            if (occludingBlocks.contains(rigBlock)) holder[2] = 1
            if (occludingBlocks.contains(lefBlock)) holder[3] = 1
            if (occludingBlocks.contains(topRigBlock)) holder[4] = 1
            if (occludingBlocks.contains(topLefBlock)) holder[5] = 1
            if (occludingBlocks.contains(botLefBlock)) holder[6] = 1
            if (occludingBlocks.contains(botRigBlock)) holder[7] = 1

        }

        return holder
    }

    override fun drawOpaque(dt: Float) {

        if (verticesOpaqueQTD == 0 || !isAvailableToDraw || isOutOfSight || isSleeping) return

        VAO_0_OPAQUE.bind()
        PID_CHUNK.bindUseProgram()

        EBO_0_OPAQUE.drawElementsTriangles()
    }

    override fun drawTransparent(dt: Float) {

        if (verticesTransparentQTD == 0 || !isAvailableToDraw || isOutOfSight || isSleeping) return

        VAO_0_TRANSPARENT.bind()
        PID_CHUNK.bindUseProgram()

        glEnable(GL_BLEND)

        EBO_0_TRANSPARENT.drawElementsTriangles()

        glDisable(GL_BLEND)
    }

    override fun initSleeping() {
        if (isSleeping) return
        isSleeping = true
        gm.rendererManager.subscribeEntitySleeping(entity = this)
    }

    override fun sleep() {

        gm.logMessage(message = "CHUNK_SLEEPING: init chunk sleeping...")

        verticesOpaqueQTD = 0
        indicesOpaqueQTD = 0
        verticesTransparentQTD = 0
        indicesTransparentQTD = 0

        isAvailableToDraw = false
        isSleeping = true
        isOutOfSight = true

//        initX = 0
//        initZ = 0
//        initY = 0

        gm.rendererManager.unsubscribeEntityChunk(entity = this)
        gm.chunkManager.removeChunkFromMapOfChunks(chunk = this)
        gm.chunkManager.releaseChunk(chunk = this)

        chunkKey = ""
    }

    override fun awake() {

        gm.logMessage(message = "CHUNK_AWAKING: init chunk awakening...")

        // isSleeping
        // chunkKey
        //    var initX: Int = 0,
        //    var initZ: Int = 0,
        //    var initY: Int = 0,

        isSleeping = false

        chunkPosition.x = initX + CHUNK_WIDTH / 2f
        //chunkPosition.y = initY + CHUNK_HEIGHT / 2f
        chunkPosition.y = 0f
        chunkPosition.z = initZ + CHUNK_LENGTH / 2f

        val keyInitX = gm.chunkManager.getInitChunkKeyByWorldPositionX(x = initX.toFloat())
        val keyInitZ = gm.chunkManager.getInitChunkKeyByWorldPositionZ(z = initZ.toFloat())
        val keyInitY = gm.chunkManager.getInitChunkKeyByWorldPositionY(y = initY.toFloat())

        chunkKey =  gm.chunkManager.getChunkKeyByOriginValues(
            initX = keyInitX,
            initY = keyInitY,
            initZ = keyInitZ,
        )
    }
}