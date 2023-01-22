package com.minepalm.nations.chat.impl.bukkit

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import com.minepalm.arkarangutils.bungee.BungeeExecutor
import com.minepalm.nations.NationService
import com.minepalm.nations.chat.impl.ChatTokens
import com.minepalm.nations.chat.impl.CrownNationsChat
import net.md_5.bungee.api.connection.ProxiedPlayer
import org.bukkit.entity.Player

class BukkitCommands(
    private val executor: BungeeExecutor,
    private val inst: CrownNationsChat,
    private val nations: NationService,
    private val messageSet: Map<String, String>
) : BaseCommand() {

    @CommandAlias("국가채팅")
    fun nationChat(player: Player){
        executor.async {
            if(nations.memberRegistry[player.uniqueId].direct.hasNation().join()){
                player.sendMessage(messageSet["NO_NATION"]!!)
                return@async
            }

            if(inst[player.uniqueId].currentChatType == NationChatType.NATION){
                player.sendMessage("ALREADY_NATION_CHAT")
                return@async
            }
            inst[player.uniqueId].setChannel(NationChatType.GLOBAL)
        }
    }

    @CommandAlias("전체채팅")
    fun globalChat(player: Player){
        executor.async {
            if(inst[player.uniqueId].currentChatType == NationChatType.GLOBAL){
                player.sendMessage("ALREADY_GLOBAL_CHAT")
                return@async
            }
            inst[player.uniqueId].setChannel(NationChatType.GLOBAL)
        }
    }

    @CommandAlias("채팅")
    fun switchChat(player: Player){

    }

}