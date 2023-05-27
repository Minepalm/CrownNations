package com.minepalm.nations.bukkit.chat

import com.minepalm.chat.addon.MinepalmChat
import com.minepalm.chat.addon.UserChat
import com.minepalm.nations.Dependencies
import com.minepalm.nations.Nation
import com.minepalm.nations.NationMember
import com.minepalm.nations.NationService
import com.minepalm.palmchat.api.ChatPlayer
import com.minepalm.palmchat.api.PalmChat
import java.util.concurrent.CompletableFuture

object NationChatApplier {


    fun NationMember.chat(): ChatPlayer{
        return PalmChat.player(this.uniqueId)
    }

    val service by Dependencies[NationService::class]

    private fun UserChat.nation(): Nation? {
        return service.memberRegistry[this.uuid].cache.nation
    }

    private fun NationMember.userChat(): UserChat? {
        return MinepalmChat.userRepo[this.uniqueId]
    }


    fun addListening(member: NationMember): CompletableFuture<Unit> {
        val userChat = MinepalmChat.userRepo[member.uniqueId] ?: return CompletableFuture.completedFuture(Unit)
        val nation = userChat.nation() ?: return CompletableFuture.completedFuture(Unit)
        /*
        return nation.chat.let { nationChat ->
            userChat.chatPlayer.let {
                val list = listOf(
                    it.addListening(nationChat.channel),
                    it.addListening(nationChat.system),
                    nationChat.ally?.let { channel -> it.addListening(channel) }
                        ?: CompletableFuture.completedFuture(Unit)
                )
                CompletableFuture.allOf(*list.toTypedArray()).thenApply { Unit }
            }
        }

         */
        return CompletableFuture.completedFuture(null)
    }

    fun setSpeaking(member: NationMember): CompletableFuture<Unit> {
        val userChat = MinepalmChat.userRepo[member.uniqueId] ?: return CompletableFuture.completedFuture(Unit)
        val nation = userChat.nation() ?: return CompletableFuture.completedFuture(Unit)
        /*
        return nation.chat.let { nationChat ->
            userChat.chatPlayer.let {
                val list = listOf(it.setSpeaking(nationChat.channel))
                CompletableFuture.allOf(*list.toTypedArray()).thenApply { Unit }
            }
        }
         */
        return CompletableFuture.completedFuture(null)
    }

    fun removeListening(member: NationMember): CompletableFuture<Unit> {
        return member.chat().let { chatPlayer ->
            chatPlayer.getListening().thenCompose {
                it.map { channel -> chatPlayer.removeListening(channel) }.merge()
            }
        }
    }
    fun Collection<CompletableFuture<*>>.merge(): CompletableFuture<Unit> {
        return CompletableFuture.allOf(*this.toTypedArray()).thenApply { Unit }
    }

}