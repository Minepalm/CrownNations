package kr.rendog.nations.core

import com.minepalm.library.database.PalmDataSources
import com.minepalm.library.database.impl.internal.MySQLDB
import com.minepalm.library.network.api.PalmNetwork
import kr.rendog.nations.Dependencies
import kr.rendog.nations.NationRegistry
import kr.rendog.nations.NationService
import kr.rendog.nations.bank.NationBankRegistry
import kr.rendog.nations.config.NationConfigurations
import kr.rendog.nations.core.bank.RendogNationBankRegistry
import kr.rendog.nations.core.grade.RendogNationGradeService
import kr.rendog.nations.core.listener.NationEventListenerInitializer
import kr.rendog.nations.core.network.HelloBungeeInitializer
import kr.rendog.nations.core.territory.RendogNationTerritoryService
import kr.rendog.nations.grade.NationGradeService
import kr.rendog.nations.territory.NationTerritoryService
import kr.rendog.nations.territory.WorldModifier
import java.util.concurrent.ExecutorService

class RendogNationsLauncher(
    private val config: NationConfigurations,
    private val networkModule: PalmNetwork,
    private val dataSource: PalmDataSources,
    private val policy: NationRegistry.Policy,
    private val worldModifier: WorldModifier,
    //todo: create nation executor pool
    private val territoryExecutor: ExecutorService,
    private val syncExecutor: ExecutorService
    ) {

    private lateinit var nations: RendogNations

    fun launch(): RendogNations{
        nations = RendogNations(mysql("nation"), mysql("member"), networkModule, policy, config)

        HelloBungeeInitializer.apply(networkModule, nations)
        NationEventListenerInitializer.init(nations, syncExecutor)

        Dependencies.register(NationTerritoryService::class.java, buildTerritoryService(nations, worldModifier))
        Dependencies.register(NationBankRegistry::class.java, buildBankRegistry(nations))
        Dependencies.register(NationGradeService::class.java, buildGradeService(nations))

        nations.territoryService.universe.host.deleteInvalidateMonuments()
        return nations

    }

    fun shutdown(){
        nations.shutdown()
    }

    private fun buildTerritoryService(service: NationService, modifier: WorldModifier): NationTerritoryService {
        return RendogNationTerritoryService(service, config.territory,
            modifier, mysql("territory"), mysql("schematic"), territoryExecutor)
    }

    private fun buildBankRegistry(service: NationService): NationBankRegistry {
        return RendogNationBankRegistry(service, mysql("bank"))
    }

    private fun buildGradeService(service: NationService): NationGradeService {
        return RendogNationGradeService(service, mysql("grade"), config.grade)
    }

    private fun mysql(name: String): MySQLDB {
        return dataSource.mysql(config.mysql(name)) ?: dataSource.mysql("default")
    }
}