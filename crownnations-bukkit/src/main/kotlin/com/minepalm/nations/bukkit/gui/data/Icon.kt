package com.minepalm.nations.bukkit.gui.data

import com.minepalm.nations.utils.TextMetadata
import org.bukkit.Material

data class Icon(
    val material: Material,
    val display: String,
    val lore: List<String>,
    val meta: MutableMap<String, String> = mutableMapOf()
) : TextMetadata {
    override fun get(key: String): String {
        return meta[key] ?: ""
    }

    override fun set(key: String, value: String) {
        meta[key] = value
    }

}
