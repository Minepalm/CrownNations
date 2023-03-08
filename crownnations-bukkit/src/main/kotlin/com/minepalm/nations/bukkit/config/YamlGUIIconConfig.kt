package com.minepalm.nations.bukkit.config

import com.minepalm.arkarangutils.bukkit.SimpleConfig
import com.minepalm.nations.bukkit.gui.data.IconData
import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.plugin.java.JavaPlugin

class YamlGUIIconConfig(
    private val plugin: JavaPlugin
) : SimpleConfig(plugin, "gui-icons.yml"){


    fun read(): Map<String, IconData> {
        return mutableMapOf<String, IconData>().apply {
            config.getKeys(false).forEach { put(it, readSection(config.getConfigurationSection(it)!!)) }
        }
    }

    private fun readSection(section: ConfigurationSection): IconData{
        return IconData(
            Material.valueOf(section.getString("material")!!),
            section.getString("display")!!,
            section.getStringList("lore")
        )
    }
}