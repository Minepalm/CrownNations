package com.minepalm.nations.chat.impl

import com.minepalm.palmchat.api.ChatType

object ChatTokens {

    val global = ChatType("NATION_GLOBAL", 19)
    val nation = ChatType("NATION", 20)
    //object AllianceChat: ChatType("ALLY", 22)
    //object PartyChat: ChatType("PARTY", 30)


    fun find(name: String): ChatType{
        return when(name){
            "GLOBAL" -> global
            "NATION" -> nation
            else -> global
        }
    }

}