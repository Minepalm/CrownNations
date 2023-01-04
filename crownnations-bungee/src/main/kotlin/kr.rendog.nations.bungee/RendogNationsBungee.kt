package kr.rendog.nations.bungee

import com.minepalm.library.PalmLibrary
import kr.rendog.nations.NationService
import kr.rendog.nations.core.RendogNationsLauncher
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.plugin.Plugin
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class RendogNationsBungee : Plugin(){

    companion object{
        lateinit var inst : NationService private set
    }

    private lateinit var launcher: RendogNationsLauncher
    lateinit var nations: NationService private set
    private lateinit var executor: ExecutorService

    override fun onEnable() {
        val config = BungeeYamlConfiguration(this)
        val networkModule = PalmLibrary.network
        val dataSource = PalmLibrary.dataSource
        val worldModifier = BungneeWorldModifier()
        val unloadPolicy = BungeeUnloadPolicy()
        executor = Executors.newCachedThreadPool()

        launcher = RendogNationsLauncher(config, networkModule, dataSource,
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