package com.minepalm.nations.bukkit.gui

import com.minepalm.nations.bukkit.U.kyori
import com.minepalm.nations.bukkit.gui.data.Icon
import com.minepalm.nations.bukkit.message.PrinterRegistry
import com.minepalm.nations.utils.TextMetadata
import net.kyori.adventure.text.Component
import org.bukkit.inventory.ItemStack

class IconFactory(
    private val repo: PrinterRegistry
) {

    fun buildIcon(icon: Icon): ItemStack {
        val item = ItemStack(icon.material)
        val meta = item.itemMeta
        meta.displayName(icon.display.map(icon))
        meta.lore(icon.lore.map { it.map(icon) })
        item.itemMeta = meta
        return item
    }

    private fun String.map(data: TextMetadata): Component {
        var str = this
        repo.replacements().forEach { str = it.replace(str, data) }
        return kyori.deserialize(str)
    }
}