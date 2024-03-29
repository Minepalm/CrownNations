package com.minepalm.nations.bungee

import com.google.common.collect.HashBiMap
import com.minepalm.arkarangutils.bungee.BungeeConfig
import com.minepalm.nations.NationRank
import com.minepalm.nations.config.GradeConfiguration
import com.minepalm.nations.config.MemberConfiguration
import com.minepalm.nations.config.TerritoryConfiguration
import com.minepalm.nations.config.WarpConfiguration
import com.minepalm.nations.utils.DeleteRange
import com.minepalm.nations.utils.SchematicOffset
import com.minepalm.nations.utils.WarpOffset
import net.md_5.bungee.api.plugin.Plugin
import net.md_5.bungee.config.Configuration
import java.util.*

class BungeeYamlConfiguration(plugin: Plugin)
    : BungeeConfig(plugin, "config.yml", true), com.minepalm.nations.config.NationConfigurations {

    override val grade: GradeConfiguration = Grade(config.getSection("grade"))
    override val member: MemberConfiguration = Member(config.getSection("member"))
    override val territory: TerritoryConfiguration = Territory(config.getSection("territory"))

    override fun mysql(name: String): String {
        return config.getString("dataSource.$name")
    }

    class Territory(
        private val config: Configuration,
    ) : TerritoryConfiguration {

        override val worlds: List<String> = mutableListOf()

        //config.getStringList("worlds")
        override val maximumCastleCount: Int = config.getInt("maximumCastleCount", 3)
        override val maximumOutpostCount: Int = config.getInt("maximumOutpostCount", 5)
        override val nearestDistanceCastleToClaim: Double = config.getDouble("nearestDistanceCastleToClaim", 150.0)
        override val nearestDistanceOutpostToClaim: Double = config.getDouble("nearestDistanceOutpostToClaim", 30.0)
        override val maximumHeight = config.getInt("maximumHeight")
        override val castleLength: Int = config.getInt("castleLength", 51)
        override val outpostLength: Int = config.getInt("outpostLength", 5)
        override val castleItemName: String = config.getString("castleItemName").replace("&", "§")
        override val outpostItemName: String = config.getString("outpostItemName").replace("&", "§")

        override fun getSchematic(type: String): String {
            throw IllegalAccessException("Proxy cannot get schematic")
        }

        override fun getSchematicOffset(type: String): SchematicOffset {
            throw IllegalAccessException("Proxy cannot get monument offset")
        }

        override fun getDeleteRange(type: String): DeleteRange {
            throw IllegalAccessException("Proxy cannot get monument delete range")
        }

        override val warp: WarpConfiguration = object : WarpConfiguration {
            override fun getDefaultMonumentOffset(monumentType: String): WarpOffset {
                return config.getSection("warp.defaultOffset.${monumentType.lowercase(Locale.getDefault())}")?.let {
                    WarpOffset(it.getInt("x", 0), it.getInt("y", 0), it.getInt("z", 0))
                } ?: WarpOffset(0, 0, 0)
            }

            override fun getWarpDelay(): Int {
                return config.getInt("warp.delay", 3)
            }

        }
    }

    class Member(
        private val config: Configuration
    ) : com.minepalm.nations.config.MemberConfiguration {

        val map = HashBiMap.create<NationRank, String>()

        init {
            map[NationRank.OWNER] = config.getString("display.OWNER").replace("&", "§")
            map[NationRank.OFFICER] = config.getString("display.OFFICER").replace("&", "§")
            map[NationRank.RESIDENT] = config.getString("display.RESIDENT").replace("&", "§")
            map[NationRank.SENIOR] = config.getString("display.SENIOR").replace("&", "§")
        }

        override val regex: Regex = Regex(config.getString("regexFilter"))

        override val displayMaxLength = config.getInt("displayMaxLength")

        override val maximumMember: Int = config.getInt("maximumMember")

        override val bannedKeywords: List<String> = config.getStringList("bannedKeywords")

        override fun getMaximumMember(rank: NationRank): Int {
            if (rank == NationRank.RESIDENT) {
                return maximumMember
            } else
                return config.getInt("maximumRankMember.$rank", 0)
        }

        override fun getRankDisplay(rank: NationRank): String {
            return map[rank] ?: rank.name
        }

        override fun getRankByDisplay(str: String): NationRank? {
            return map.inverse()[str]
        }

    }

    class Grade(
        private val config: Configuration
    ) : com.minepalm.nations.config.GradeConfiguration {

        override fun getDisplay(level: Int): String {
            return config.getString("$level").replace("&", "§")
        }

    }
}