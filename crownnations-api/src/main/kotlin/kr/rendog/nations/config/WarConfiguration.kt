package kr.rendog.nations.config

import kr.rendog.nations.war.WarTime

interface WarConfiguration {

    val defaultRating: Int

    val prepareSeconds: Int

    val warSeconds: Int

    val maximumWarSessions: Int

    val eloConstant: Int
    val eloWeight: Int
    val eloBase: Int

    val castleMaxHealth: Int

    fun isSubscribed(serverName: String): Boolean

    fun isHandling(serverName: String): Boolean

    fun generateWarTime(time: Long): WarTime
}