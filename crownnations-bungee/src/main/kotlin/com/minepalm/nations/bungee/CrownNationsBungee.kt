package com.minepalm.nations.bungee

import com.minepalm.library.PalmLibrary
import com.minepalm.nations.NationService
import com.minepalm.nations.core.PalmNationsLauncher
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.plugin.Plugin
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CrownNationsBungee : Plugin(){

    companion object{
        lateinit var inst : NationService private set
    }

    private lateinit var launcher: PalmNationsLauncher
    lateinit var nations: NationService private set
    private lateinit var executor: ExecutorService

    override fun onEnable() {
        val config = BungeeYamlConfiguration(this)
        val networkModule = PalmLibrary.network
        val dataSource = PalmLibrary.dataSource
        val worldModifier = BungneeWorldModifier()
        val unloadPolicy = BungeeUnloadPolicy()
        executor = Executors.newCachedThreadPool()

        launcher = PalmNationsLauncher(config, networkModule, dataSource,
        unloadPolicy, worldModifier, executor, executor)
        nations = launcher.launch()
        inst = nations
        ProxyServer.getInstance().pluginManager
            .registerListener(this, BungeeMemberListener(PlayerLoader(nations)))
    }

    override fun onDisable() {
        nations.shutdown()
        launcher.shutdown()
    }

}