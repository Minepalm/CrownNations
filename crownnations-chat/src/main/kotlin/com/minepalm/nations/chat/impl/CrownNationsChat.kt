package com.minepalm.nations.chat.impl

import com.minepalm.library.database.impl.internal.MySQLDB
import com.minepalm.nations.NationService
import com.minepalm.nations.chat.impl.channel.ChannelService
import com.minepalm.nations.chat.impl.channel.GlobalChannelHandler
import com.minepalm.nations.chat.impl.channel.NationChannelHandler
import com.minepalm.palmchat.api.ChatChannel
import com.minepalm.palmchat.api.ChatService
import com.minepalm.palmchat.api.ChatType
import com.minepalm.palmchat.api.PalmChat
import java.util.UUID
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap

class CrownNationsChat(
    mysql: MySQLDB
) {

    val channelHandler = ChannelService()

    private val defaultChannel = ChatTokens.global
    private val database = MySQLUserChannelDatabase(mysql, "crownnations_chat_type")

    init{
        channelHandler.register(GlobalChannelHandler())
        channelHandler.register(NationChannelHandler(database))
    }


    fun apply(type: ChatType, uuid: UUID): CompletableFuture<Unit> {
        val future = database.getChatChannel(uuid).thenCompose { speak(it, uuid) }
        val future2 = channelHandler[type].apply(PalmChat.player(uuid))
        return future.thenCompose { future2 }
    }

    fun reset(type: ChatType, uuid: UUID): CompletableFuture<Unit>{
        val future = database.getChatChannel(uuid)
        val future2 = channelHandler[type].reset(PalmChat.player(uuid))
        return future.thenCompose{
            if(it.equals(type)){
                speak(defaultChannel, uuid)
            }else
                CompletableFuture.completedFuture(Unit)
        }.thenCompose { future2 }
    }

    fun speak(type: ChatType, uuid: UUID): CompletableFuture<Unit>{
        val future = database.setChatChannel(uuid, type)
        val future2 = channelHandler[type].speak(PalmChat.player(uuid))
        return future.thenCompose { future2 }
    }
}