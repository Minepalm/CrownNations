package kr.rendog.nations.bukkit.listener.nation

import kr.rendog.nations.NationEventListener
import kr.rendog.nations.NationService
import kr.rendog.nations.bukkit.NetworkBroadcaster
import kr.rendog.nations.bukkit.PlayerCache
import kr.rendog.nations.bukkit.message.ResultMessage
import kr.rendog.nations.bukkit.message.ResultPrinter
import kr.rendog.nations.event.NationTransferEvent

class AlertTransfer(
    private val service: NationService,
    private val players: PlayerCache,
    private val printer: ResultPrinter,
    private val broadcaster: NetworkBroadcaster
) : NationEventListener<NationTransferEvent>{
    override fun onEvent(event: NationTransferEvent) {
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