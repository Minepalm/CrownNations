package com.minepalm.nations.bukkit.warp

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent

class PlayerMarkingListener(
    private val marker: PlayerMarker
) : Listener {

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        if(marker.isMarked(event.player.uniqueId)) {
            marker.unmark(event.player.uniqueId)
        }
    }

    @EventHandler
    fun onMove(event: PlayerMoveEvent) {
        if(!event.isCancelled && marker.isMarked(event.player.uniqueId)) {
            marker.unmark(event.player.uniqueId)
        }
    }

    @EventHandler
    fun onInteract(event: PlayerInteractEvent) {

    }

    @EventHandler
    fun onDamaged(event: EntityDamageByEntityEvent) {

    }
}