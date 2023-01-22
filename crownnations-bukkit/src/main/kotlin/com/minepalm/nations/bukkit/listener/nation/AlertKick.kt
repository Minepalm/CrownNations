package com.minepalm.nations.bukkit.listener.nation

import com.minepalm.nations.NationEventListener
import com.minepalm.nations.NationService
import com.minepalm.nations.bukkit.NetworkBroadcaster
import com.minepalm.nations.bukkit.PlayerCache
import com.minepalm.nations.bukkit.message.ResultMessage
import com.minepalm.nations.bukkit.message.ResultPrinter

class AlertKick(
    private val service: NationService,
    private val players: PlayerCache,
    private val printer: ResultPrinter,
    private val broadcaster: NetworkBroadcaster
) : NationEventListener<com.minepalm.nations.event.NationRemoveMemberEvent> {
    override fun onEvent(event: com.minepalm.nations.event.NationRemoveMemberEvent) {
        if(event.reason == "KICK"){
            service.nationRegistry[event.nationId]?.let {
                val alertMessage = printer.build(ResultMessage("ALERT_KICK").apply {
                    set("player", players.username(event.commander)!!)
                    set("target", players.username(event.removerId)!!)
                    set("nation", it.name)
                })
                broadcaster.broadcast(event.nationId, alertMessage)
            }
        }
    }
}