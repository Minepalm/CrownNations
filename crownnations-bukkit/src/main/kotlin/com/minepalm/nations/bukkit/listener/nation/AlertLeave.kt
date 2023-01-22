package com.minepalm.nations.bukkit.listener.nation

import com.minepalm.nations.NationEventListener
import com.minepalm.nations.NationService
import com.minepalm.nations.bukkit.NetworkBroadcaster
import com.minepalm.nations.bukkit.PlayerCache
import com.minepalm.nations.bukkit.message.ResultMessage
import com.minepalm.nations.bukkit.message.ResultPrinter

class AlertLeave(
    private val service: NationService,
    private val players: PlayerCache,
    private val printer: ResultPrinter,
    private val broadcaster: NetworkBroadcaster
) : NationEventListener<com.minepalm.nations.event.NationRemoveMemberEvent> {
    override fun onEvent(event: com.minepalm.nations.event.NationRemoveMemberEvent) {
        if(event.reason == "LEAVE"){
            service.nationRegistry[event.nationId]?.let {
                val alertMessage = printer.build(ResultMessage("ALERT_LEAVE").apply {
                    set("player", players.username(event.commander)!!)
                    set("nation", it.name)
                })
                broadcaster.broadcast(event.nationId, alertMessage)
            }
        }
    }
}