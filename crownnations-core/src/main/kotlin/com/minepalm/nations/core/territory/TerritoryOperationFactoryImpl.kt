package com.minepalm.nations.core.territory

import com.minepalm.nations.NationMember
import com.minepalm.nations.NationOperation
import com.minepalm.nations.core.operation.*

class TerritoryOperationFactoryImpl(
    private val service: com.minepalm.nations.territory.NationTerritoryService,
    private val config: com.minepalm.nations.config.TerritoryConfiguration
) : com.minepalm.nations.territory.TerritoryOperationFactory {
    override fun buildOperateClaimCastleNationCreate(
        commander: NationMember,
        loc: com.minepalm.nations.utils.ServerLoc
    ): NationOperation<com.minepalm.nations.territory.NationCastle> {
        return OperationClaimCastleAsNationCreate(service, config, loc, commander)
    }

    override fun buildOperateModify(
        monument: com.minepalm.nations.territory.NationMonument,
        commander: NationMember,
        action: com.minepalm.nations.territory.NationAction,
        loc: com.minepalm.nations.utils.ServerLoc
    ): NationOperation<Boolean> {
        return OperationTerritoryModify(service, monument, action, loc, commander)
    }

    override fun buildOperateClaimCastle(
        territory: com.minepalm.nations.territory.NationTerritory,
        commander: NationMember,
        loc: com.minepalm.nations.utils.ServerLoc
    ): NationOperation<com.minepalm.nations.territory.NationCastle> {
        return OperationClaimCastle(service, config, territory, loc, commander)
    }

    override fun buildOperateClaimOutpost(
        territory: com.minepalm.nations.territory.NationTerritory,
        commander: NationMember,
        loc: com.minepalm.nations.utils.ServerLoc
    ): NationOperation<com.minepalm.nations.territory.NationOutpost> {
        return OperationClaimOutpost(service, config, territory, loc, commander)
    }

    override fun buildOperationDecomposeCastle(
        monument: com.minepalm.nations.territory.NationCastle,
        commander: NationMember,
        reason: String
    ): NationOperation<Boolean> {
        return OperationDecomposeCastle(service, monument, reason, commander)
    }

    override fun buildOperationDecomposeOutpost(
        monument: com.minepalm.nations.territory.NationOutpost,
        commander: NationMember,
        reason: String
    ): NationOperation<Boolean> {
        return OperationDecomposeOutpost(service, monument, commander)
    }

}