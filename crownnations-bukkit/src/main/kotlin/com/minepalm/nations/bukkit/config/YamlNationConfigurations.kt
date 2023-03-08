package com.minepalm.nations.bukkit.config

import com.google.common.collect.HashBiMap
import com.minepalm.arkarangutils.bukkit.SimpleConfig
import com.minepalm.nations.NationRank
import com.minepalm.nations.config.*
import com.minepalm.nations.utils.DeleteRange
import com.minepalm.nations.utils.SchematicOffset
import com.minepalm.nations.utils.WarpOffset
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

class YamlNationConfigurations(
    plugin: JavaPlugin,
) : SimpleConfig(plugin, "config.yml"), NationConfigurations {

    override val grade: GradeConfiguration = Grade(config.getConfigurationSection("grade")!!)
    override val member: MemberConfiguration = Member(config.getConfigurationSection("member")!!)
    override val territory: TerritoryConfiguration = Territory(config.getConfigurationSection("territory")!!)

    override fun mysql(name: String): String {
        return config.getString("dataSource.$name")!!
    }

    class Territory(
        private val config: ConfigurationSection,
    ) : TerritoryConfiguration {

        override val worlds: List<String> = config.getStringList("worlds")
        override val maximumCastleCount: Int = config.getInt("maximumCastleCount", 3)
        override val maximumOutpostCount: Int = config.getInt("maximumOutpostCount", 5)
        override val nearestDistanceCastleToClaim: Double = config.getDouble("nearestDistanceCastleToClaim", 150.0)
        override val nearestDistanceOutpostToClaim: Double = config.getDouble("nearestDistanceOutpostToClaim", 30.0)
        override val maximumHeight = config.getInt("maximumHeight")
        override val castleLength: Int = config.getInt("castleLength", 100)
        override val outpostLength: Int = config.getInt("outpostLength", 5)
        override val castleItemName: String = config.getString("castleItemName")!!.replace("&", "§")
        override val outpostItemName: String = config.getString("outpostItemName")!!.replace("&", "§")

        override fun getSchematic(type: String): String {
            return config.getString(
                "schematic.${type.uppercase(Locale.getDefault())}",
                "${type.lowercase(Locale.getDefault())}.schem"
            )!!
        }

        override fun getSchematicOffset(type: String): SchematicOffset {
            val section = config.getConfigurationSection("schematicOffset.${type.lowercase(Locale.getDefault())}")
            return SchematicOffset(section!!.getInt("x", 0), section.getInt("y", 0), section.getInt("z", 0))
        }

        override fun getDeleteRange(type: String): DeleteRange {
            val section = config.getConfigurationSection("deleteRange.${type.lowercase(Locale.getDefault())}")
            return DeleteRange(
                section!!.getInt("weightX", 0),
                section.getInt("lengthZ", 0),
                section.getInt("height", 0),
                section.getInt("depth", 0)
            )
        }

        override val warp: WarpConfiguration = object : WarpConfiguration {
                override fun getDefaultMonumentOffset(monumentType: String): WarpOffset {
                    return config.getConfigurationSection("warp.defaultOffset.${monumentType.lowercase(Locale.getDefault())}")?.let {
                        WarpOffset(it.getInt("x", 0), it.getInt("y", 0), it.getInt("z", 0))
                    } ?: WarpOffset(0, 0, 0)
                }

                override fun getWarpDelay(): Int {
                    return config.getInt("warp.delay", 3)
                }

            }
    }

    class Member(
        private val config: ConfigurationSection
    ) : MemberConfiguration {

        val map = HashBiMap.create<NationRank, String>()

        init {
            map[NationRank.OWNER] = config.getString("display.OWNER")!!.replace("&", "§")
            map[NationRank.OFFICER] = config.getString("display.OFFICER")!!.replace("&", "§")
            map[NationRank.RESIDENT] = config.getString("display.RESIDENT")!!.replace("&", "§")
            map[NationRank.SENIOR] = config.getString("display.SENIOR")!!.replace("&", "§")
        }

        override val regex: Regex = Regex(config.getString("regexFilter")!!)

        override val displayMaxLength = config.getInt("displayMaxLength")

        override val maximumMember: Int = config.getInt("maximumMember")

        override val bannedKeywords: List<String> = config.getStringList("bannedKeywords")

        override fun getMaximumMember(rank: NationRank): Int {
            if(rank == NationRank.RESIDENT){
                return maximumMember
            }else
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
        private val config: ConfigurationSection
    ) : GradeConfiguration {

        override fun getDisplay(level: Int): String {
            return config.getString("$level")!!.replace("&", "§")
        }

    }
}