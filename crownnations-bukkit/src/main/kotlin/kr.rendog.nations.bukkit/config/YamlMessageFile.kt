package kr.rendog.nations.bukkit.config

import com.minepalm.arkarangutils.bukkit.SimpleConfig
import kr.rendog.nations.bukkit.message.ResultPrinter
import org.bukkit.configuration.InvalidConfigurationException
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

class YamlMessageFile(plugin: JavaPlugin) : SimpleConfig(plugin, "messages.yml"){

    fun read(): Map<String, ResultPrinter>{
        return mutableMapOf<String, ResultPrinter>().apply {
            config.getKeys(false).forEach { put(it, readSection(it)) }
        }
    }

    private fun readSection(key: String): ResultPrinter{
        val subSection = config.getConfigurationSection(key)!!
        return ResultPrinter(key).apply {
            subSection.getKeys(false).forEach { registerText(it, subSection.getStringList(it).let {
                mutableListOf<String>().apply {
                    it.forEach { str-> this.add(str.replace("&", "§")) }
                }
            }) }
        }
    }

}