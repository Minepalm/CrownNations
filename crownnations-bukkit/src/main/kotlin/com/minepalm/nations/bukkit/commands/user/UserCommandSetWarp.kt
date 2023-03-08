package com.minepalm.nations.bukkit.commands.user

import com.minepalm.arkarangutils.bukkit.BukkitExecutor
import com.minepalm.nations.*
import com.minepalm.nations.bukkit.convert
import com.minepalm.nations.bukkit.message.ResultMessage
import com.minepalm.nations.bukkit.message.ResultPrinter
import com.minepalm.nations.territory.NationMonument
import com.minepalm.nations.territory.WarpMonument
import org.bukkit.Location
import org.bukkit.entity.Player

class UserCommandSetWarp(
    val printer: ResultPrinter
) {

    val service: NationService by Dependencies[NationService::class]
    val executor: BukkitExecutor by Dependencies[BukkitExecutor::class]


    fun whenCommand(player: Player) {
        executor.async{
            val nationFuture = service.memberRegistry[player.uniqueId].direct.getNation()
            val loc = player.location.convert()
            val monument = getCurrentMonument(player.location)

            val result = if (monument is WarpMonument) {
                monument.operateSetWarpLocation(service.memberRegistry[player.uniqueId], loc).process()
            } else {
                OperationResult(ResultCode.NOT_WARP_MONUMENT, false)
            }

            if(result.code != ResultCode.EVENT_CANCELLED) {

                val messageCode = when {
                    printer.containsMessage(result.code) -> result.code
                    else -> "ERROR"
                }

                val text = ResultMessage(messageCode, result).apply {
                    set("player", player.name)
                    set("nation", nationFuture.join()?.name ?: "")
                    set("index", nationFuture.join()?.findIndex(monument!!).toString())
                }

                player.sendMessage(printer.build(text))

            }
        }
    }

    private fun getCurrentMonument(location: Location) : NationMonument? {
        val loc = location.convert()
        val world = service.territoryService.universe.host[loc]
        return world?.local?.get(loc)
    }

    private fun Nation.findIndex(monument: NationMonument): Int {
        this.territory.direct.getCastles().join()
            .sortedBy { it.id }
            .forEachIndexed { index, castle -> if(castle.id == monument.id) { return index } }
        return -1
    }
}