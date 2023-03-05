package com.minepalm.nations.bukkit.commands.user

import com.minepalm.arkarangutils.bukkit.BukkitExecutor
import com.minepalm.nations.Nation
import com.minepalm.nations.NationService
import com.minepalm.nations.OperationResult
import com.minepalm.nations.ResultCode
import com.minepalm.nations.bukkit.PlayerCache
import com.minepalm.nations.bukkit.message.ResultMessage
import com.minepalm.nations.bukkit.message.ResultPrinter
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.CompletableFuture

class UserCommandTransfer(
    val service: NationService,
    val players: PlayerCache,
    val printer: ResultPrinter,
    val executor: BukkitExecutor
) {

    fun whenCommand(player: Player, transferTo: String){
        executor.async {
            val uuid = players.uuid(transferTo)
            val nationFuture = service.memberRegistry[player.uniqueId].direct.getNation()
            val result = execute(player, transferTo, uuid, nationFuture)

            val messageCode = when {
                printer.containsMessage(result.code) -> result.code
                else -> "ERROR"
            }

            val resultMessage = ResultMessage(messageCode, result).apply {
                set("player", player.name)
                set("nation", nationFuture.join()?.name ?: "")
                set("target", transferTo)
            }

            player.sendMessage(printer.build(resultMessage))

        }
    }

    fun execute(player: Player, transferTo: String, uuid: UUID?, nationFuture: CompletableFuture<Nation?>)
    : OperationResult<Boolean> {
        if(uuid == null){
            return OperationResult(ResultCode.PLAYER_NOT_EXISTS, false)
        }

        if (!players.isOnline(transferTo)) {
            return OperationResult(ResultCode.PLAYER_NOT_ONLINE, false)
        }

        val nation = nationFuture.join() ?: return OperationResult(ResultCode.NATION_NOT_EXISTS, false)

        return nation.operateTransferOwner(service.memberRegistry[player.uniqueId], uuid).process()
    }

}