package com.minepalm.nations.chat.impl.channel

import com.minepalm.palmchat.api.ChatPlayer
import com.minepalm.palmchat.api.ChatType
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap


class ChannelService {

    private val map = ConcurrentHashMap<ChatType, ChatChannelHandler>()


    operator fun get(chatType: ChatType): ChatChannelHandler{
        return map[chatType] ?: Skip(chatType)
    }

    fun register(handler: ChatChannelHandler){
        map[handler.type] = handler
    }

    private class Skip(
        override val type: ChatType
    ): ChatChannelHandler{

        override fun apply(player: ChatPlayer): CompletableFuture<Unit> {
            return CompletableFuture.completedFuture(Unit)
        }

        override fun reset(player: ChatPlayer): CompletableFuture<Unit> {
            return CompletableFuture.completedFuture(Unit)
        }

        override fun speak(player: ChatPlayer): CompletableFuture<Unit> {
            return CompletableFuture.completedFuture(Unit)
        }
    }
}