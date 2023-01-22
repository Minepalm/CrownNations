package com.minepalm.nations.bukkit.commands.admin

import com.minepalm.arkarangutils.bukkit.BukkitExecutor
import com.minepalm.nations.NationService
import com.minepalm.nations.ResultCode
import com.minepalm.nations.bukkit.PlayerCache
import org.bukkit.entity.Player

class AdminCommandKick(
    private val service : NationService,
    private val players: PlayerCache,
    private val executor : BukkitExecutor
) {

    fun whenCommand(player: Player, nationName: String, username: String){
        executor.async {
            var code = ""
            val commander = service.memberRegistry[player.uniqueId]

            if(!commander.cache.isAdmin()){
                player.sendMessage("당신은 관리자가 아닙니다.")
                return@async
            }

            val uuid = players.uuid(username)
            val nation = service.nationRegistry[nationName]

            if(uuid == null){
                code = ResultCode.PLAYER_NOT_EXISTS
                player.sendMessage("명령어 실행 결과: $code")
                return@async
            }

            if(nation == null){
                code = ResultCode.NATION_NOT_EXISTS
                player.sendMessage("명령어 실행 결과: $code")
                return@async
            }

            code = nation.operateKickMember(commander, uuid).process().code
            player.sendMessage("명령어 실행 결과: $code")
        }
    }
}