package com.minepalm.nations.bukkit

import com.minepalm.library.database.impl.internal.MySQLDB
import com.minepalm.palmchat.api.*
import com.minepalm.palmchat.mysql.MySQLChannelDatabase
import com.minepalm.palmchat.mysql.MySQLChannelFactory
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer

class NetworkBroadcaster(
    private val chatModule: ChatService,
    private val mysql: MySQLDB
) {
    object NationChat: ChatType("NATION", 11)

    //todo: ChatModule 추가할때 옮겨놓기
    object NationUserChat: ChatType("NATION_CHAT", 12)
    companion object{
        @JvmStatic
        val kyori = MiniMessage
            .builder()
            .tags(
                TagResolver.builder()
                    .resolver(StandardTags.color())
                    .resolver(StandardTags.decorations())
                    .build()
            ).build()

    }

    init {
        chatModule.channelRegistry.register(NationChat, MySQLChannelFactory(MySQLChannelDatabase(mysql)))
    }

    fun broadcast(msg: Component){
        chatModule.channelRegistry.channel("SYSTEM:broadcast")
            .session().system()
            .send(ChatText(TextType.KYORI, kyori.serialize(msg)))
    }

    fun broadcast(nationId: Int, msg: Component){
        chatModule.channelRegistry.channel("NATION:$nationId")
            .session().system()
            .send(ChatText(TextType.JSON, kyori.serialize(msg)))
    }
}