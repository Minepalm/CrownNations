package com.minepalm.nations.bukkit.commands.user

import com.minepalm.arkarangutils.bukkit.BukkitExecutor
import com.minepalm.nations.NationRank
import com.minepalm.nations.NationService
import com.minepalm.nations.bukkit.PlayerCache
import com.minepalm.nations.bukkit.message.ResultMessage
import com.minepalm.nations.bukkit.message.ResultPrinter
import org.bukkit.entity.Player

//국가 정보
class UserCommandInfo(
    private val service: NationService,
    private val executor: BukkitExecutor,
    private val printer: ResultPrinter,
    private val players: PlayerCache
) {

    fun whenCommand(player: Player, nationName: String){
        executor.async {
            val nation = service.nationRegistry.direct.getNation(nationName).join()
            if (nation == null) {
                player.sendMessage(printer["NATION_NOT_FOUND"])
                return@async
            }
            val owner = nation.cache.getOwner().let { players.username(it.uniqueId) } ?: "알수 없음"
            var members = nation.cache.getRanks()
                .asSequence()
                .filter { it.value.hasPermissibleOf(NationRank.RESIDENT) && !it.value.hasPermissibleOf(NationRank.OWNER) }
                .map { players.username(it.key) }
                .joinToString(", ")

            if(members.isEmpty()){
                members = "없음"
            }

            val text = ResultMessage("INFO").apply {
                set("nation", nation.name)
                set("owner", owner)
                set("members", members)
            }
            player.sendMessage(printer.build(text))

        }
    }

    fun whenCommand(player: Player){
        executor.async {
            val nation = service.memberRegistry[player.uniqueId].cache.getNation()
            if (nation == null) {
                player.sendMessage(printer["NO_NATION"])
                return@async
            }
            val owner = nation.cache.getOwner().let { players.username(it.uniqueId) } ?: "알수 없음"
            var members = nation.cache.getRanks()
                .asSequence()
                .filter { it.value.hasPermissibleOf(NationRank.RESIDENT) && !it.value.hasPermissibleOf(NationRank.OWNER) }
                .map { players.username(it.key) }
                .joinToString(", ")

            if(members.isEmpty()){
                members = "없음"
            }

            val text = ResultMessage("INFO").apply {
                set("nation", nation.name)
                set("owner", owner)
                set("members", members)
            }
            player.sendMessage(printer.build(text))

        }
    }
}