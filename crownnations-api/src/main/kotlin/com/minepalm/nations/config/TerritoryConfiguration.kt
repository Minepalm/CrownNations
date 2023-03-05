package com.minepalm.nations.config

import com.minepalm.nations.utils.DeleteRange
import com.minepalm.nations.utils.SchematicOffset

interface TerritoryConfiguration {

    val worlds: List<String>

    val maximumCastleCount: Int

    val maximumOutpostCount: Int

    val nearestDistanceCastleToClaim: Double

    val nearestDistanceOutpostToClaim: Double

    val maximumHeight: Int

    val castleLength: Int

    val outpostLength: Int

    val castleItemName: String

    val outpostItemName: String

    fun getSchematic(type: String): String

    fun getSchematicOffset(type: String): SchematicOffset

    fun getDeleteRange(type: String): DeleteRange
}