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

            if(result.code == ResultCode.SUCCESSFUL) {

                val messageCode = when {
                    printer.containsMessage(result.code) -> result.code
                    else -> "ERROR"
                }

                val resultMessage = ResultMessage(messageCode, result).apply {
                    set("player", player.name)
                    set("nation", nationFuture.join()?.name ?: "")
                    set("target", transferTo)
                }

                player.sendMessage(printer.build(resultMessage).toTypedArray())

            }
        }
    }

    fun execute(player: Player, transferTo: String, uuid: UUID?, nationFuture: CompletableFuture<Nation?>)
    : OperationResult<Boolean>{
        if(uuid == null){
            return OperationResult(ResultCode.PLAYER_NOT_EXISTS, false)
        }

        if(players.isOnline(transferTo)){
            return OperationResult(ResultCode.PLAYER_NOT_ONLINE, false)
        }

        val nation = nationFuture.join()

        if(nation == null){
            return OperationResult(ResultCode.NATION_NOT_EXISTS, false)
        }

        return nation.operateTransferOwner(service.memberRegistry[player.uniqueId], uuid).process()
    }

}