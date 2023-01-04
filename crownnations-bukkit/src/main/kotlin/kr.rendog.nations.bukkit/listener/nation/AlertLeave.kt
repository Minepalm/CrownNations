package kr.rendog.nations.bukkit.listener.nation

import kr.rendog.nations.NationEventListener
import kr.rendog.nations.NationService
import kr.rendog.nations.bukkit.NetworkBroadcaster
import kr.rendog.nations.bukkit.PlayerCache
import kr.rendog.nations.bukkit.message.ResultMessage
import kr.rendog.nations.bukkit.message.ResultPrinter
import kr.rendog.nations.event.NationRemoveMemberEvent

class AlertLeave(
    private val service: NationService,
    private val players: PlayerCache,
    private val printer: ResultPrinter,
    private val broadcaster: NetworkBroadcaster
) : NationEventListener<NationRemoveMemberEvent>{
    override fun onEvent(event: NationRemoveMemberEvent) {
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