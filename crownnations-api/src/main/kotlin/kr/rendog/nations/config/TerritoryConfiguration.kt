package kr.rendog.nations.config

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
}