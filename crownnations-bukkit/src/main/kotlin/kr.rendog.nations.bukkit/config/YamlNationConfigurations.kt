package kr.rendog.nations.bukkit.config

import com.google.common.collect.HashBiMap
import com.minepalm.arkarangutils.bukkit.SimpleConfig
import kr.rendog.nations.NationRank
import kr.rendog.nations.config.GradeConfiguration
import kr.rendog.nations.config.MemberConfiguration
import kr.rendog.nations.config.NationConfigurations
import kr.rendog.nations.config.TerritoryConfiguration
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin

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
    }

    class Member(
        private val config: ConfigurationSection
    ) : MemberConfiguration {

        val map = HashBiMap.create<NationRank, String>()

        init{
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