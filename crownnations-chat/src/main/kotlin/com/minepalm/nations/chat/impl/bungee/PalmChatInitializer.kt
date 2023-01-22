package com.minepalm.nations.chat.impl.bungee

import com.minepalm.library.database.impl.internal.MySQLDB
import com.minepalm.nations.NationService
import com.minepalm.nations.chat.impl.ChatTokens
import com.minepalm.palmchat.mysql.MySQLChannelDatabase
import com.minepalm.palmchat.mysql.MySQLChannelFactory
import com.minepalm.nations.chat.impl.PrefixNation
import com.minepalm.palmchat.api.ChatService

object PalmChatInitializer{

    fun initializeBungee(palmChat: ChatService, service: NationService, mysql: MySQLDB, prefixFormat: String){
        initialize(palmChat, mysql)
        palmChat.formatRegistry.registerPlaceholder("%nation%", PrefixNation(service, prefixFormat))
    }

    private fun initialize(palmChat: ChatService, database: MySQLDB){
        palmChat.channelRegistry.register(ChatTokens.nation, MySQLChannelFactory(MySQLChannelDatabase(database)))
    }
}