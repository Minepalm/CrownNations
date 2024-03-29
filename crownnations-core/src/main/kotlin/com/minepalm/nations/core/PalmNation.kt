package com.minepalm.nations.core

import com.minepalm.nations.*
import java.util.*

class PalmNation(
    override val id: Int,
    override val name : String,
    override val cache: Nation.Cache,
    override val unsafe: Nation.Unsafe,
    override val direct: Nation.Direct,
    override val metadata: NationMetadata,
    private val service : NationService
) : Nation {

    override val bank: com.minepalm.nations.bank.NationBank
        get() = service.bankRegistry[id]
    override val grade: com.minepalm.nations.grade.NationGrade
        get() = service.gradeService.registry[id]!!
    private val factory : NationOperationFactory
        get() = service.operationFactory
    override val territory: com.minepalm.nations.territory.NationTerritory
        get() = service.territoryService.territoryRegistry[id]!!
    override val war: com.minepalm.nations.war.NationWar
        get() = TODO("Not yet implemented")

    override fun operateSetMember(commander: NationMember, uniqueId: UUID, rank: NationRank): NationOperation<Boolean> {
        return factory.buildSetMember(this, commander, uniqueId, rank)
    }

    override fun operateAddMember(commander: NationMember, uniqueId: UUID): NationOperation<Boolean> {
        return factory.buildAddMember(this, commander, uniqueId)
    }

    override fun operateKickMember(commander: NationMember, uniqueId: UUID): NationOperation<Boolean> {
        return factory.buildKickMember(this, commander, uniqueId)
    }

    override fun operateTransferOwner(commander: NationMember, uniqueId: UUID): NationOperation<Boolean> {
        return factory.buildTransferOwner(this, commander, uniqueId)
    }

    override fun operateDisband(commander: NationMember): NationOperation<Boolean> {
        return factory.buildDisband(this, commander)
    }

    override fun operateChangeMetadata(commander: NationMember, key: String, value: String): NationOperation<Boolean> {
        return factory.buildChangeMetadata(this, commander, key, value)
    }

}