package com.minepalm.nations.bukkit.listener.nation

import com.minepalm.nations.NationEventListener
import com.minepalm.nations.NationService
import com.minepalm.nations.bukkit.NetworkBroadcaster
import com.minepalm.nations.bukkit.PlayerCache
import com.minepalm.nations.bukkit.message.ResultMessage
import com.minepalm.nations.bukkit.message.ResultPrinter

class AlertTransfer(
    private val service: NationService,
    private val players: PlayerCache,
    private val printer: ResultPrinter,
    private val broadcaster: NetworkBroadcaster
) : NationEventListener<com.minepalm.nations.event.NationTransferEvent> {
    override fun onEvent(event: com.minepalm.nations.event.NationTransferEvent) {
        service.nationRegistry[event.nationId]?.let {
            val alertMessage = printer.build(ResultMessage("ALERT").apply {
                set("player", players.username(event.transferFrom)!!)
                set("target", players.username(event.transferTo)!!)
                set("nation", it.name)
            })
            broadcaster.broadcast(event.nationId, alertMessage)
        }
    }

}