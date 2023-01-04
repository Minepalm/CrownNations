package kr.rendog.nations.bukkit

import co.aikar.commands.BukkitCommandManager
import com.minepalm.arkarangutils.bukkit.BukkitExecutor
import com.minepalm.arkarangutils.invitation.InvitationService
import com.minepalm.library.PalmLibrary
import com.minepalm.palmchat.api.PalmChat
import kr.rendog.nations.NationService
import kr.rendog.nations.bukkit.commands.AdminCommands
import kr.rendog.nations.bukkit.commands.UserCommands
import kr.rendog.nations.bukkit.config.YamlMessageFile
import kr.rendog.nations.bukkit.config.YamlNationConfigurations
import kr.rendog.nations.bukkit.invitation.MemberInvitationStrategy
import kr.rendog.nations.bukkit.invitation.MySQLNationInvitationDatabase
import kr.rendog.nations.bukkit.invitation.PalmLibraryInvitationLoad
import kr.rendog.nations.bukkit.listener.bukkit.BukkitGeneralListener
import kr.rendog.nations.bukkit.listener.bukkit.NationMonumentClaimListener
import kr.rendog.nations.bukkit.listener.nation.AlertListenerInitializer
import kr.rendog.nations.bukkit.message.PrinterRegistry
import kr.rendog.nations.bukkit.listener.bukkit.NationTeamProtectionListener
import kr.rendog.nations.bukkit.listener.bukkit.NationTerritoryProtectionListener
import kr.rendog.nations.bukkit.territory.SchematicStorage
import kr.rendog.nations.bukkit.territory.SchematicWorldModifier
import kr.rendog.nations.core.RendogNationsLauncher
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class RendogNationsBukkit : JavaPlugin(){

    companion object{
        lateinit var inst : NationService private set
    }

    private lateinit var launcher: RendogNationsLauncher
    lateinit var nations: NationService private set

    private lateinit var players: PlayerCache
    private lateinit var broadcaster: NetworkBroadcaster
    private lateinit var printerRegistry: PrinterRegistry

    private lateinit var invitationExecutor: ExecutorService

    private lateinit var sessions: CreationSessionRegistry

    override fun onEnable() {
        invitationExecutor = Executors.newCachedThreadPool()
        val bukkitExecutor = BukkitExecutor(this, Bukkit.getScheduler())

        val networkModule = PalmLibrary.network
        val playerModule = PalmLibrary.players
        val chatModule = PalmChat.inst

        val config = YamlNationConfigurations(this)
        val messageYaml = YamlMessageFile(this)

        printerRegistry = PrinterRegistry()
        messageYaml.read().forEach { printerRegistry.register(it.value) }

        val dataSource = PalmLibrary.dataSource
        val mysqlAddon = dataSource.mysql(config.mysql("addon"))
        val mysqlMisc = dataSource.mysql(config.mysql("misc"))

        players = PlayerCache(playerModule)
        broadcaster = NetworkBroadcaster(chatModule, mysqlAddon)
        launcher = RendogNationsLauncher(YamlNationConfigurations(this), networkModule, dataSource,
            BukkitUnloadPolicy(),
            SchematicWorldModifier(SchematicStorage(File(dataFolder, "schematics")), bukkitExecutor),
            Executors.newCachedThreadPool(),
            Executors.newCachedThreadPool())
        nations = launcher.launch()
        inst = nations

        sessions = CreationSessionRegistry(nations)
        val invitationService = InvitationService(PalmLibraryInvitationLoad("nation_invitation", mysqlMisc, 30000L),
            MemberInvitationStrategy(nations, players, chatModule, printerRegistry["ADD_MEMBER"], invitationExecutor))
        val invitationDatabase = MySQLNationInvitationDatabase(mysqlMisc, "rendognations_invitations")

        AlertListenerInitializer.init(nations, players, printerRegistry, broadcaster)

        BukkitCommandManager(this).apply {
            registerCommand(
                UserCommands(nations, players, printerRegistry, sessions, invitationService, invitationDatabase, bukkitExecutor)
            )
            registerCommand(
                AdminCommands(nations, players, bukkitExecutor)
            )
        }
        Bukkit.getPluginManager().registerEvents(BukkitGeneralListener(PlayerLoader(nations)), this)
        Bukkit.getPluginManager().registerEvents(NationTeamProtectionListener(nations), this)
        Bukkit.getPluginManager().registerEvents(NationTerritoryProtectionListener(nations), this)
        Bukkit.getPluginManager().registerEvents(
            NationMonumentClaimListener(printerRegistry, nations, sessions, config.territory, bukkitExecutor), this)

    }

    override fun onDisable() {
        nations.shutdown()
        invitationExecutor.shutdown()
    }
}