package com.minepalm.nations.bukkit.listener.bukkit

import com.minepalm.arkarangutils.bukkit.BukkitExecutor
import com.minepalm.nations.NationMember
import com.minepalm.nations.NationService
import com.minepalm.nations.OperationResult
import com.minepalm.nations.ResultCode
import com.minepalm.nations.bukkit.CreationSessionRegistry
import com.minepalm.nations.bukkit.message.PrinterRegistry
import com.minepalm.nations.bukkit.message.ResultMessage
import com.minepalm.nations.bukkit.message.ResultPrinter
import com.minepalm.nations.config.TerritoryConfiguration
import com.minepalm.nations.utils.ServerLoc
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.inventory.ItemStack

class NationMonumentClaimListener(
    private val printers: PrinterRegistry,
    private val service: NationService,
    private val registry: CreationSessionRegistry,
    private val config: TerritoryConfiguration,
    private val executor: BukkitExecutor
) : Listener {

    private val serverName: String = service.network.host.name
    private val castlePrinter = printers["CLAIM_CASTLE"]
    private val outpostPrinter = printers["CLAIM_OUTPOST"]

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    fun onPlace(event: BlockPlaceEvent) {
        val loc = event.blockPlaced.location.loc()
        when (event.itemInHand.type) {
            Material.BEACON -> {
                if (event.itemInHand.itemMeta.displayName == config.castleItemName) {
                    executor.async {
                        if (registry.exists(event.player.uniqueId)) {
                            onNationCreation(event.player, loc)
                        } else {
                            onClaimCastle(event.player, loc)
                        }
                    }
                    event.isCancelled = true
                }
            }

            Material.NETHERRACK -> {
                if(event.itemInHand.itemMeta.displayName == config.outpostItemName) {
                    executor.async{ onClaimOutpost(event.player, loc) }
                }
                event.isCancelled = true
            }
            else -> {}
        }
    }


    private fun onClaimOutpost(player: Player, loc: ServerLoc) {
        val member = player.member()
        member.cache.getNation()?.also { nation ->
            val item = player.takeItem(Material.NETHERRACK)?.apply { amount = 1 }
            nation.territory.local.operateNewOutpost(member, loc).process().also {
                printResult(outpostPrinter, it, player, nation.name)
                if (it.code != ResultCode.SUCCESSFUL) {
                    item?.let { i -> player.inventory.addItem(i) }
                }
            }
        } ?: player.sendMessage(outpostPrinter["NO_NATION"])
    }

    private fun onClaimCastle(player: Player, loc: ServerLoc) {
        val member = player.member()
        member.cache.getNation()?.also { nation ->
            val item = player.takeItem(Material.BEACON)?.apply { amount = 1 }
            nation.territory.local.operateNewCastle(member, loc).process().also {
                printResult(castlePrinter, it, player, nation.name)
                if (it.code != ResultCode.SUCCESSFUL) {
                    item?.let { i -> player.inventory.addItem(i) }
                }
            }
        } ?: player.sendMessage(castlePrinter["NO_NATION"])
    }

    private fun onNationCreation(player: Player, loc: ServerLoc) {
        val uuid = player.uniqueId
        val name = registry[uuid]!!
        val item = player.takeItem(Material.BEACON)?.apply { amount = 1 }
        registry.operate(uuid, loc)?.process()
            ?.also { result ->
                printResult(castlePrinter, result, player, name)
                if (result.code != ResultCode.SUCCESSFUL) {
                    item?.let { i -> player.inventory.addItem(i) }
                }

            } ?: item?.let { i -> player.inventory.addItem(i) }

    }

    private fun printResult(printer: ResultPrinter, result: OperationResult<*>, player: Player, nation: String){
        if (result.code != ResultCode.EVENT_CANCELLED) {

            val messageCode = when {
                printer.containsMessage(result.code) -> result.code
                else -> "ERROR"
            }

            val text = ResultMessage(messageCode, result).apply {
                set("player", player.name)
                set("nation", nation)
            }

            player.sendMessage(printer.build(text))
        }
    }

    private fun Player.takeItem(material: Material): ItemStack?{
        inventory.filterNotNull().forEach {  item ->
            if(item.type == material){
                return item.clone().also { item.amount = item.amount - 1 }
            }
        }
        return null
    }


    private fun Player.member(): NationMember {
        return service.memberRegistry[this.uniqueId]
    }


    private fun Location.loc(): ServerLoc {
        return ServerLoc(serverName, this.world.name, this.blockX, this.blockY, this.blockZ)
    }

}