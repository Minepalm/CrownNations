package com.minepalm.nations.core

import com.minepalm.library.database.impl.internal.MySQLDB
import com.minepalm.library.network.api.PalmNetwork
import com.minepalm.nations.*
import com.minepalm.nations.bank.NationBankRegistry
import com.minepalm.nations.config.NationConfigurations
import com.minepalm.nations.core.mysql.*
import com.minepalm.nations.core.network.CrownNationNetwork
import com.minepalm.nations.core.operation.OperationNationFoundation
import com.minepalm.nations.grade.NationGradeService
import com.minepalm.nations.server.NationNetwork
import com.minepalm.nations.territory.NationTerritoryService
import com.minepalm.nations.war.NationWarService
import java.time.Duration
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class PalmNationsService(
    nationsSource: MySQLDB,
    membersSource: MySQLDB,
    networkModule: PalmNetwork,
    policy: NationRegistry.Policy,
    override val config: NationConfigurations
) : NationService {

    override val network: NationNetwork
    override val memberRegistry: NationMemberRegistry
    override val nationRegistry: NationRegistry

    override val localEventBus: NationEventBus
    override val remoteEventBus: NationEventBus

    val admins: NationAdmins

    override val operationFactory: NationOperationFactory
    override val gradeService: NationGradeService
        get() = Dependencies[NationGradeService::class.java].get()
    override val territoryService: NationTerritoryService
        get() = Dependencies[NationTerritoryService::class.java].get()
    override val warService: NationWarService
        get() = throw UnsupportedOperationException("not implemented")
            //Dependencies[NationWarService::class.java].get()
    override val bankRegistry: NationBankRegistry
        get() = Dependencies[NationBankRegistry::class.java].get()

    private val memberFactory: PalmNationMemberFactory
    private val nationFactory: PalmNationFactory

    private val nationServerDatabase: MySQLNationServerDatabase
    private val adminDatabase: MySQLAdminDatabase
    private val nationIdDatabase: MySQLNationIdDatabase
    private val nationDataDatabase: MySQLNationDataDatabase
    private val nationMemberDataDatabase: MySQLNationMemberDatabase

    //todo: create nation executor pool
    private val memberExecutorThreadPool: ExecutorService
    private val nationExecutorThreadPool: ExecutorService
    private val loopExecutor: ExecutorService
    private val updateExecutor: ExecutorService

    init {
        memberExecutorThreadPool = Executors.newCachedThreadPool()
        nationExecutorThreadPool = Executors.newCachedThreadPool()
        loopExecutor = Executors.newSingleThreadExecutor()
        updateExecutor = Executors.newCachedThreadPool()

        nationServerDatabase = MySQLNationServerDatabase(nationsSource, "crownnations_servers")
        adminDatabase = MySQLAdminDatabase(nationsSource, "crownnations_admins")
        nationIdDatabase = MySQLNationIdDatabase(nationsSource, "crownnations_ids")
        nationDataDatabase = MySQLNationDataDatabase(nationsSource, "crownnations_nation_data")
        nationMemberDataDatabase = MySQLNationMemberDatabase(membersSource, "crownnations_members")

        network = CrownNationNetwork(networkModule, nationServerDatabase)
        admins = NationAdmins(adminDatabase, network)

        localEventBus = PalmLocalEventBus(network)
        remoteEventBus = PalmEventBus()

        memberFactory = PalmNationMemberFactory(this, nationMemberDataDatabase, admins, memberExecutorThreadPool)
        nationFactory = PalmNationFactory(
            nationDataDatabase,
            nationMemberDataDatabase,
            nationIdDatabase,
            this,
            nationExecutorThreadPool
        )
        val cleaner = PalmNationRegistryCleaner(this, loopExecutor, Duration.ofMinutes(5), 50L)

        memberRegistry = PalmNationMemberRegistry(memberFactory, memberExecutorThreadPool)
            .initAs(NationMemberRegistry::class)
        nationRegistry = PalmNationRegistry(policy, nationFactory, localEventBus, nationMemberDataDatabase, nationIdDatabase, updateExecutor, cleaner)
            .initAs(NationRegistry::class)
        cleaner.start()

        operationFactory = PalmNationOperationFactory(this)

    }

    override fun createNewNation(name: String): Nation? {
        val newNationId = nationIdDatabase.generateNewId(name).join()
        return if (newNationId == -1) {
            null
        } else {
            nationFactory.build(newNationId, name).join()
        }
    }

    override fun forceDelete(id: Int): CompletableFuture<Boolean> {
        return nationIdDatabase.deleteId(id)
    }

    override fun shutdown() {
        memberExecutorThreadPool.shutdown()
        nationExecutorThreadPool.shutdown()
        loopExecutor.shutdown()
    }

    override fun operateFoundation(
        commander: NationMember,
        nationName: String,
        loc: com.minepalm.nations.utils.ServerLoc
    ): NationOperation<Nation> {
        return OperationNationFoundation(this, commander, nationName, loc)
    }



}