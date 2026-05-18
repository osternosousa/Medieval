package com.medieval.managers

import com.medieval.components.factory_blocks.Block000DirtTerrain
import com.medieval.components.factory_blocks.Block001GrassTerrain
import com.medieval.components.factory_blocks.Block002RockTerrain
import com.medieval.components.factory_blocks.Block003SandTerrain
import com.medieval.components.factory_blocks.Block004WaterSurface
import com.medieval.components.factory_blocks.Block005Air
import com.medieval.components.factory_blocks.Block006Glass
import com.medieval.components.factory_blocks.Block007WoodA
import com.medieval.components.factory_blocks.Block008Treetop
import com.medieval.components.factory_blocks.Block009FlowerThreePink
import com.medieval.components.factory_blocks.Block010Lawn
import com.medieval.components.factory_blocks.Block011FlowerThreeYellow
import com.medieval.components.factory_blocks.Block012UnderWaterSoil
import com.medieval.components.factory_blocks.Block013FlowerOneRed
import com.medieval.components.factory_blocks.Block014FlowerOneYellow
import com.medieval.components.factory_blocks.Block015Bush
import com.medieval.foundation.BlockFactory

enum class BlockManager(val block: BlockFactory) {

    BLOCK_000_DIRT_TERRAIN(block = Block000DirtTerrain()),
    BLOCK_001_GRASS_TERRAIN(block = Block001GrassTerrain()),
    BLOCK_002_ROCK_TERRAIN(block = Block002RockTerrain()),
    BLOCK_003_SAND_TERRAIN(block = Block003SandTerrain()),
    BLOCK_004_WATER_SURFACE(block = Block004WaterSurface()),
    BLOCK_005_AIR(block = Block005Air()),
    BLOCK_006_GLASS(block = Block006Glass()),
    BLOCK_007_WOOD_A(block = Block007WoodA()),
    BLOCK_008_TREETOP(block = Block008Treetop()),
    BLOCK_009_THREE_PINK_FLOWERS(block = Block009FlowerThreePink()),
    BLOCK_010_SMALL_LAWN(block = Block010Lawn()),
    BLOCK_011_THREE_YELLOW_FLOWERS(block = Block011FlowerThreeYellow()),
    BLOCK_012_UNDER_WATER_SOIL(block = Block012UnderWaterSoil()),
    BLOCK_013_ONE_RED_FLOWER(block = Block013FlowerOneRed()),
    BLOCK_014_ONE_YELLOW_FLOWER(block = Block014FlowerOneYellow()),
    BLOCK_015_BUSH(block = Block015Bush());

