package com.minepalm.nations.bukkit.config

import com.minepalm.nations.bukkit.gui.data.IconData

class IconRepository {

    private val icons = mutableMapOf<String, IconData>()

    fun register(name: String, icon: IconData) {
        icons[name] = icon
    }

    fun getIcon(name: String): IconData? {
        return icons[name]
    }

    fun getIcons(): Map<String, IconData> {
        return icons
    }
}