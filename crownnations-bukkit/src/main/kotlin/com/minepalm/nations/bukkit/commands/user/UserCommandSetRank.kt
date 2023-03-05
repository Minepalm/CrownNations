package com.minepalm.nations.bukkit.commands.user

import com.minepalm.arkarangutils.bukkit.BukkitExecutor
import com.minepalm.nations.*
import com.minepalm.nations.bukkit.PlayerCache
import com.minepalm.nations.bukkit.message.ResultMessage
import com.minepalm.nations.bukkit.message.ResultPrinter
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.CompletableFuture

class UserCommandSetRank(
    val service: NationService,
    val players: PlayerCache,
    val printer: ResultPrinter,
    val executor: BukkitExecutor
) {

    fun whenCommand(player: Player, username: String, rankIn: String){
        executor.async {
            val uuid = players.uuid(username)
            val nationFuture = service.memberRegistry[player.uniqueId].direct.getNation()
            val result = execute(player, username, uuid, nationFuture, rankIn)

            if(result.code != ResultCode.EVENT_CANCELLED) {

                val messageCode = when {
                    printer.containsMessage(result.code) -> result.code
                    else -> "ERROR"
                }

                val resultMessage = ResultMessage(messageCode, result).apply {
                    set("player", player.name)
                    set("rank", rankIn)
                    set("nation", nationFuture.join()?.name ?: "")
                    set("target", username)
                }

                player.sendMessage(printer.build(resultMessage))

            }
        }

    }

    private fun execute(player: Player, username: String, uuid: UUID?, nationFuture: CompletableFuture<Nation?>, rankIn: String)
            : OperationResult<Boolean> {
        if(uuid == null){
            return OperationResult(ResultCode.PLAYER_NOT_EXISTS, false)
        }

        if (!players.isOnline(username)) {
            return OperationResult(ResultCode.PLAYER_NOT_ONLINE, false)
        }

        val nation = nationFuture.join()

        if(nation == null){
            return OperationResult(ResultCode.NATION_NOT_EXISTS, false)
        }

        val rank: NationRank

        try{
            rank = parse(rankIn)
        }catch (ex: Throwable){
            return OperationResult(ResultCode.INVALID_RANK_NAME, false)
        }

        return nation.operateSetMember(service.memberRegistry[player.uniqueId], uuid, rank).process()

    }

    private fun parse(name: String): NationRank {
        return when(name){
            "왕" -> NationRank.OWNER
            "부왕" -> NationRank.OFFICER
            "시민" -> NationRank.RESIDENT
            else -> throw IllegalArgumentException()
        }
    }

}