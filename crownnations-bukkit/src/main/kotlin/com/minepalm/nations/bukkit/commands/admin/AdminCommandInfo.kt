package com.minepalm.nations.bukkit.commands.admin

import com.minepalm.nations.NationService
import org.bukkit.entity.Player

class AdminCommandInfo(private val service: NationService) {

    fun whenCommand(player: Player){
        player.sendMessage("현재 캐싱된 국가: "+(service.memberRegistry[player.uniqueId].cache.getNation()?.name ?: "없음"))
        player.sendMessage("현재 국가: "+(service.memberRegistry[player.uniqueId].direct.getNation().join()?.name ?: "없음"))
    }
}