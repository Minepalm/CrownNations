package com.minepalm.nations.bukkit.commands.user

import com.minepalm.arkarangutils.bukkit.BukkitExecutor
import com.minepalm.nations.Nation
import com.minepalm.nations.NationRank
import com.minepalm.nations.NationService
import com.minepalm.nations.bukkit.PlayerCache
import com.minepalm.nations.bukkit.message.ResultMessage
import com.minepalm.nations.bukkit.message.ResultPrinter
import org.bukkit.entity.Player
import java.text.DecimalFormat

//국가 정보
class UserCommandInfo(
    private val service: NationService,
    private val executor: BukkitExecutor,
    private val printer: ResultPrinter,
    private val players: PlayerCache
) {

    companion object {
        val format = DecimalFormat("###,###,###,###")
    }

    fun whenCommand(player: Player, nationName: String) {
        executor.async {
            val nation = service.nationRegistry.direct.getNation(nationName).join()
            if (nation == null) {
                player.sendMessage(printer["NATION_NOT_FOUND"])
                return@async
            }
            val text = printMessage(nation)
            player.sendMessage(printer.build(text))

        }
    }

    fun whenCommand(player: Player) {
        executor.async {
            val nation = service.memberRegistry[player.uniqueId].cache.nation
            if (nation == null) {
                player.sendMessage(printer["NO_NATION"])
                return@async
            }
            val text = printMessage(nation)
            player.sendMessage(printer.build(text))

        }
    }

    private fun printMessage(nation: Nation): ResultMessage {
        val balance = nation.bank.balance().join()
        val owner = nation.cache.getOwner().let { players.username(it.uniqueId) } ?: "알수 없음"
        var officers = printRankers(nation, NationRank.OFFICER)
        var members = printRankers(nation, NationRank.RESIDENT)

        return ResultMessage("INFO").apply {
            set("nation", nation.name)
            set("owner", owner)
            set("money", format.format(balance))
            set("officers", officers)
            set("members", members)
        }
    }

    private fun printRankers(nation: Nation, rank: NationRank): String {
        return nation.cache.getRanks()
            .asSequence()
            .filter { it.value == rank }
            .map { players.username(it.key) }
            .toList()
            .let {
                if(it.isEmpty())
                    "없음"
                else
                    if(it.size > 10) {
                        it.subList(0, 10).joinToString(", ") + " 외 ${it.size - 10}명"
                    } else
                        it.joinToString(", ")
            }
    }
}