package com.minepalm.nations.chat.impl.bungee

import com.minepalm.nations.NationEventListener
import com.minepalm.nations.chat.impl.ChatTokens
import com.minepalm.nations.chat.impl.CrownNationsChat
import com.minepalm.nations.event.NationRemoveMemberEvent

class ChatRemoveMemberListener(
    private val inst: CrownNationsChat
) : NationEventListener<NationRemoveMemberEvent> {
    override fun onEvent(event: NationRemoveMemberEvent) {
        inst.reset(ChatTokens.nation, event.removerId)
    }
}