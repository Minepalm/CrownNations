package com.minepalm.nations.bukkit.commands.user

import com.minepalm.arkarangutils.bukkit.BukkitExecutor
import com.minepalm.chat.addon.MinepalmChat
import com.minepalm.nations.Dependencies
import com.minepalm.nations.Nation
import com.minepalm.nations.NationService
import com.minepalm.nations.bukkit.message.ResultPrinter
import com.minepalm.nations.chat.NationChatType
import com.minepalm.palmchat.api.ChatPlayer
import com.minepalm.palmchat.api.PalmChat
import org.bukkit.entity.Player

class UserCommandChat(
    val printer: ResultPrinter
) {

    private val executor by Dependencies[BukkitExecutor::class]
    private val service by Dependencies[NationService::class]
    private val userRepo = MinepalmChat.userRepo


    fun whenCommand(player: Player, msg: String) {
        executor.async {

            val nation = service.memberRegistry[player.uniqueId].cache.nation

            if(nation == null) {
                printer["NO_NATION"].let { player.sendMessage(it) }
                return@async
            }

            if(msg.isEmpty()) {
                executeSwitch(player)
            } else {
                executeSendMessage(nation, player, msg)
            }
        }
    }

    private fun executeSwitch(player: Player) {
        if( player.chat().speakingChannel().join().category.type == NationChatType ) {
            player.sendMessage(printer["ALREADY_IN_NATION_CHAT"])
        } else {
            val result = userRepo[player.uniqueId]?.setChat(NationChatType)
            if( result != null )
                player.sendMessage(printer["SWITCH_TO_NATION_CHAT"])
            else
                player.sendMessage(printer["LOAD_ERROR"])
        }
    }

    private fun executeSendMessage(nation: Nation, player: Player, msg: String) {
        //nation.chat.channel.session().player(player.chat()).send(msg)
    }

    private fun Player.chat(): ChatPlayer {
        return PalmChat.player(this.uniqueId)
    }
}