package com.minepalm.nations.bukkit.commands

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Subcommand
import com.minepalm.nations.NationService
import org.bukkit.entity.Player

@CommandAlias("cnd")
@CommandPermission("crownnations.admin")
class DebugCommands(
    private val service: NationService
) : BaseCommand() {

    @Subcommand("worldinfo")
    fun printWorldInfo(player: Player) {
        val nationWorld = service.territoryService.universe.host[player.world.name]
        player.sendMessage("World: ${player.world.name}, ")
        nationWorld?.let { world ->
            world.local.getMonuments().forEach {
                player.sendMessage("Monument: ")
                player.sendMessage("  id: ${it.id}")
                player.sendMessage("  nation id: ${it.nationId}")
                player.sendMessage("  type: ${it.type}")
                player.sendMessage("  range: ${it.range}")
                player.sendMessage("  center location: ${it.center}")
            }
            player.sendMessage("found monument count : ${world.local.getMonuments().size}")
        } ?: {
            player.sendMessage("not found nation world")
        }
    }


}