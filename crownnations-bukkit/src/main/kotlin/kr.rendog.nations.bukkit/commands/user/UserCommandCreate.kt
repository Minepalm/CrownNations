package kr.rendog.nations.bukkit.commands.user

import com.minepalm.arkarangutils.bukkit.BukkitExecutor
import kr.rendog.nations.NationService
import kr.rendog.nations.OperationResult
import kr.rendog.nations.ResultCode
import kr.rendog.nations.bukkit.CreationSessionRegistry
import kr.rendog.nations.bukkit.message.ResultMessage
import kr.rendog.nations.bukkit.message.ResultPrinter
import kr.rendog.nations.bukkit.sendMessage
import org.bukkit.Material
import org.bukkit.entity.Player

class UserCommandCreate(
    private val service : NationService,
    private val creationSessionRegistry: CreationSessionRegistry,
    private val printer : ResultPrinter,
    private val executor : BukkitExecutor
) {

    fun whenCommand(player: Player, nationName: String){
        executor.async{
            val result = execute(player, nationName)

            if(result.code != ResultCode.EVENT_CANCELLED) {

                val messageCode = when {
                    printer.containsMessage(result.code) -> result.code
                    result.code == ResultCode.SUCCESSFUL -> ResultCode.PREPARE_COMPLETE
                    else -> "ERROR"
                }

                val text = ResultMessage(messageCode, result).apply {
                    set("player", player.name)
                    set("nation", nationName)
                }

                player.sendMessage(printer.build(text))
            }

        }
    }

    private fun execute(player: Player, nationName: String): OperationResult<Boolean>{
        if(!player.inventory.contains(Material.BEACON))
            return OperationResult(ResultCode.NO_BEACON, false)
        return creationSessionRegistry.prepare(player.uniqueId, nationName)
    }
}