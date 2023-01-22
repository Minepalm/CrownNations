package com.minepalm.nations.chat.impl.bungee

import com.minepalm.nations.NationEventListener
import com.minepalm.nations.chat.impl.ChatTokens
import com.minepalm.nations.chat.impl.CrownNationsChat
import com.minepalm.nations.event.NationAddMemberEvent

class ChatAddMemberListener(
    private val inst: CrownNationsChat
) : NationEventListener<NationAddMemberEvent> {
    override fun onEvent(event: NationAddMemberEvent) {
        inst.apply(ChatTokens.nation, event.userId)
    }
}