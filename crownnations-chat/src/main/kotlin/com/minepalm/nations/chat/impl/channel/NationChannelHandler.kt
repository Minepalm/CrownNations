package com.minepalm.nations.chat.impl.channel

import com.minepalm.nations.CrownNations
import com.minepalm.nations.chat.impl.ChatTokens
import com.minepalm.nations.chat.impl.MySQLUserChannelDatabase
import com.minepalm.palmchat.api.ChatChannel
import com.minepalm.palmchat.api.ChatPlayer
import com.minepalm.palmchat.api.ChatType
import com.minepalm.palmchat.api.PalmChat
import java.util.concurrent.CompletableFuture

class NationChannelHandler(
    private val database: MySQLUserChannelDatabase
) : ChatChannelHandler{

    private val nations = CrownNations.inst
    override val type: ChatType = ChatTokens.nation

    private fun ChatPlayer.nationChannel(): CompletableFuture<ChatChannel?>{
        return nations.memberRegistry[uniqueId].direct.getNation().thenApply {
            it?.let { PalmChat.channel("NATION:${it.id}") }
        }
    }

    override fun reset(player: ChatPlayer): CompletableFuture<Unit> {
        return player.nationChannel().thenCompose { it?.let { player.removeListening(it) } }
    }

    override fun apply(player: ChatPlayer): CompletableFuture<Unit> {
        return player.nationChannel().thenCompose { it?.let { player.addListening(it) } }
    }

    override fun speak(player: ChatPlayer): CompletableFuture<Unit> {
        return player.nationChannel().thenCompose { it?.let { player.setSpeaking(it) } }
    }
}