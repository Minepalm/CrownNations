package com.minepalm.nations.chat.impl.channel

import com.minepalm.palmchat.api.ChatPlayer
import com.minepalm.palmchat.api.ChatType
import java.util.concurrent.CompletableFuture

interface ChatChannelHandler {

    val type: ChatType
    
    fun reset(player: ChatPlayer): CompletableFuture<Unit>
    
    fun apply(player: ChatPlayer): CompletableFuture<Unit>

    fun speak(player: ChatPlayer): CompletableFuture<Unit>
}