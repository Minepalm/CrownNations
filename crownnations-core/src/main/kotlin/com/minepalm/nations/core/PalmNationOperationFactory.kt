package com.minepalm.nations.core

import com.minepalm.nations.*
import com.minepalm.nations.core.operation.*
import java.util.*

class PalmNationOperationFactory(
    val service : NationService
) : NationOperationFactory {
    override fun buildCreate(commander: NationMember, name : String): NationOperation<Nation> {
        return OperationCreate(name, commander, service)
    }

    override fun buildSetMember(
        nation: Nation,
        commander: NationMember,
        uniqueId: UUID,
        rank: NationRank
    ): NationOperation<Boolean> {
        return OperationSetRank(nation, commander, service.memberRegistry[uniqueId], rank, service)
    }

    override fun buildAddMember(nation: Nation, commander: NationMember, uniqueId: UUID): NationOperation<Boolean> {
        return OperationAddMember(nation, commander, service.memberRegistry[uniqueId], service)
    }

    override fun buildKickMember(nation: Nation, commander: NationMember, uniqueId: UUID): NationOperation<Boolean> {
        return OperationRemoveMember(nation, commander, service.memberRegistry[uniqueId], service)
    }

    override fun buildTransferOwner(nation: Nation, commander: NationMember, uniqueId: UUID): NationOperation<Boolean> {
        return OperationTransfer(nation, commander, service.memberRegistry[uniqueId], service)
    }

    override fun buildDisband(nation: Nation, commander: NationMember): NationOperation<Boolean> {
        return OperationDisband(nation, commander, service)
    }

    override fun buildChangeMetadata(
        nation: Nation,
        commander: NationMember,
        key: String,
        value: String
    ): NationOperation<Boolean> {
        return OperationMetadataChange(nation, commander, key, value, service)
    }

    override fun buildLeave(commander: NationMember): NationOperation<Boolean> {
        return OperationLeave(commander, service)
    }
}