package com.minepalm.nations.bukkit.listener.bukkit

import com.minepalm.nations.bukkit.PlayerCache
import com.minepalm.nations.bukkit.PlayerLoader
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.bukkit.event.player.PlayerQuitEvent

class BukkitGeneralListener(
    private val loader: PlayerLoader,
    private val cache: PlayerCache
) : Listener{

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    fun onPlayerJoin(event : AsyncPlayerPreLoginEvent){
        loader.onLogin(event.uniqueId)
        event.playerProfile.name?.let { cache.register(event.uniqueId, it) }
    }

    @EventHandler
    fun onPlayerLeft(event : PlayerQuitEvent){
        loader.onLeft(event.player.uniqueId)
        cache.invalidate(event.player.uniqueId, event.player.name)
    }
}