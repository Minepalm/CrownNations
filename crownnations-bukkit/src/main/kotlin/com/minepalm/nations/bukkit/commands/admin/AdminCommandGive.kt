package com.minepalm.nations.bukkit.commands.admin

import com.minepalm.nations.config.NationConfigurations
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class AdminCommandGive(
    val config: NationConfigurations
){

    fun whenCommand(player: Player, type: String){
        when(type.lowercase()){
            "castle" -> {
                val itemName = config.territory.castleItemName
                player.inventory.addItem(ItemStack(Material.BEACON).apply {
                    itemMeta = itemMeta.apply {
                        displayName(Component.text(itemName))
                    }
                })
                player.sendMessage("지급완료.")
            }
            "outpost" -> {
                val itemName = config.territory.outpostItemName
                player.inventory.addItem(ItemStack(Material.NETHERRACK).apply {
                    itemMeta = itemMeta.apply {
                        displayName(Component.text(itemName))
                    }
                })
                player.sendMessage("지급완료.")
            }
            else -> {
                player.sendMessage("/국가관리 지급 castle|outpost - 아이템을 지급받습니다.")
            }
        }
    }
}