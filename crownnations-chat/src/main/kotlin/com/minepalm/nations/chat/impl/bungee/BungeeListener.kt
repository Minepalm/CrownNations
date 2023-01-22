package kr.rendog.nations.chat.bungee

import com.minepalm.nations.chat.impl.CrownNationsChat
import net.md_5.bungee.api.event.PlayerDisconnectEvent
import net.md_5.bungee.api.event.ServerConnectEvent
import net.md_5.bungee.api.plugin.Listener

internal class BungeeListener(
    private val inst: CrownNationsChat
) : Listener {

    @net.md_5.bungee.event.EventHandler
    fun onPlayerJoin(event : ServerConnectEvent){
        if(event.reason == ServerConnectEvent.Reason.JOIN_PROXY) {

        }
    }

    @net.md_5.bungee.event.EventHandler
    fun onPlayerLeft(event : PlayerDisconnectEvent){

    }
}