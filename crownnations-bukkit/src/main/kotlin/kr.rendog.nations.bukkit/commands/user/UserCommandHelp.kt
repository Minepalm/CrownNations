package kr.rendog.nations.bukkit.commands.user

import kr.rendog.nations.bukkit.message.ResultPrinter
import kr.rendog.nations.bukkit.sendMessage
import org.bukkit.entity.Player

class UserCommandHelp(private val printer: ResultPrinter) {

    fun whenCommand(player: Player, page: Int){
        val showTo = "$page"
        if(printer.containsMessage(showTo)){
            player.sendMessage(printer[showTo].toTypedArray())
        }else{
            player.sendMessage(printer["1"].toTypedArray())
        }
    }
}