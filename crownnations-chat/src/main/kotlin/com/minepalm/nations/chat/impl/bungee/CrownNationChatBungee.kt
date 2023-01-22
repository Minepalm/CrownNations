package com.minepalm.nations.chat.impl.bungee

//import co.aikar.commands.BungeeCommandManager
import com.minepalm.arkarangutils.bungee.BungeeConfig
import com.minepalm.library.PalmLibrary
import com.minepalm.nations.CrownNations
import com.minepalm.nations.chat.impl.CrownNationsChat
import com.minepalm.nations.event.NationAddMemberEvent
import com.minepalm.nations.event.NationRemoveMemberEvent
import com.minepalm.palmchat.api.PalmChat
import kr.rendog.nations.chat.bungee.BungeeListener
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.plugin.Plugin

class CrownNationChatBungee : Plugin() {

    lateinit var inst: CrownNationsChat

    private val conf = object: BungeeConfig(this, "config.yml", true){

        val prefix = config.getString("prefix")
        val mysql = config.getString("mysql")
        val messageSet = mutableMapOf<String, String>().apply {
                config.getSection("messages").also {
                    it.keys.forEach { key ->
                        put(key, it.getString(key).replace("&", "§"))
                    }
                }

            }
    }

    override fun onEnable() {
        val dataSource = PalmLibrary.dataSource
        val mysql = dataSource.mysql(conf.mysql)
        val nations = CrownNations.inst
        val palmChat = PalmChat.inst
        inst = CrownNationsChat(mysql)
        PalmChatInitializer.initializeBungee(palmChat, nations, mysql, conf.prefix)
        ProxyServer.getInstance().pluginManager.registerListener(this, BungeeListener(inst))
        nations.remoteEventBus.addListener(NationAddMemberEvent::class.java, ChatAddMemberListener(inst))
        nations.remoteEventBus.addListener(NationRemoveMemberEvent::class.java, ChatRemoveMemberListener(inst))
        //BungeeCommandManager(this).registerCommand(
        //    BungeeCommands(
        //    BungeeExecutor(this, ProxyServer.getInstance().scheduler), inst, nations, conf.messageSet)
        //)
    }

    override fun onDisable() {

    }
}