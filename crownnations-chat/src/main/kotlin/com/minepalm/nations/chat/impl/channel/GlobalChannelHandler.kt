package com.minepalm.nations.chat.impl.channel

import com.minepalm.nations.chat.impl.ChatTokens
import com.minepalm.palmchat.api.ChatChannel
import com.minepalm.palmchat.api.ChatPlayer
import com.minepalm.palmchat.api.ChatType
import com.minepalm.palmchat.api.PalmChat
import java.util.concurrent.CompletableFuture

class GlobalChannelHandler : ChatChannelHandler {

    override val type: ChatType = ChatTokens.global
    private val channel: ChatChannel = PalmChat.inst.channelRegistry["NATION_GLOBAL:0"]

    override fun reset(player: ChatPlayer): CompletableFuture<Unit> {
        return player.removeListening(channel)
    }

    override fun apply(player: ChatPlayer): CompletableFuture<Unit> {
        return player.addListening(channel)
    }

    override fun speak(player: ChatPlayer): CompletableFuture<Unit> {
        TODO("Not yet implemented")
    }
}