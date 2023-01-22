package com.minepalm.nations.bukkit.listener.nation

import com.minepalm.nations.NationEventListener
import com.minepalm.nations.NationRank
import com.minepalm.nations.NationService
import com.minepalm.nations.bukkit.NetworkBroadcaster
import com.minepalm.nations.bukkit.PlayerCache
import com.minepalm.nations.bukkit.message.ResultMessage
import com.minepalm.nations.bukkit.message.ResultPrinter

class AlertSetRank(
    private val service: NationService,
    private val players: PlayerCache,
    private val printer: ResultPrinter,
    private val broadcaster: NetworkBroadcaster
) : NationEventListener<com.minepalm.nations.event.NationSetRankEvent> {

    override fun onEvent(event: com.minepalm.nations.event.NationSetRankEvent) {
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