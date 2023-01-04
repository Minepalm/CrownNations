package kr.rendog.nations.bukkit

import com.minepalm.palmchat.api.ChatPlayer
import com.minepalm.palmchat.api.ChatService
import com.minepalm.palmchat.api.ChatType
import com.minepalm.palmchat.mysql.MySQLChannelDatabase
import com.minepalm.palmchat.mysql.MySQLChannelFactory
import com.minepalm.library.database.impl.internal.MySQLDB

class NetworkBroadcaster(
    private val chatModule: ChatService,
    private val mysql: MySQLDB
) {
    object NationChat: ChatType("NATION", 11)

    //todo: ChatModule 추가할때 옮겨놓기
    object NationUserChat: ChatType("NATION_CHAT", 12)
    companion object{

    }

    init {
        chatModule.channelRegistry.register(NationChat, MySQLChannelFactory(MySQLChannelDatabase(mysql)))
    }

    fun broadcast(list: List<String>){
        chatModule.channelRegistry.channel("SYSTEM:broadcast")
            ?.context(ChatPlayer.SYSTEM, "")?.send(list.joinToString("\n"))
    }

    fun broadcast(nationId: Int, msg: List<String>){
        chatModule.channelRegistry.channel("NATION:$nationId")
            ?.context(ChatPlayer.SYSTEM, "")?.send(msg.joinToString("\n"))
    }
}