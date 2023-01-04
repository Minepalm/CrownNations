package kr.rendog.nations

import kr.rendog.nations.bank.NationBankRegistry
import kr.rendog.nations.config.NationConfigurations
import kr.rendog.nations.grade.NationGradeService
import kr.rendog.nations.server.NationNetwork
import kr.rendog.nations.territory.NationTerritoryService
import kr.rendog.nations.utils.ServerLoc
import kr.rendog.nations.war.NationWarService

interface NationService {

    val config: NationConfigurations

    val nationRegistry : NationRegistry
    val memberRegistry : NationMemberRegistry
    val operationFactory : NationOperationFactory
    val network : NationNetwork

    val bankRegistry : NationBankRegistry
    val gradeService : NationGradeService
    val territoryService : NationTerritoryService
    val warService: NationWarService

    val localEventBus : NationEventBus
    val remoteEventBus : NationEventBus

    fun shutdown()

    fun operateFoundation(commander: NationMember, nationName: String, loc: ServerLoc): NationOperation<Nation>

    fun createNewNation(name : String) : Nation?

}