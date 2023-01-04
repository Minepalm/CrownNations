package kr.rendog.nations.bukkit.listener.nation

import kr.rendog.nations.NationEventListener
import kr.rendog.nations.NationRank
import kr.rendog.nations.NationService
import kr.rendog.nations.bukkit.NetworkBroadcaster
import kr.rendog.nations.bukkit.PlayerCache
import kr.rendog.nations.bukkit.message.ResultMessage
import kr.rendog.nations.bukkit.message.ResultPrinter
import kr.rendog.nations.event.NationSetRankEvent

class AlertSetRank(
    private val service: NationService,
    private val players: PlayerCache,
    private val printer: ResultPrinter,
    private val broadcaster: NetworkBroadcaster
) : NationEventListener<NationSetRankEvent>{

    override fun onEvent(event: NationSetRankEvent) {
        try {
            service.nationRegistry[event.nationId]?.let {
                val alertMessage = printer.build(ResultMessage("ALERT").apply {
                    set("player", players.username(event.commander)!!)
                    set("target", players.username(event.user)!!)
                    set("rank", parse(event.rank))
                    set("nation", it.name)
                })
                broadcaster.broadcast(event.nationId, alertMessage)
            }
        }catch (_: IllegalArgumentException){
            //skip
        }
    }

    private fun parse(rank: NationRank) : String{
        return when(rank){
            NationRank.OWNER -> "왕"
            NationRank.OFFICER -> "부왕"
            NationRank.RESIDENT -> "시민"
            else -> throw IllegalArgumentException()
        }
    }

}