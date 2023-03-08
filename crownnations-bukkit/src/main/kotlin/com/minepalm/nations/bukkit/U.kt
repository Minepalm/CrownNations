package com.minepalm.nations.bukkit

import com.minepalm.arkarangutils.bukkit.ArkarangGUI
import com.minepalm.arkarangutils.bukkit.BukkitExecutor
import com.minepalm.nations.Dependencies
import com.minepalm.nations.utils.ServerLoc
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

internal object U /* tils */ {

    @JvmStatic
    val kyori = MiniMessage
        .builder()
        .tags(
            TagResolver.builder()
                .resolver(StandardTags.defaults())
                .build()
        ).build()

    val serverName: String
        get() = CrownNationsBukkit.inst.network.host.name

}

class ItemHandler(
    var material: Material = Material.AIR,
    var amount: Int = 1,
    var name: String = "",
    var lore: List<String> = listOf()
) {
    fun toItemStack(): ItemStack {
        val item = ItemStack(material, amount)
        val meta = item.itemMeta
        meta.displayName(U.kyori.deserialize(name))
        meta.lore(lore.map { U.kyori.deserialize(it) })
        item.itemMeta = meta
        return item
    }
}
fun item(func: ItemHandler.() -> Unit): ItemStack {
    val handler = ItemHandler()
    handler.func()
    return handler.toItemStack()
}

fun ArkarangGUI.openSync(player: Player) {
    Dependencies[BukkitExecutor::class].get().sync { this.openGUI(player) }
}

fun Location.convert(): ServerLoc {
    return ServerLoc(U.serverName, this.world.name, this.blockX, this.blockY, this.blockZ)
}

fun ServerLoc.convert(): Location {
    return Location(Bukkit.getWorld(this.world), this.x.toDouble(), this.y.toDouble(), this.z.toDouble())
}