package com.minepalm.nations.bukkit.chat

import com.minepalm.chat.addon.MinepalmChat
import com.minepalm.chat.addon.UserChat
import com.minepalm.nations.Dependencies
import com.minepalm.nations.NationEventListener
import com.minepalm.nations.NationMember
import com.minepalm.nations.NationService
import com.minepalm.nations.chat.NationChatType
import com.minepalm.nations.event.NationAddMemberEvent
import com.minepalm.nations.event.NationDisbandEvent
import com.minepalm.nations.event.NationRemoveMemberEvent
import com.minepalm.palmchat.api.ChatPlayer
import com.minepalm.palmchat.api.ChatType
import com.minepalm.palmchat.api.PalmChat
import java.util.*

sealed class ChatApplyListener {

    companion object{
        private val service by Dependencies[NationService::class]
        private fun UUID.member(): NationMember {
            return service.memberRegistry[this]
        }
        private fun UUID.chat(): ChatPlayer {
            return PalmChat.player(this)
        }

        private fun UUID.userChat(): UserChat? {
            return MinepalmChat.userRepo[this]
        }
    }

    object Join : NationEventListener<NationAddMemberEvent> {
        override fun onEvent(event: NationAddMemberEvent) {
            NationChatApplier.addListening(event.userId.member())
        }

    }

    object Quit : NationEventListener<NationRemoveMemberEvent> {
        override fun onEvent(event: NationRemoveMemberEvent) {
            NationChatApplier.removeListening(event.commander.member())
            event.removerId.chat().speakingChannel().thenApply {
                if(it.category.type == NationChatType) {
                    event.removerId.userChat()?.setChat(ChatType.DEFAULT)
                }
            }
        }

    }

    object Disband : NationEventListener<NationDisbandEvent> {
        override fun onEvent(event: NationDisbandEvent) {

        }

    }
}