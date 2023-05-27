package com.minepalm.nations.bukkit.chat

import com.minepalm.chat.addon.UserChat
import com.minepalm.chat.addon.UserChatAdapter
import com.minepalm.nations.Dependencies
import com.minepalm.nations.Nation
import com.minepalm.nations.NationMember
import com.minepalm.nations.NationService
import com.minepalm.nations.chat.NationChatType
import com.minepalm.palmchat.api.ChatType
import java.util.concurrent.CompletableFuture

class NationChatAdapter(override val type: ChatType = NationChatType) : UserChatAdapter {

    val service by Dependencies[NationService::class]

    private fun UserChat.nation(): Nation? {
        return service.memberRegistry[this.uuid].cache.nation
    }

    private fun UserChat.member(): NationMember {
        return service.memberRegistry[this.uuid]
    }

    override fun load(userChat: UserChat): CompletableFuture<Unit> {
        val addListening = NationChatApplier.addListening(userChat.member())
        val setSpeaking = userChat.getCurrentChat().thenCompose { chat ->
            if (chat == NationChatType) NationChatApplier.setSpeaking(userChat.member())
            else CompletableFuture.completedFuture(Unit)
        }
        return CompletableFuture.allOf(addListening, setSpeaking).thenApply { Unit }
    }

    override fun apply(userChat: UserChat): CompletableFuture<Unit> {
        return NationChatApplier.setSpeaking(userChat.member())
    }



}