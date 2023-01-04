package kr.rendog.nations.bukkit.commands.admin

import com.minepalm.arkarangutils.bukkit.BukkitExecutor
import kr.rendog.nations.NationService
import kr.rendog.nations.ResultCode
import org.bukkit.entity.Player

class AdminCommandDisband(
    private val service: NationService,
    private val executor: BukkitExecutor
) {

    fun whenCommand(player: Player, nationName: String){
        executor.async {
            var code = ""
            val commander = service.memberRegistry[player.uniqueId]

            if(!commander.cache.isAdmin()){
                player.sendMessage("당신은 관리자가 아닙니다.")
                return@async
            }

            val nation = service.nationRegistry[nationName]


            if(nation == null){
                code = ResultCode.NATION_NOT_EXISTS
                player.sendMessage("명령어 실행 결과: $code")
                return@async
            }

            code = nation.operateDisband(commander).process().code
            player.sendMessage("명령어 실행 결과: $code")
        }
    }
}