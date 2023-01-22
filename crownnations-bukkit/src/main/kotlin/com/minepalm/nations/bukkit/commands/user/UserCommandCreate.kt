package com.minepalm.nations.bukkit.commands.user

import com.minepalm.arkarangutils.bukkit.BukkitExecutor
import com.minepalm.nations.NationService
import com.minepalm.nations.OperationResult
import com.minepalm.nations.ResultCode
import com.minepalm.nations.bukkit.CreationSessionRegistry
import com.minepalm.nations.bukkit.message.ResultMessage
import com.minepalm.nations.bukkit.message.ResultPrinter
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

    private fun execute(player: Player, nationName: String): OperationResult<Boolean> {
        if(!player.inventory.contains(Material.BEACON))
            return OperationResult(ResultCode.NO_BEACON, false)
        return creationSessionRegistry.prepare(player.uniqueId, nationName)
    }
}