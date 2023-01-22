package com.minepalm.nations.bukkit.commands.user

import com.minepalm.arkarangutils.bukkit.BukkitExecutor
import com.minepalm.nations.Nation
import com.minepalm.nations.NationService
import com.minepalm.nations.OperationResult
import com.minepalm.nations.ResultCode
import com.minepalm.nations.bukkit.message.ResultMessage
import com.minepalm.nations.bukkit.message.ResultPrinter
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

                player.sendMessage(printer.build(resultMessage))

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