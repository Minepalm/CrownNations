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

class UserCommandKick(
    private val service: NationService,
    private val players: PlayerCache,
    private val printer: ResultPrinter,
    private val executor: BukkitExecutor,
) {

    fun whenCommand(player: Player, username: String) {
        executor.async{
            val uuid = players.uuid(username)
            val nationFuture = service.memberRegistry[player.uniqueId].direct.getNation()

            val result = execute(player, username, uuid, nationFuture)

            if(result.code != ResultCode.EVENT_CANCELLED) {

                val messageCode = when {
                    result.code == ResultCode.SUCCESSFUL -> "SUCCESSFUL_KICK"
                    printer.containsMessage(result.code) -> result.code
                    else -> "ERROR"
                }

                val text = ResultMessage(messageCode, result).apply {
                    set("player", player.name)
                    set("nation", nationFuture.join()?.name ?: "")
                    set("target", username)
                }

                player.sendMessage(printer.build(text))

            }
        }
    }

    private fun execute(player: Player, username: String, uuid: UUID?, nationFuture: CompletableFuture<Nation?>)
    : OperationResult<Boolean> {

        if(uuid == null){
            return OperationResult(ResultCode.PLAYER_NOT_EXISTS, false)
        }

        val nation = nationFuture.join() ?: return OperationResult(ResultCode.NO_NATION, false)

        return nation.operateKickMember(service.memberRegistry[player.uniqueId], uuid).process()
    }
}