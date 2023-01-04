package kr.rendog.nations.bukkit.commands.admin

import com.minepalm.arkarangutils.bukkit.BukkitExecutor
import kr.rendog.nations.NationRank
import kr.rendog.nations.NationService
import kr.rendog.nations.ResultCode
import kr.rendog.nations.bukkit.PlayerCache
import org.bukkit.entity.Player

class AdminCommandSetRank(
    private val service : NationService,
    private val players: PlayerCache,
    private val executor : BukkitExecutor
) {

    fun whenCommand(player: Player, nationName: String, username: String, rankName: String){
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

            val rank = try{
                parse(rankName)
            }catch (_: IllegalArgumentException){
                code = ResultCode.INVALID_RANK_NAME
                player.sendMessage("명령어 실행 결과: $code")
                return@async
            }

            code = nation.operateSetMember(commander, uuid, rank).process().code
            player.sendMessage("명령어 실행 결과: $code")
        }
    }

    private fun parse(name: String): NationRank {
        return when(name){
            "왕" -> NationRank.OWNER
            "부왕" -> NationRank.OFFICER
            "기사단장" -> NationRank.SENIOR
            "시민" -> NationRank.RESIDENT
            else -> throw IllegalArgumentException()
        }
    }
}