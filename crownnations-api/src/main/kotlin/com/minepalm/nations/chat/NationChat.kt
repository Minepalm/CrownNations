package com.minepalm.nations.chat

import com.minepalm.palmchat.api.ChatChannel
import java.util.concurrent.CompletableFuture

interface NationChat {

    val system: NationChatChannel
    val channel: NationChatChannel
    val ally: ChatChannel?

    fun disband(): CompletableFuture<Unit>
}