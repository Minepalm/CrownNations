package com.minepalm.nations.chat.impl

import com.minepalm.nations.NationMember
import com.minepalm.nations.NationService
import com.minepalm.palmchat.api.ChatFormatPlaceholder
import com.minepalm.palmchat.api.TextContext
import java.util.*

class PrefixNation(
    private val nations: NationService,
    private val prefixFormat: String
) : ChatFormatPlaceholder("%nation%", 20) {

    override fun format(ctx: TextContext) {
        val nation = ctx.component.sender.member().direct.getNation().join()
        val prefix = nation?.let {  prefixFormat.replace("%nation%", it.name) } ?: ""
        ctx.replace("%nation%", prefix)
    }

    private fun UUID.member(): NationMember {
        return nations.memberRegistry[this]
    }
}