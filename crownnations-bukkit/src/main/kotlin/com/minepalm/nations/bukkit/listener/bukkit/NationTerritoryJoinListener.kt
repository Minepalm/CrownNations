package com.minepalm.nations.bukkit.listener.bukkit

import com.minepalm.nations.bukkit.convert
import com.minepalm.nations.territory.NationTerritoryService
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent

class NationTerritoryJoinListener(
    val service: NationTerritoryService
) : Listener {

    @EventHandler
    fun onEvent(event: PlayerMoveEvent) {
        val player = event.player
        val from = event.from.convert()
        val to = event.to.convert()

        if (!service.universe.host.isInNationWorld(from))
            return

        val world = service.universe.host[event.to.convert()]
        val fromNation = world?.local?.get(from)
        val toNation = world?.local?.get(to)

        Bukkit.getLogger().info("world: ${world?.name}")
        Bukkit.getLogger().info("from: ${fromNation?.nationId}, to: ${toNation?.nationId}")

        if (fromNation == null) {
            if (toNation == null)
                return
            else
                player.sendMessage("You have entered ${toNation.owner.name}'s territory")
        } else {
            if (toNation == null)
                player.sendMessage("You have left ${fromNation.owner.name} nation's territory")
            else if (fromNation.id != toNation.id)
                player.sendMessage("You have entered ${toNation.owner.name}'s territory")
        }
    }
}