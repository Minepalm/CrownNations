package kr.rendog.nations.bukkit.commands.user

import com.minepalm.arkarangutils.bukkit.BukkitExecutor
import kr.rendog.nations.Nation
import kr.rendog.nations.NationService
import kr.rendog.nations.OperationResult
import kr.rendog.nations.ResultCode
import kr.rendog.nations.bukkit.message.ResultMessage
import kr.rendog.nations.bukkit.message.ResultPrinter
import kr.rendog.nations.bukkit.sendMessage
import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture

class UserCommandDisband(
    private val service : NationService,
    private val printer : ResultPrinter,
    private val executor : BukkitExecutor
) {

    fun whenCommand(player: Player){
        executor.async {
            val nationFuture = service.memberRegistry[player.uniqueId].direct.getNation()
            val result = execute(player, nationFuture)

            if(result.code != ResultCode.EVENT_CANCELLED) {

                val messageCode = when {
                    printer.containsMessage(result.code) -> result.code
                    else -> "ERROR"
                }

                val resultMessage = ResultMessage(messageCode, result).apply {
                    set("player", player.name)
                    set("nation", nationFuture.join()?.name ?: "")
                }

                player.sendMessage(printer.build(resultMessage).toTypedArray())

            }
        }
    }

    private fun execute(player: Player, nationFuture: CompletableFuture<Nation?>)
            : OperationResult<Boolean> {
        val nation = nationFuture.join()

        if(nation == null){
            return OperationResult(ResultCode.NATION_NOT_EXISTS, false)
        }

        return nation.operateDisband(service.memberRegistry[player.uniqueId]).process()
    }

}