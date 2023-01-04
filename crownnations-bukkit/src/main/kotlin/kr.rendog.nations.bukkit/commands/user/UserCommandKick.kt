package kr.rendog.nations.bukkit.commands.user

import com.minepalm.arkarangutils.bukkit.BukkitExecutor
import kr.rendog.nations.Nation
import kr.rendog.nations.NationService
import kr.rendog.nations.OperationResult
import kr.rendog.nations.ResultCode
import kr.rendog.nations.bukkit.PlayerCache
import kr.rendog.nations.bukkit.message.ResultMessage
import kr.rendog.nations.bukkit.message.ResultPrinter
import kr.rendog.nations.bukkit.sendMessage
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

                player.sendMessage(printer.build(text).toTypedArray())

            }
        }
    }

    private fun execute(player: Player, username: String, uuid: UUID?, nationFuture: CompletableFuture<Nation?>)
    : OperationResult<Boolean>{

        if(uuid == null){
            return OperationResult(ResultCode.PLAYER_NOT_EXISTS, false)
        }

        if(players.isOnline(username)){
            return OperationResult(ResultCode.PLAYER_NOT_ONLINE, false)
        }

        val nation = nationFuture.join() ?: return OperationResult(ResultCode.NO_NATION, false)

        return nation.operateKickMember(service.memberRegistry[player.uniqueId], uuid).process()
    }
}