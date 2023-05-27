package com.minepalm.nations.bukkit.commands.user

import com.minepalm.nations.Dependencies
import com.minepalm.nations.NationService
import com.minepalm.nations.bukkit.gui.GUIFactory
import com.minepalm.nations.bukkit.gui.WarpMainGUI
import com.minepalm.nations.bukkit.message.PrinterRegistry
import com.minepalm.nations.bukkit.message.ResultPrinter
import com.minepalm.nations.bukkit.openSync
import com.minepalm.nations.bukkit.warp.WarpExecutor
import com.minepalm.nations.config.WarpConfiguration
import org.bukkit.entity.Player

//todo: Cleanup code
class UserCommandWarp(
    val printer: ResultPrinter,
    val config: WarpConfiguration
){

    val service: NationService by Dependencies[NationService::class]

    fun whenCommand(player: Player) {
        if(service.memberRegistry[player.uniqueId].cache.nation != null)
            WarpMainGUI(player.uniqueId).openSync(player)
        else
            player.sendMessage("해당 국가가 존재하지 않습니다.")
    }

    class Outpost(
        val printer: ResultPrinter,
        val config: WarpConfiguration
    ) {

        val service: NationService by Dependencies[NationService::class]
        val guiFactory: GUIFactory by Dependencies[GUIFactory::class]
        val warpExecutor: WarpExecutor by Dependencies[WarpExecutor::class]

        fun whenCommand(player: Player) {
            val member = service.memberRegistry[player.uniqueId]
            member.cache.nation?.let { nation ->
                guiFactory.buildOutpostWarpGUI(nation, member).thenAccept {
                    it.openSync(player)
                }
            } ?: player.sendMessage("해당 국가가 존재하지 않습니다.")
        }

        fun whenCommandShortcut(player: Player, index: Int) {
            service.memberRegistry[player.uniqueId].cache.nation?.let { nation ->
                nation.territory.direct.getOutposts().thenApply {
                    it.getOrNull(index)?.let { outpost ->
                        outpost.getWarpLocation().thenApply {
                            warpExecutor.runWarp(player.uniqueId, it, config.getWarpDelay())
                        }
                    } ?: player.sendMessage("해당 전초기지가 존재하지 않습니다.")
                }
            } ?: player.sendMessage("해당 국가가 존재하지 않습니다.")
        }

    }

    class Castle(
        val printer: ResultPrinter,
        val config: WarpConfiguration
    ) {

        val service: NationService by Dependencies[NationService::class]
        val guiFactory: GUIFactory by Dependencies[GUIFactory::class]
        val warpExecutor: WarpExecutor by Dependencies[WarpExecutor::class]

        fun whenCommand(player: Player) {
            val member = service.memberRegistry[player.uniqueId]
            member.cache.nation?.let { nation ->
                guiFactory.buildCastleWarpGUI(nation, member).thenAccept {
                    it.openSync(player)
                }
            } ?: player.sendMessage("해당 국가가 존재하지 않습니다.")
        }

        fun whenCommandShortcut(player: Player, index: Int) {
            service.memberRegistry[player.uniqueId].cache.nation?.let { nation ->
                nation.territory.direct.getCastles().thenApply {
                    it.getOrNull(index)?.let { castle ->
                        castle.getWarpLocation().thenApply {
                            warpExecutor.runWarp(player.uniqueId, it, config.getWarpDelay())
                        }
                    } ?: player.sendMessage("해당 성이 존재하지 않습니다.")
                }
            } ?: player.sendMessage("해당 국가가 존재하지 않습니다.")
        }

    }

}