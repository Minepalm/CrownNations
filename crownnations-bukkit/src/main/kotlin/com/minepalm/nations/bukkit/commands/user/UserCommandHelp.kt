package com.minepalm.nations.bukkit.commands.user

import com.minepalm.nations.bukkit.message.ResultPrinter
import org.bukkit.entity.Player

class UserCommandHelp(private val printer: ResultPrinter) {

    fun whenCommand(player: Player, page: Int){
        val showTo = "$page"
        if(printer.containsMessage(showTo)){
            player.sendMessage(printer[showTo])
        }else{
            player.sendMessage(printer["1"])
        }
    }
}