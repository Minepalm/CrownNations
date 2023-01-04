package kr.rendog.nations.bukkit.listener.bukkit

import kr.rendog.nations.bukkit.PlayerLoader
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class BukkitGeneralListener(
    private val loader: PlayerLoader
) : Listener{

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    fun onPlayerJoin(event : AsyncPlayerPreLoginEvent){
        loader.onLogin(event.uniqueId)
    }

    @EventHandler
    fun onPlayerLeft(event : PlayerQuitEvent){
        loader.onLeft(event.player.uniqueId)
    }
}