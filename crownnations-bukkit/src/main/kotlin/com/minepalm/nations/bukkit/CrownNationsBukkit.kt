package com.minepalm.nations.bukkit

import co.aikar.commands.BukkitCommandManager
import com.minepalm.arkarangutils.bukkit.BukkitExecutor
import com.minepalm.arkarangutils.invitation.InvitationService
import com.minepalm.bungeejump.impl.BungeeJump
import com.minepalm.bungeejump.impl.bukkit.BungeeJumpBukkit
import com.minepalm.library.PalmLibrary
import com.minepalm.library.network.api.PalmNetwork
import com.minepalm.library.network.impl.player.NetworkPlayers
import com.minepalm.nations.Dependencies
import com.minepalm.nations.NationService
import com.minepalm.nations.bukkit.bank.PalmCoconutEconomyAdapter
import com.minepalm.nations.bukkit.commands.AdminCommands
import com.minepalm.nations.bukkit.commands.DebugCommands
import com.minepalm.nations.bukkit.commands.UserCommands
import com.minepalm.nations.bukkit.config.IconRepository
import com.minepalm.nations.bukkit.config.YamlGUIIconConfig
import com.minepalm.nations.bukkit.config.YamlMessageFile
import com.minepalm.nations.bukkit.config.YamlNationConfigurations
import com.minepalm.nations.bukkit.gui.GUIFactory
import com.minepalm.nations.bukkit.gui.IconFactory
import com.minepalm.nations.bukkit.invitation.MemberInvitationStrategy
import com.minepalm.nations.bukkit.invitation.MySQLNationInvitationDatabase
import com.minepalm.nations.bukkit.invitation.PalmLibraryInvitationLoad
import com.minepalm.nations.bukkit.listener.bukkit.*
import com.minepalm.nations.bukkit.listener.nation.AlertListenerInitializer
import com.minepalm.nations.bukkit.message.PrinterRegistry
import com.minepalm.nations.bukkit.territory.SchematicStorage
import com.minepalm.nations.bukkit.territory.SchematicWorldModifier
import com.minepalm.nations.bukkit.warp.WarpExecutor
import com.minepalm.nations.config.NationConfigurations
import com.minepalm.nations.core.PalmNationsLauncher
import com.minepalm.nations.core.bank.EconomyAdapter
import com.minepalm.nations.initAs
import com.minepalm.palmchat.api.ChatService
import com.minepalm.palmchat.api.PalmChat
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CrownNationsBukkit : JavaPlugin(){

    companion object{
        lateinit var inst : NationService private set
    }

    private lateinit var launcher: PalmNationsLauncher
    lateinit var nations: NationService private set

    private lateinit var players: PlayerCache
    private lateinit var broadcaster: NetworkBroadcaster
    private lateinit var printerRegistry: PrinterRegistry

    private lateinit var invitationExecutor: ExecutorService

    private lateinit var sessions: CreationSessionRegistry
    private lateinit var config: NationConfigurations

    override fun onEnable() {
        invitationExecutor = Executors.newCachedThreadPool()
        val bukkitExecutor = BukkitExecutor(this, Bukkit.getScheduler()).initAs(BukkitExecutor::class)

        val bungeeJump = BungeeJumpBukkit.getService().initAs(BungeeJump::class)
        val networkModule = PalmLibrary.network.initAs(PalmNetwork::class)
        val playerModule = PalmLibrary.players.initAs(NetworkPlayers::class)
        val chatModule = PalmChat.inst.initAs(ChatService::class)

        config = YamlNationConfigurations(this)
        val messageYaml = YamlMessageFile(this)

        printerRegistry = PrinterRegistry().initAs(PrinterRegistry::class)
        messageYaml.read().forEach { printerRegistry.register(it.value) }

        val dataSource = PalmLibrary.dataSource
        val mysqlAddon = dataSource.mysql(config.mysql("addon"))
        val mysqlMisc = dataSource.mysql(config.mysql("misc"))
        Dependencies.register(EconomyAdapter::class.java, PalmCoconutEconomyAdapter())

        players = PlayerCache(playerModule).initAs(PlayerCache::class)
        broadcaster = NetworkBroadcaster(chatModule, mysqlAddon)
        launcher = PalmNationsLauncher(
            config, networkModule, dataSource,
            BukkitUnloadPolicy(),
            SchematicWorldModifier(
                SchematicStorage(File(dataFolder, "schematics"), config.territory),
                bukkitExecutor,
                config.territory
            ),
            Executors.newCachedThreadPool(),
            Executors.newCachedThreadPool()
        )


        nations = launcher.launch()
        inst = nations

        sessions = CreationSessionRegistry(nations)
        val invitationService = InvitationService(PalmLibraryInvitationLoad("nation_invitation", mysqlMisc, 30000L),
            MemberInvitationStrategy(nations, players, chatModule, printerRegistry["ADD_MEMBER"], invitationExecutor))
        val invitationDatabase = MySQLNationInvitationDatabase(mysqlMisc, "crownnations_invitations")

        AlertListenerInitializer.init(nations, players, printerRegistry, broadcaster)

        BukkitCommandManager(this).apply {
            registerCommand(
                UserCommands(
                    sessions,
                    invitationService,
                    invitationDatabase
                )
            )
            registerCommand(
                AdminCommands(nations, players, bukkitExecutor)
            )
            registerCommand(DebugCommands(nations))
        }
        Bukkit.getPluginManager().registerEvents(BukkitGeneralListener(PlayerLoader(nations), players), this)
        Bukkit.getPluginManager().registerEvents(NationTeamProtectionListener(nations), this)
        Bukkit.getPluginManager().registerEvents(NationTerritoryProtectionListener(nations), this)
        Bukkit.getPluginManager().registerEvents(
            NationMonumentClaimListener(printerRegistry, nations, sessions, config.territory, bukkitExecutor), this
        )
        Bukkit.getPluginManager().registerEvents(NationTerritoryJoinListener(nations.territoryService), this)

        initGUI()
        initWarp(bungeeJump, bukkitExecutor)
    }

    private fun initWarp(bungeeJump: BungeeJump, executor: BukkitExecutor) {
        val warp = WarpExecutor(bungeeJump, executor).initAs(WarpExecutor::class)
        warp.registerListener(this, Bukkit.getServer())
    }

    private fun initGUI() {
        val iconRepository = IconRepository().initAs(IconRepository::class)
        val iconFactory = IconFactory(printerRegistry).initAs(IconFactory::class)
        val guiIconConfig = YamlGUIIconConfig(this)
        guiIconConfig.read().forEach { iconRepository.register(it.key, it.value) }
        val factory = GUIFactory().initAs(GUIFactory::class)
    }

    override fun onDisable() {
        nations.shutdown()
        invitationExecutor.shutdown()
    }

}