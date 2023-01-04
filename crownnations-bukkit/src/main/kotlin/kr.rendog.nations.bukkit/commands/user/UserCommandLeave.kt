package kr.rendog.nations.bukkit.commands.user

import com.minepalm.arkarangutils.bukkit.BukkitExecutor
import kr.rendog.nations.NationService
import kr.rendog.nations.ResultCode
import kr.rendog.nations.bukkit.message.ResultMessage
import kr.rendog.nations.bukkit.message.ResultPrinter
import kr.rendog.nations.bukkit.sendMessage
import org.bukkit.entity.Player

//todo: Success leave 랑 success kick 메세지 구분시켜두기
class UserCommandLeave(
    private val service: NationService,
    private val printer: ResultPrinter,
    private val executor: BukkitExecutor
) {

    fun whenCommand(player: Player){
        executor.async {
            val member = service.memberRegistry[player.uniqueId]
            val nation = member.direct.getNation().join()
            val result = member.operateLeaveNation().process()

            if(result.code != ResultCode.EVENT_CANCELLED) {

                val messageCode = when {
                    result.code == "SUCCESSFUL" -> "SUCCESSFUL_LEAVE"
                    printer.containsMessage(result.code) -> result.code
                    else -> "ERROR"
                }

                val resultMessage = ResultMessage(messageCode, result).apply {
                    set("player", player.name)
                    set("nation", nation?.name ?: "")
                }

                player.sendMessage(printer.build(resultMessage).toTypedArray())

            }
        }
    }

}