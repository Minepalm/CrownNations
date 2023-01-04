package kr.rendog.nations.bungee

import net.md_5.bungee.api.event.PlayerDisconnectEvent
import net.md_5.bungee.api.event.ServerConnectEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler

class BungeeMemberListener(
    private val loader: PlayerLoader
) : Listener {


    @EventHandler
    fun onPlayerJoin(event : ServerConnectEvent){
        if(event.reason == ServerConnectEvent.Reason.JOIN_PROXY) {
            loader.onLogin(event.player.uniqueId)
        }
    }

    @EventHandler
    fun onPlayerLeft(event : PlayerDisconnectEvent){
        loader.onLeft(event.player.uniqueId)
    }
}