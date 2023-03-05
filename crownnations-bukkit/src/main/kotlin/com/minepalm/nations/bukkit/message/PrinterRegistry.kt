package com.minepalm.nations.bukkit.message

import java.util.concurrent.ConcurrentHashMap

class PrinterRegistry {

    private val map = ConcurrentHashMap<String, ResultPrinter>()
    private val replacements = ConcurrentHashMap<String, Replace>()

    init{
        replacements["%player%"] = Placeholders.PLAYER_NAME
        replacements["%nation%"] = Placeholders.NATION_NAME
        replacements["%target%"] = Placeholders.TARGET_NAME
        replacements["%rank%"] = Placeholders.RANK
        replacements["%error%"] = Placeholders.ERROR
        replacements["%members%"] = Placeholders.NATION_MEMBERS
        replacements["%owner%"] = Placeholders.NATION_OWNER
        replacements["%officers%"] = Placeholders.NATION_OFFICERS
        replacements["%money%"] = Placeholders.MONEY
        replacements["%amount%"] = Placeholders.AMOUNT
    }

    fun register(provider: ResultPrinter){
        map[provider.tag] = provider.apply { replacements.values.forEach { registerReplacement(it) } }
    }

    fun replacements(): List<Replace>{
        return replacements.values.toList()
    }

    operator fun get(tag: String): ResultPrinter {
        return map[tag] as ResultPrinter
    }
}