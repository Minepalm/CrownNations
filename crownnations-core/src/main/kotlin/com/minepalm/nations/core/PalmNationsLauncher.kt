package com.minepalm.nations.core

import com.minepalm.library.database.PalmDataSources
import com.minepalm.library.database.impl.internal.MySQLDB
import com.minepalm.library.network.api.PalmNetwork
import com.minepalm.nations.Dependencies
import com.minepalm.nations.NationRegistry
import com.minepalm.nations.NationService
import com.minepalm.nations.bank.NationBankRegistry
import com.minepalm.nations.core.bank.PalmNationBankRegistry
import com.minepalm.nations.core.grade.PalmNationGradeService
import com.minepalm.nations.core.listener.NationEventListenerInitializer
import com.minepalm.nations.core.network.HelloBungeeInitializer
import com.minepalm.nations.core.territory.PalmNationTerritoryService
import com.minepalm.nations.grade.NationGradeService
import com.minepalm.nations.territory.NationTerritoryService
import java.util.concurrent.ExecutorService

class PalmNationsLauncher(
    private val config: com.minepalm.nations.config.NationConfigurations,
    private val networkModule: PalmNetwork,
    private val dataSource: PalmDataSources,
    private val policy: NationRegistry.Policy,
    private val worldModifier: com.minepalm.nations.territory.WorldModifier,
    //todo: create nation executor pool
    private val territoryExecutor: ExecutorService,
    private val syncExecutor: ExecutorService
    ) {

    private lateinit var nations: PalmNationsService

    fun launch(): PalmNationsService {
        nations = PalmNationsService(mysql("nation"), mysql("member"), networkModule, policy, config)

        HelloBungeeInitializer.apply(networkModule, nations)
        NationEventListenerInitializer.init(nations, syncExecutor)

        Dependencies.register(NationService::class.java, nations)
        Dependencies.register(NationTerritoryService::class.java, buildTerritoryService(nations, worldModifier))
        Dependencies.register(NationBankRegistry::class.java, buildBankRegistry(nations))
        Dependencies.register(NationGradeService::class.java, buildGradeService(nations))

        nations.territoryService.universe.host.deleteInvalidateMonuments()
        return nations

    }

    fun shutdown(){
        nations.shutdown()
    }

    private fun buildTerritoryService(service: NationService, modifier: com.minepalm.nations.territory.WorldModifier): com.minepalm.nations.territory.NationTerritoryService {
        return PalmNationTerritoryService(service, config.territory,
            modifier, mysql("territory"), mysql("schematic"), territoryExecutor)
    }

    private fun buildBankRegistry(service: NationService): com.minepalm.nations.bank.NationBankRegistry {
        return PalmNationBankRegistry(service, mysql("bank"))
    }

    private fun buildGradeService(service: NationService): com.minepalm.nations.grade.NationGradeService {
        return PalmNationGradeService(service, mysql("grade"), config.grade)
    }

    private fun mysql(name: String): MySQLDB {
        return dataSource.mysql(config.mysql(name))
    }
}