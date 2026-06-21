package com.medieval.managers

import com.medieval.foundation.CubeM
import org.joml.Vector3f
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

interface MovingEntity {

    var collisionWidth: Float
    var collisionLength: Float
    var collisionHeight: Float

    var collisionBounds: CubeM

    var isMovingForward: Boolean
    var isMovingBackwards: Boolean
    var isMovingRight: Boolean
    var isMovingLeft: Boolean

    var isRunning: Boolean
    var isJumping: Boolean
    var isUnderWater: Boolean
    var isFreeFlying: Boolean

    var runSpeed: Float
    var walkSpeed: Float
    var runWalkSpeedDelayMaxValue: Float
    var runWalkSpeedDelayCounter: Float
    var dragSpeed: Float

    var anglePlaneXZ: Double
    var anglePlaneYZ: Double
    var anglePlaneXY: Double

    var jumpingAnglePlaneXZ: Double

    var eyePosition: Vector3f
    var centerTarget: Vector3f
    var upVector: Vector3f

    var jumpCounter: Float
    var jumpInitialPositionY: Float

    var fallCounter: Float
    var fallInitialPositionY: Float

    var owner: MovingEntity


    companion object {

        private val movingEntities: MutableList<MovingEntity> = mutableListOf()

        fun subscribeMovingEntity(movingEntity: MovingEntity) {
            synchronized(movingEntities) {
                movingEntities.add(movingEntity)
            }
        }

        fun unsubscribeMovingEntity(movingEntity: MovingEntity) {
            synchronized(movingEntities) {
                movingEntities.remove(movingEntity)
            }
        }

        fun checkWater(gm: GameManager, movingEntity: PlayerManager) {

            var posZ = movingEntity.eyePosition.z
            var posX = movingEntity.eyePosition.x
            val posY = (movingEntity.eyePosition.y - movingEntity.collisionHeight/2f).toInt()

            // Ajustando localização para conseguir a posição no block map.
            if (posZ < 0) posZ = ((posZ - 0.5f).toInt().toFloat()) else posZ = ((posZ + 0.5f).toInt().toFloat())
            if (posX < 0) posX = ((posX - 0.5f).toInt().toFloat()) else posX = ((posX + 0.5f).toInt().toFloat())

            val blockId0: Int =  gm.terrainManager.getBlockId(x = posX.toInt(), y = posY + 0, z = posZ.toInt())
            val blockId1: Int =  gm.terrainManager.getBlockId(x = posX.toInt(), y = posY + 1, z = posZ.toInt())
            val blockId2: Int =  gm.terrainManager.getBlockId(x = posX.toInt(), y = posY + 2, z = posZ.toInt())

            if (   blockId0 == BlockManager.BLOCK_004_WATER_SURFACE.ordinal
                || blockId1 == BlockManager.BLOCK_004_WATER_SURFACE.ordinal
                || blockId2 == BlockManager.BLOCK_004_WATER_SURFACE.ordinal) {

                movingEntity.isUnderWater = true
                movingEntity.dragSpeed = 0.35f
            } else {

                movingEntity.isUnderWater = false
                movingEntity.dragSpeed = 1.0f
            }
        }

        fun performJump(gm: GameManager, movingEntity: MovingEntity, deltaTime: Float) {

            if (movingEntity.isFreeFlying || !movingEntity.isJumping) return

            // Caso não esteja contando pulo (não está realizando pulo, então está caindo ou pode estar no chão)
            // e caso não esteja no chaõ (isBottomBlocked), não realiza pulo e retorna do método..
            if (movingEntity.jumpCounter == 0f) {

                if (movingEntity.isUnderWater) {

                } else {

                    movingEntity.eyePosition.y -= 0.001f

                    if (!isBottomBlocked(gm = gm, movingEntity = movingEntity)) {

                        movingEntity.eyePosition.y += 0.001f
                        movingEntity.isJumping = false
                        return
                    }

                    movingEntity.eyePosition.y += 0.001f
                }
            }

            // Caso esteja iniciando pulo (jumpCounter == 0f), registra posição inicial para iniciar pulo e
            // caso jumpCounter != 0f, já está pulando e segue em frente.
            if (movingEntity.jumpCounter == 0f) movingEntity.jumpInitialPositionY = movingEntity.eyePosition.y

            val tempY = movingEntity.eyePosition.y

            var heightInit: Float = -(movingEntity.jumpCounter * 1.50f - 1.1f).pow(4) + 1.464f

            movingEntity.jumpCounter += deltaTime * movingEntity.dragSpeed

            //val heightEnd: Float = -(movingEntity.jumpCounter * 0.8f - 1f).pow(4) + 1f
            var heightEnd: Float = -(movingEntity.jumpCounter * 1.50f - 1.1f).pow(4) + 1.464f

            movingEntity.eyePosition.y = movingEntity.jumpInitialPositionY + heightEnd
            movingEntity.centerTarget.y = movingEntity.centerTarget.y + heightEnd - heightInit

            if (movingEntity.jumpCounter > 0.5f) {
                movingEntity.jumpCounter = 0f
                movingEntity.isJumping = false
            }

            // Bloqueio pelo terreno estático.
            if (isTopBlocked(gm = gm, movingEntity = movingEntity)) {
                movingEntity.isJumping = false
                movingEntity.eyePosition.y = movingEntity.jumpInitialPositionY + heightInit
                movingEntity.jumpCounter = 0f
            }

            // Bloqueio por objetos em movimento.
//            if (isNPCBlocked(gm = gm, movingEntity = movingEntity)) {
//                movingEntity.isJumping = false
//                movingEntity.jumpCounter = 0f
//                movingEntity.eyePosition.y = tempY
//            }
        }

        fun performFall(gm: GameManager, movingEntity: MovingEntity, deltaTime: Float) {

            if (movingEntity.isFreeFlying || movingEntity.isJumping) return

            if (movingEntity.fallCounter == 0f) movingEntity.fallInitialPositionY = movingEntity.eyePosition.y

            val tempTargetY = movingEntity.centerTarget.y

            movingEntity.fallCounter += deltaTime * movingEntity.dragSpeed

            val fall: Float = (movingEntity.fallCounter * 0.6f).pow(3)

            movingEntity.eyePosition.y -= fall
            movingEntity.centerTarget.y -= fall

            // Bloqueio pelo terreno estático.
            if (isBottomBlocked(gm = gm, movingEntity = movingEntity)) {

                movingEntity.fallCounter = 0f
                movingEntity.eyePosition.y = (movingEntity.eyePosition.y - movingEntity.collisionHeight/2f).toInt() + 0.5f + movingEntity.collisionHeight/2f + 0.001f
                movingEntity.centerTarget.y = tempTargetY
            }

            // Bloqueio por objetos em movimento.
//            if (isNPCBlocked(gm = gm, movingEntity = movingEntity)) {
//                movingEntity.fallCounter = 0f
//                movingEntity.eyePosition.y = tempY
//            }
        }

        fun performSideMovement(gm: GameManager, movingEntity: PlayerManager, deltaTime: Float) {

            if ((!movingEntity.isJumping || movingEntity.isUnderWater) && movingEntity.fallCounter == 0f) movingEntity.jumpingAnglePlaneXZ = movingEntity.anglePlaneXZ

            val sinPlaneXZJumping = sin(Math.toRadians(movingEntity.jumpingAnglePlaneXZ)).toFloat()
            val cosPlaneXZJumping = cos(Math.toRadians(movingEntity.jumpingAnglePlaneXZ)).toFloat()

            val sinPlaneXZ = sin(Math.toRadians(movingEntity.anglePlaneXZ)).toFloat()
            val cosPlaneXZ = cos(Math.toRadians(movingEntity.anglePlaneXZ)).toFloat()
            val sinPlaneYZ = sin(Math.toRadians(movingEntity.anglePlaneYZ)).toFloat()
            val cosPlaneYZ = cos(Math.toRadians(movingEntity.anglePlaneYZ)).toFloat()

            var tempSpeedWalk: Float = movingEntity.walkSpeed
            var runWalkSpeedDelayTemp: Float = 0f

            if (movingEntity.isRunning) tempSpeedWalk = movingEntity.runSpeed

            // Se estiver se movendo aplica o delay sobre o movimento inicial. Do contrário,
            // zera o valor.
            if (movingEntity.isMovingForward || movingEntity.isMovingBackwards || movingEntity.isMovingLeft
                || movingEntity.isMovingRight) {

                if (movingEntity.runWalkSpeedDelayCounter < movingEntity.runWalkSpeedDelayMaxValue) {
                    movingEntity.runWalkSpeedDelayCounter += deltaTime
                }

                runWalkSpeedDelayTemp = movingEntity.runWalkSpeedDelayCounter / movingEntity.runWalkSpeedDelayMaxValue
            } else {

                movingEntity.runWalkSpeedDelayCounter = 0f
            }

            val finalSpeed: Float = tempSpeedWalk * runWalkSpeedDelayTemp * deltaTime * movingEntity.dragSpeed

            /**
            sin(𝜃 + 180) = −sin(𝜃)      cos(𝜃 + 180) = −cos(𝜃)
            sin(𝜃 + 90) = cos(𝜃)        sin(𝜃 − 90) = −cos(𝜃)
            cos(𝜃 + 90) = −sin(𝜃)       cos(𝜃 − 90) = sin(𝜃)
             */

            if (movingEntity.isMovingForward) {

                val tempZ = movingEntity.eyePosition.z
                movingEntity.eyePosition.z += sinPlaneXZJumping * finalSpeed
                if (isSideBlocked(gm = gm, movingEntity = movingEntity)) movingEntity.eyePosition.z = tempZ

                if (movingEntity.isFreeFlying) movingEntity.eyePosition.y += sinPlaneYZ * finalSpeed

                val tempX = movingEntity.eyePosition.x
                movingEntity.eyePosition.x += cosPlaneXZJumping * finalSpeed
                if (isSideBlocked(gm = gm, movingEntity = movingEntity)) movingEntity.eyePosition.x = tempX
            }

            if (movingEntity.isMovingBackwards) {

                val tempZ = movingEntity.eyePosition.z
                movingEntity.eyePosition.z -= sinPlaneXZJumping * finalSpeed
                if (isSideBlocked(gm = gm, movingEntity = movingEntity)) movingEntity.eyePosition.z = tempZ

                if (movingEntity.isFreeFlying) movingEntity.eyePosition.y -= sinPlaneYZ * finalSpeed

                val tempX = movingEntity.eyePosition.x
                movingEntity.eyePosition.x -= cosPlaneXZJumping * finalSpeed
                if (isSideBlocked(gm = gm, movingEntity = movingEntity)) movingEntity.eyePosition.x = tempX
            }

            if (movingEntity.isMovingRight) {

                val tempZ = movingEntity.eyePosition.z
                movingEntity.eyePosition.z += cosPlaneXZ * finalSpeed
                if (isSideBlocked(gm = gm, movingEntity = movingEntity)) movingEntity.eyePosition.z = tempZ

                val tempX = movingEntity.eyePosition.x
                movingEntity.eyePosition.x -= sinPlaneXZ * finalSpeed
                if (isSideBlocked(gm = gm, movingEntity = movingEntity)) movingEntity.eyePosition.x = tempX
            }

            if (movingEntity.isMovingLeft) {

                val tempZ = movingEntity.eyePosition.z
                movingEntity.eyePosition.z -= cosPlaneXZ * finalSpeed
                if (isSideBlocked(gm = gm, movingEntity = movingEntity)) movingEntity.eyePosition.z = tempZ

                val tempX = movingEntity.eyePosition.x
                movingEntity.eyePosition.x += sinPlaneXZ * finalSpeed
                if (isSideBlocked(gm = gm, movingEntity = movingEntity)) movingEntity.eyePosition.x = tempX
            }

            movingEntity.centerTarget.z = movingEntity.eyePosition.z + sinPlaneXZ * cosPlaneYZ
            movingEntity.centerTarget.y = movingEntity.eyePosition.y + sinPlaneYZ
            movingEntity.centerTarget.x = movingEntity.eyePosition.x + cosPlaneXZ * cosPlaneYZ
        }

        private fun isBottomBlocked(gm: GameManager, movingEntity: MovingEntity): Boolean {

            // Vamos considerar xyz no centro da collisionBounds. Assim, devemos verifcar na
            // posição metade abaixo do centro da collisionBounds em y.
            var posZ = movingEntity.eyePosition.z
            var posX = movingEntity.eyePosition.x
            val posY = (movingEntity.eyePosition.y - movingEntity.collisionHeight/2f).toInt()

            // Ajustando localização para conseguir a posição no block map.
            if (posZ < 0) posZ = ((posZ - 0.5f).toInt().toFloat()) else posZ = ((posZ + 0.5f).toInt().toFloat())
            if (posX < 0) posX = ((posX - 0.5f).toInt().toFloat()) else posX = ((posX + 0.5f).toInt().toFloat())

            movingEntity.collisionBounds.x = movingEntity.eyePosition.x
            movingEntity.collisionBounds.y = movingEntity.eyePosition.y
            movingEntity.collisionBounds.z = movingEntity.eyePosition.z

                    // CENTER BLOCK
            return     checkTerrainBlocking(gm = gm, movingEntity = movingEntity, posX = (posX + 0).toInt(), posY = posY + 0, posZ = (posZ + 0).toInt())
                    // FRONT BLOCK
                    || checkTerrainBlocking(gm = gm, movingEntity = movingEntity, posX = (posX + 0).toInt(), posY = posY + 0, posZ = (posZ - 1).toInt())
                    // BACK BLOCK
                    || checkTerrainBlocking(gm = gm, movingEntity = movingEntity, posX = (posX + 0).toInt(), posY = posY + 0, posZ = (posZ + 1).toInt())
                    // CENTER RIGHT BLOCK
                    || checkTerrainBlocking(gm = gm, movingEntity = movingEntity, posX = (posX + 1).toInt(), posY = posY + 0, posZ = (posZ + 0).toInt())
                    // CENTER LEFT BLOCK
                    || checkTerrainBlocking(gm = gm, movingEntity = movingEntity, posX = (posX - 1).toInt(), posY = posY + 0, posZ = (posZ + 0).toInt())
                    // FRONT RIGHT BLOCK
                    || checkTerrainBlocking(gm = gm, movingEntity = movingEntity, posX = (posX + 1).toInt(), posY = posY + 0, posZ = (posZ - 1).toInt())
                    // FRONT LEFT BLOCK
                    || checkTerrainBlocking(gm = gm, movingEntity = movingEntity, posX = (posX - 1).toInt(), posY = posY + 0, posZ = (posZ - 1).toInt())
                    // BACK RIGHT BLOCK
                    || checkTerrainBlocking(gm = gm, movingEntity = movingEntity, posX = (posX + 1).toInt(), posY = posY + 0, posZ = (posZ + 1).toInt())
                    // BACK LEFT BLOCK
                    || checkTerrainBlocking(gm = gm, movingEntity = movingEntity, posX = (posX - 1).toInt(), posY = posY + 0, posZ = (posZ + 1).toInt())
        }

        private fun isTopBlocked(gm: GameManager, movingEntity: MovingEntity): Boolean {

            // Coletando a posição logo acima da altura da câmera.
            var posX = movingEntity.eyePosition.x
            val posY = (movingEntity.eyePosition.y + movingEntity.collisionHeight/2f).toInt() + 1
            var posZ = movingEntity.eyePosition.z

            // Ajustando localização para conseguir a posição no block map.
            if (posZ < 0) posZ = ((posZ - 0.5f).toInt().toFloat()) else posZ = ((posZ + 0.5f).toInt().toFloat())
            if (posX < 0) posX = ((posX - 0.5f).toInt().toFloat()) else posX = ((posX + 0.5f).toInt().toFloat())

            movingEntity.collisionBounds.x = movingEntity.eyePosition.x
            movingEntity.collisionBounds.y = movingEntity.eyePosition.y + 0.10f
            movingEntity.collisionBounds.z = movingEntity.eyePosition.z

            // CENTER BLOCK
            return     checkTerrainBlocking(gm = gm, movingEntity = movingEntity, posX = (posX + 0).toInt(), posY = posY + 0, posZ = (posZ + 0).toInt())
                    // FRONT BLOCK
                    || checkTerrainBlocking(gm = gm, movingEntity = movingEntity, posX = (posX + 0).toInt(), posY = posY + 0, posZ = (posZ - 1).toInt())
                    // BACK BLOCK
                    || checkTerrainBlocking(gm = gm, movingEntity = movingEntity, posX = (posX + 0).toInt(), posY = posY + 0, posZ = (posZ + 1).toInt())
                    // CENTER RIGHT BLOCK
                    || checkTerrainBlocking(gm = gm, movingEntity = movingEntity, posX = (posX + 1).toInt(), posY = posY + 0, posZ = (posZ + 0).toInt())
                    // CENTER LEFT BLOCK
                    || checkTerrainBlocking(gm = gm, movingEntity = movingEntity, posX = (posX - 1).toInt(), posY = posY + 0, posZ = (posZ + 0).toInt())
                    // FRONT RIGHT BLOCK
                    || checkTerrainBlocking(gm = gm, movingEntity = movingEntity, posX = (posX + 1).toInt(), posY = posY + 0, posZ = (posZ - 1).toInt())
                    // FRONT LEFT BLOCK
                    || checkTerrainBlocking(gm = gm, movingEntity = movingEntity, posX = (posX - 1).toInt(), posY = posY + 0, posZ = (posZ - 1).toInt())
                    // BACK RIGHT BLOCK
                    || checkTerrainBlocking(gm = gm, movingEntity = movingEntity, posX = (posX + 1).toInt(), posY = posY + 0, posZ = (posZ + 1).toInt())
                    // BACK LEFT BLOCK
                    || checkTerrainBlocking(gm = gm, movingEntity = movingEntity, posX = (posX - 1).toInt(), posY = posY + 0, posZ = (posZ + 1).toInt())
        }

        private fun isSideBlocked(gm: GameManager, movingEntity: PlayerManager): Boolean {

            if (movingEntity.isFreeFlying) return false

            // Coletando a posição na base da câmera.
            var posZ = movingEntity.eyePosition.z
            var posX = movingEntity.eyePosition.x
            val posY = (movingEntity.eyePosition.y - movingEntity.collisionHeight/2f).toInt() + 1

            // Ajustando localização para conseguir a posição no block map.
            if (posZ < 0) posZ = ((posZ - 0.5f).toInt().toFloat()) else posZ = ((posZ + 0.5f).toInt().toFloat())
            if (posX < 0) posX = ((posX - 0.5f).toInt().toFloat()) else posX = ((posX + 0.5f).toInt().toFloat())

            movingEntity.collisionBounds.x = movingEntity.eyePosition.x
            movingEntity.collisionBounds.y = movingEntity.eyePosition.y
            movingEntity.collisionBounds.z = movingEntity.eyePosition.z

            // Aqui vamos verificar os locos laterais até dois blocos a partir da base e 1 acima,
            // Já que player tem 1.5f de altura.

                    // BLOCOS AO LADO DA BASE.
                    // FRONT BLOCK
            return     checkTerrainBlocking(gm = gm, movingEntity = movingEntity, posX = (posX + 0).toInt(), posY = posY + 0, posZ = (posZ - 1).toInt())
                    // BACK BLOCK
                    || checkTerrainBlocking(gm = gm, movingEntity = movingEntity, posX = (posX + 0).toInt(), posY = posY + 0, posZ = (posZ + 1).toInt())
                    // RIGHT BLOCK
                    || checkTerrainBlocking(gm = gm, movingEntity = movingEntity, posX = (posX + 1).toInt(), posY = posY + 0, posZ = (posZ + 0).toInt())
                    // LEFT BLOCK
                    || checkTerrainBlocking(gm = gm, movingEntity = movingEntity, posX = (posX - 1).toInt(), posY = posY + 0, posZ = (posZ + 0).toInt())
                    // TOP RIGHT BLOCK
                    || checkTerrainBlocking(gm = gm, movingEntity = movingEntity, posX = (posX + 1).toInt(), posY = posY + 0, posZ = (posZ - 1).toInt())
                    // TOP LEFT BLOCK
                    || checkTerrainBlocking(gm = gm, movingEntity = movingEntity, posX = (posX - 1).toInt(), posY = posY + 0, posZ = (posZ - 1).toInt())
                    // BOTTOM RIGHT BLOCK
                    || checkTerrainBlocking(gm = gm, movingEntity = movingEntity, posX = (posX + 1).toInt(), posY = posY + 0, posZ = (posZ + 1).toInt())
                    // BOTTOM LEFT BLOCK
                    || checkTerrainBlocking(gm = gm, movingEntity = movingEntity, posX = (posX - 1).toInt(), posY = posY + 0, posZ = (posZ + 1).toInt())

                    // BLOCOS 1 UNIDADE ACIMA DOS BLOCOS AO LADO DA BASE.
                    // FRONT BLOCK
                    || checkTerrainBlocking(gm = gm, movingEntity = movingEntity, posX = (posX + 0).toInt(), posY = posY + 1, posZ = (posZ - 1).toInt())
                    // BACK BLOCK
                    || checkTerrainBlocking(gm = gm, movingEntity = movingEntity, posX = (posX + 0).toInt(), posY = posY + 1, posZ = (posZ + 1).toInt())
                    // RIGHT BLOCK
                    || checkTerrainBlocking(gm = gm, movingEntity = movingEntity, posX = (posX + 1).toInt(), posY = posY + 1, posZ = (posZ + 0).toInt())
                    // LEFT BLOCK
                    || checkTerrainBlocking(gm = gm, movingEntity = movingEntity, posX = (posX - 1).toInt(), posY = posY + 1, posZ = (posZ + 0).toInt())
                    // TOP RIGHT BLOCK
                    || checkTerrainBlocking(gm = gm, movingEntity = movingEntity, posX = (posX + 1).toInt(), posY = posY + 1, posZ = (posZ - 1).toInt())
                    // TOP LEFT BLOCK
                    || checkTerrainBlocking(gm = gm, movingEntity = movingEntity, posX = (posX - 1).toInt(), posY = posY + 1, posZ = (posZ - 1).toInt())
                    // BOTTOM RIGHT BLOCK
                    || checkTerrainBlocking(gm = gm, movingEntity = movingEntity, posX = (posX + 1).toInt(), posY = posY + 1, posZ = (posZ + 1).toInt())
                    // BOTTOM LEFT BLOCK
                    || checkTerrainBlocking(gm = gm, movingEntity = movingEntity, posX = (posX - 1).toInt(), posY = posY + 1, posZ = (posZ + 1).toInt())
        }

        private fun isEntityBlocked(): Boolean {

            return false
        }

        private fun checkTerrainBlocking(gm: GameManager, movingEntity: MovingEntity, posX: Int, posY: Int, posZ: Int): Boolean {

            // Coleta a ID do bloco no block map na localização informada.
            //val block: Short =  TerrainManager.getBlockMap()[posZ][posX][posY]
            val blockId: Short =  gm.terrainManager.getBlockId(x = posX, y = posY, z = posZ).toShort()
            // Verifica a colisão caso o bloco seja sólido (colidível)
            if (BlockManager.solidBlocks.contains(element = blockId)) {
                // Coleta as dimensões da collision bounds do bloco padrão a patir de block: Short.
                val blockIdToInt: Int = blockId.toInt()
                val collisionBounds: CubeM = BlockManager.entries[blockIdToInt].block.collisionBounds
                // Posição de bounds retorna para a posição real no mundo. Até aqui xyz indicaram a posição
                // do bloco no block map no intervalo 0..1023. Para collisionBounds, xyz devem considerar a
                // posição real do objecto no espaço, logo, devemos devolver os valores de localização do
                // terreno, que são as coordenads reais de mundo.
                collisionBounds.x = posX.toFloat()
                collisionBounds.y = posY.toFloat()
                collisionBounds.z = posZ.toFloat()

                if (collisionBounds.intersects(movingEntity.collisionBounds)) return true
            }

            return false
        }
    }
}