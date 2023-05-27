package com.minepalm.nations.bukkit

import com.minepalm.library.database.impl.internal.MySQLDB
import com.minepalm.nations.chat.NationChatType
import com.minepalm.palmchat.api.ChatService
import com.minepalm.palmchat.api.ChatText
import com.minepalm.palmchat.api.ChatType
import com.minepalm.palmchat.api.TextType
import com.minepalm.palmchat.mysql.MySQLChannelDatabase
import com.minepalm.palmchat.mysql.MySQLChannelFactory
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags

class NetworkBroadcaster(
    private val chatModule: ChatService,
    private val mysql: MySQLDB
) {
    val NationChat = NationChatType

    companion object{
        @JvmStatic
        val kyori = MiniMessage
            .builder()
            .tags(
                TagResolver.builder()
                    .resolver(StandardTags.defaults())
                    .build()
            ).build()

    }

    init {
        chatModule.channelRegistry.register(NationChatType, MySQLChannelFactory(MySQLChannelDatabase(mysql)))
    }

    fun broadcast(msg: Component){
        chatModule.channelRegistry.channel("SYSTEM:broadcast")
            .session().system()
            .send(ChatText(TextType.KYORI, kyori.serialize(msg)))
    }

    fun broadcast(nationId: Int, msg: Component){
        chatModule.channelRegistry.channel("NATION:$nationId")
            .session()
            .system()
            .send(ChatText(TextType.KYORI, kyori.serialize(msg)))
    }
}