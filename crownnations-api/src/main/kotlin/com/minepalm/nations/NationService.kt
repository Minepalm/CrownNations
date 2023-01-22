package com.minepalm.nations

import com.minepalm.nations.bank.NationBankRegistry
import com.minepalm.nations.config.NationConfigurations
import com.minepalm.nations.grade.NationGradeService
import com.minepalm.nations.server.NationNetwork
import com.minepalm.nations.territory.NationTerritoryService
import com.minepalm.nations.utils.ServerLoc
import com.minepalm.nations.war.NationWarService

interface NationService {

    val config: NationConfigurations

    val nationRegistry: NationRegistry
    val memberRegistry: NationMemberRegistry
    val operationFactory: NationOperationFactory
    val network: NationNetwork

    val bankRegistry: NationBankRegistry
    val gradeService: NationGradeService
    val territoryService: NationTerritoryService
    val warService: NationWarService

    val localEventBus: NationEventBus
    val remoteEventBus: NationEventBus

    fun shutdown()

    fun operateFoundation(
        commander: NationMember,
        nationName: String,
        loc: ServerLoc
    ): NationOperation<Nation>

    fun createNewNation(name: String): Nation?

}