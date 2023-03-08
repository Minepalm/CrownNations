package com.minepalm.nations.bukkit.gui.data

import org.bukkit.Material

data class IconData(
    val material: Material,
    val display: String,
    val lore: List<String>
) {

    fun toIcon(): Icon {
        return Icon(material, display, lore)
    }

}