    companion object {

        val fullCubeVertices: FloatArray = floatArrayOf(
            // XYZ                  RGB             UV          TEX INDEX
            +0.5f, +0.5f, +0.5f,    1f, 0f, 0f,     1f, 0f,     0f,     // 00 -
            -0.5f, +0.5f, +0.5f,    0f, 1f, 0f,     0f, 0f,     0f,     // 01 -
            -0.5f, -0.5f, +0.5f,    0f, 0f, 1f,     0f, 1f,     0f,     // 02 -
            +0.5f, -0.5f, +0.5f,    1f, 0f, 0f,     1f, 1f,     0f,     // 03 -

            -0.5f, +0.5f, -0.5f,    0f, 1f, 0f,     1f, 0f,     0f,     // 04 -
            +0.5f, +0.5f, -0.5f,    1f, 0f, 0f,     0f, 0f,     0f,     // 05 -
            +0.5f, -0.5f, -0.5f,    1f, 0f, 0f,     0f, 1f,     0f,     // 06 -
            -0.5f, -0.5f, -0.5f,    0f, 0f, 1f,     1f, 1f,     0f,     // 07 -

            +0.5f, +0.5f, -0.5f,    0f, 1f, 0f,     1f, 0f,     0f,     // 08 -
            +0.5f, +0.5f, +0.5f,    1f, 0f, 0f,     0f, 0f,     0f,     // 09 -
            +0.5f, -0.5f, +0.5f,    1f, 0f, 0f,     0f, 1f,     0f,     // 10 -
            +0.5f, -0.5f, -0.5f,    0f, 0f, 1f,     1f, 1f,     0f,     // 11 -

            -0.5f, +0.5f, +0.5f,    0f, 1f, 0f,     1f, 0f,     0f,     // 12 -
            -0.5f, +0.5f, -0.5f,    1f, 0f, 0f,     0f, 0f,     0f,     // 13 -
            -0.5f, -0.5f, -0.5f,    1f, 0f, 0f,     0f, 1f,     0f,     // 14 -
            -0.5f, -0.5f, +0.5f,    0f, 0f, 1f,     1f, 1f,     0f,     // 15 -

            +0.5f, +0.5f, -0.5f,    0f, 1f, 0f,     1f, 0f,     0f,     // 16 -
            -0.5f, +0.5f, -0.5f,    1f, 0f, 0f,     0f, 0f,     0f,     // 17 -
            -0.5f, +0.5f, +0.5f,    1f, 0f, 0f,     0f, 1f,     0f,     // 18 -
            +0.5f, +0.5f, +0.5f,    0f, 0f, 1f,     1f, 1f,     0f,     // 19 -

            +0.5f, -0.5f, +0.5f,    0f, 1f, 0f,     1f, 0f,     0f,     // 20 -
            -0.5f, -0.5f, +0.5f,    1f, 0f, 0f,     0f, 0f,     0f,     // 21 -
            -0.5f, -0.5f, -0.5f,    1f, 0f, 0f,     0f, 1f,     0f,     // 22 -
            +0.5f, -0.5f, -0.5f,    0f, 0f, 1f,     1f, 1f,     0f,     // 23 -
        )

        val fullCubeIndices: IntArray = intArrayOf(
            0, 1, 2, 2, 3, 0,
            4, 5, 6, 6, 7, 4,
            8, 9, 10, 10, 11, 8,
            12, 13, 14, 14, 15, 12,
            16, 17, 18, 18, 19, 16,
            20, 21, 22, 22, 23, 20
        )

        val fullFaceIndices: IntArray = intArrayOf(
            0, 1, 2, 2, 3, 0,
        )

        val faceFront: FloatArray = floatArrayOf(
            +0.5f, +0.5f, +0.5f,    1f, 0f, 0f,     1f, 0f,     1f,
            -0.5f, +0.5f, +0.5f,    0f, 1f, 0f,     0f, 0f,     1f,
            -0.5f, -0.5f, +0.5f,    0f, 0f, 1f,     0f, 1f,     1f,
            +0.5f, -0.5f, +0.5f,    1f, 0f, 0f,     1f, 1f,     1f,
        )

        val faceBack: FloatArray = floatArrayOf(
            -0.5f, +0.5f, -0.5f,    0f, 1f, 0f,     1f, 0f,     1f,
            +0.5f, +0.5f, -0.5f,    1f, 0f, 0f,     0f, 0f,     1f,
            +0.5f, -0.5f, -0.5f,    1f, 0f, 0f,     0f, 1f,     1f,
            -0.5f, -0.5f, -0.5f,    0f, 0f, 1f,     1f, 1f,     1f,
        )

        val faceRight: FloatArray = floatArrayOf(
            +0.5f, +0.5f, -0.5f,    0f, 1f, 0f,     1f, 0f,     1f,
            +0.5f, +0.5f, +0.5f,    1f, 0f, 0f,     0f, 0f,     1f,
            +0.5f, -0.5f, +0.5f,    1f, 0f, 0f,     0f, 1f,     1f,
            +0.5f, -0.5f, -0.5f,    0f, 0f, 1f,     1f, 1f,     1f,
        )

        val faceLeft: FloatArray = floatArrayOf(
            -0.5f, +0.5f, +0.5f,    0f, 1f, 0f,     1f, 0f,     1f,
            -0.5f, +0.5f, -0.5f,    1f, 0f, 0f,     0f, 0f,     1f,
            -0.5f, -0.5f, -0.5f,    1f, 0f, 0f,     0f, 1f,     1f,
            -0.5f, -0.5f, +0.5f,    0f, 0f, 1f,     1f, 1f,     1f,
        )

        val faceTop: FloatArray = floatArrayOf(
            +0.5f, +0.5f, -0.5f,    0f, 1f, 0f,     1f, 0f,     1f,
            -0.5f, +0.5f, -0.5f,    1f, 0f, 0f,     0f, 0f,     1f,
            -0.5f, +0.5f, +0.5f,    1f, 0f, 0f,     0f, 1f,     1f,
            +0.5f, +0.5f, +0.5f,    0f, 0f, 1f,     1f, 1f,     1f,
        )

        val faceBottom: FloatArray = floatArrayOf(
            +0.5f, -0.5f, +0.5f,    0f, 1f, 0f,     1f, 0f,     1f,
            -0.5f, -0.5f, +0.5f,    1f, 0f, 0f,     0f, 0f,     1f,
            -0.5f, -0.5f, -0.5f,    1f, 0f, 0f,     0f, 1f,     1f,
            +0.5f, -0.5f, -0.5f,    0f, 0f, 1f,     1f, 1f,     1f,
        )

        /** Sequência de faces para construção de um simple cube na ordem:
        faceFront, faceBack, faceRight, faceLeft, faceTop, faceBottom. */
        val cubeFaces: Array<FloatArray> = arrayOf(faceFront, faceBack, faceRight,
            faceLeft, faceTop, faceBottom)

        val solidBlocks: Array<Short> = BlockManager.entries.filter {
            it.block.isSolid
        }.map {
            it.ordinal.toShort()
        }.toTypedArray()

        val transparentBlocks: Array<Short> = BlockManager.entries.filter {
            it.block.isTransparent
        }.map {
            it.ordinal.toShort()
        }.toTypedArray()

        val interactableBlocks: Array<Short> = BlockManager.entries.filter {
            it.block.isTransparent
        }.map {
            it.ordinal.toShort()
        }.toTypedArray()
    }
}