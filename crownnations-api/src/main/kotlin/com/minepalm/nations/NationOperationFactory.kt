package com.minepalm.nations

import java.util.*

interface NationOperationFactory {

    fun buildCreate(commander: NationMember, name: String): NationOperation<Nation>

    fun buildSetMember(
        nation: Nation,
        commander: NationMember,
        uniqueId: UUID,
        rank: NationRank
    ): NationOperation<Boolean>

    fun buildAddMember(nation: Nation, commander: NationMember, uniqueId: UUID): NationOperation<Boolean>

    fun buildKickMember(nation: Nation, commander: NationMember, uniqueId: UUID): NationOperation<Boolean>

    fun buildTransferOwner(nation: Nation, commander: NationMember, uniqueId: UUID): NationOperation<Boolean>

    fun buildDisband(nation: Nation, commander: NationMember): NationOperation<Boolean>

    fun buildChangeMetadata(
        nation: Nation,
        commander: NationMember,
        key: String,
        value: String
    ): NationOperation<Boolean>

    fun buildLeave(commander: NationMember): NationOperation<Boolean>
}