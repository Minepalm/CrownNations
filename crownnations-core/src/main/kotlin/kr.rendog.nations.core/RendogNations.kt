package kr.rendog.nations.core

import com.minepalm.library.database.impl.internal.MySQLDB
import com.minepalm.library.network.api.PalmNetwork
import kr.rendog.nations.*
import kr.rendog.nations.bank.NationBankRegistry
import kr.rendog.nations.config.NationConfigurations
import kr.rendog.nations.grade.NationGradeService
import kr.rendog.nations.core.mysql.*
import kr.rendog.nations.core.network.RendogNationNetwork
import kr.rendog.nations.core.operation.OperationNationFoundation
import kr.rendog.nations.server.NationNetwork
import kr.rendog.nations.territory.NationTerritoryService
import kr.rendog.nations.utils.ServerLoc
import kr.rendog.nations.war.NationWarService
import java.time.Duration
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class RendogNations(
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

    private val memberFactory: RendogNationMemberFactory
    private val nationFactory: RendogNationFactory

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

    init{
        memberExecutorThreadPool = Executors.newCachedThreadPool()
        nationExecutorThreadPool = Executors.newCachedThreadPool()
        loopExecutor = Executors.newSingleThreadExecutor()
        updateExecutor = Executors.newCachedThreadPool()

        nationServerDatabase = MySQLNationServerDatabase(nationsSource, "rendognations_servers")
        adminDatabase = MySQLAdminDatabase(nationsSource, "rendognations_admins")
        nationIdDatabase = MySQLNationIdDatabase(nationsSource, "rendognations_ids")
        nationDataDatabase = MySQLNationDataDatabase(nationsSource, "rendognations_nation_data")
        nationMemberDataDatabase = MySQLNationMemberDatabase(membersSource, "rendognations_members")

        network = RendogNationNetwork(networkModule, nationServerDatabase)
        admins = NationAdmins(adminDatabase, network)

        localEventBus = RendogLocalEventBus(network)
        remoteEventBus = RendogEventBus()

        memberFactory = RendogNationMemberFactory(this, nationMemberDataDatabase, admins, memberExecutorThreadPool)
        nationFactory = RendogNationFactory(nationDataDatabase, nationMemberDataDatabase, nationIdDatabase, this, nationExecutorThreadPool)
        val cleaner = RendogNationRegistryCleaner(this, loopExecutor, Duration.ofMinutes(5), 50L)

        memberRegistry = RendogNationMemberRegistry(memberFactory, memberExecutorThreadPool)
        nationRegistry = RendogNationRegistry(policy, nationFactory, localEventBus, nationMemberDataDatabase, nationIdDatabase, updateExecutor, cleaner)
        cleaner.start()

        operationFactory = RendogNationOperationFactory(this)

    }

    override fun createNewNation(name: String): Nation? {
        val newNationId = nationIdDatabase.generateNewId(name).join()
        return if(newNationId == -1){
            null
        }else{
            nationFactory.build(newNationId, name).join()
        }
    }

    override fun shutdown() {
        memberExecutorThreadPool.shutdown()
        nationExecutorThreadPool.shutdown()
        loopExecutor.shutdown()
    }

    override fun operateFoundation(
        commander: NationMember,
        nationName: String,
        loc: ServerLoc
    ): NationOperation<Nation> {
        return OperationNationFoundation(this, commander, nationName, loc)
    }



}