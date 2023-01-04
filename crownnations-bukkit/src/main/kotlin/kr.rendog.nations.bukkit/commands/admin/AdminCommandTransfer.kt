package kr.rendog.nations.bukkit.commands.admin

import com.minepalm.arkarangutils.bukkit.BukkitExecutor
import kr.rendog.nations.NationService
import kr.rendog.nations.ResultCode
import kr.rendog.nations.bukkit.PlayerCache
import org.bukkit.entity.Player

class AdminCommandTransfer(
    private val service : NationService,
    private val players: PlayerCache,
    private val executor : BukkitExecutor
) {

    fun whenCommand(player: Player, nationName: String, transferTo: String){
        executor.async {
            var code = ""
            val commander = service.memberRegistry[player.uniqueId]

            if(!commander.cache.isAdmin()){
                player.sendMessage("당신은 관리자가 아닙니다.")
                return@async
            }

            val uuid = players.uuid(transferTo)
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

            code = nation.operateTransferOwner(commander, uuid).process().code
            player.sendMessage("명령어 실행 결과: $code")
        }
    }
}