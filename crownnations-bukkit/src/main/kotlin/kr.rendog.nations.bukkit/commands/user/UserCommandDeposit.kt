package kr.rendog.nations.bukkit.commands.user

import com.minepalm.arkarangutils.bukkit.BukkitExecutor
import kr.rendog.nations.NationMember
import kr.rendog.nations.NationService
import kr.rendog.nations.OperationResult
import kr.rendog.nations.ResultCode
import kr.rendog.nations.bukkit.message.ResultMessage
import kr.rendog.nations.bukkit.message.ResultPrinter
import kr.rendog.nations.bukkit.sendMessage
import org.bukkit.entity.Player

class UserCommandDeposit(
    private val service : NationService,
    private val printer : ResultPrinter,
    private val executor : BukkitExecutor
) {

    fun whenCommand(player: Player, amount: Double){
        executor.async{
            val result = execute(player, amount)

            if(result == null){
                player.sendMessage(printer["NO_NATION"].toTypedArray())
                return@async
            }

            if(result.code != ResultCode.EVENT_CANCELLED) {

                val messageCode = when {
                    printer.containsMessage(result.code) -> result.code
                    else -> "ERROR"
                }

                val resultMessage = ResultMessage(messageCode, result).apply {
                    set("player", player.name)
                    set("nation", player.member().cache.getNation()?.name ?: "알수 없는 국가")
                    set("amount", "${amount.toInt()}")
                    set("after", "${result.result?.toInt()}")
                }

                player.sendMessage(printer.build(resultMessage))

            }
        }
    }

    fun execute(player: Player, amount: Double): OperationResult<Double>?{
        return player.member().let { it.cache.getNation()?.bank?.operateDeposit(it, "COMMAND", amount)?.process() }
    }

    private fun Player.member(): NationMember {
        return service.memberRegistry[uniqueId]
    }
}