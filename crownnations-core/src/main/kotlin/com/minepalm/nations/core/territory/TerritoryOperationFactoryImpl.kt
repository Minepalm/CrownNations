package com.minepalm.nations.core.territory

import com.minepalm.nations.NationMember
import com.minepalm.nations.NationOperation
import com.minepalm.nations.config.TerritoryConfiguration
import com.minepalm.nations.core.operation.*
import com.minepalm.nations.territory.*
import com.minepalm.nations.utils.ServerLoc

class TerritoryOperationFactoryImpl(
    private val service: NationTerritoryService,
    private val config: TerritoryConfiguration
) : TerritoryOperationFactory {
    override fun buildOperateClaimCastleNationCreate(
        commander: NationMember,
        loc: ServerLoc
    ): NationOperation<NationCastle> {
        return OperationClaimCastleAsNationCreate(service, config, loc, commander)
    }

    override fun buildOperateModify(
        monument: NationMonument,
        commander: NationMember,
        action: NationAction,
        loc: ServerLoc
    ): NationOperation<Boolean> {
        return OperationTerritoryModify(service, monument, action, loc, commander)
    }

    override fun buildOperateClaimCastle(
        territory: NationTerritory,
        commander: NationMember,
        loc: ServerLoc
    ): NationOperation<NationCastle> {
        return OperationClaimCastle(service, config, territory, loc, commander)
    }

    override fun buildOperateClaimOutpost(
        territory: NationTerritory,
        commander: NationMember,
        loc: ServerLoc
    ): NationOperation<NationOutpost> {
        return OperationClaimOutpost(service, config, territory, loc, commander)
    }

    override fun buildOperationDecomposeCastle(
        monument: NationCastle,
        commander: NationMember,
        reason: String
    ): NationOperation<Boolean> {
        return OperationDecomposeCastle(service, monument, reason, commander)
    }

    override fun buildOperationDecomposeOutpost(
        monument: NationOutpost,
        commander: NationMember,
        reason: String
    ): NationOperation<Boolean> {
        return OperationDecomposeOutpost(service, monument, commander)
    }

}