package com.minepalm.nations.bukkit.commands.user

import com.minepalm.arkarangutils.bukkit.BukkitExecutor
import com.minepalm.nations.NationMember
import com.minepalm.nations.NationService
import com.minepalm.nations.OperationResult
import com.minepalm.nations.ResultCode
import com.minepalm.nations.bukkit.message.ResultMessage
import com.minepalm.nations.bukkit.message.ResultPrinter
import org.bukkit.entity.Player

class UserCommandWithdraw(
    private val service : NationService,
    private val printer : ResultPrinter,
    private val executor : BukkitExecutor
) {

    fun whenCommand(player: Player, amount: Double){
        executor.async{
            val result = execute(player, amount)

            if(result == null){
                player.sendMessage(printer["NO_NATION"])
                return@async
            }

            if(result.code != ResultCode.EVENT_CANCELLED) {

                val messageCode = when {
                    printer.containsMessage(result.code) -> result.code
                    else -> "ERROR"
                }

                val resultMessage = ResultMessage(messageCode, result).apply {
                    set("player", player.name)
                    set("nation", player.member().cache.nation?.name ?: "알수 없는 국가")
                    set("amount", "${amount.toInt()}")
                    set("after", "${result.result?.toInt()}")
                }

                player.sendMessage(printer.build(resultMessage))

            }
        }
    }

    fun execute(player: Player, amount: Double): OperationResult<Double>?{
        return player.member().let { it.cache.nation?.bank?.operateWithdraw(it, "COMMAND", amount)?.process() }
    }

    private fun Player.member(): NationMember {
        return service.memberRegistry[uniqueId]
    }
}