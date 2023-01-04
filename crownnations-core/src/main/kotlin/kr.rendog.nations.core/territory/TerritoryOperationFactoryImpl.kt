package kr.rendog.nations.core.territory

import kr.rendog.nations.NationMember
import kr.rendog.nations.NationOperation
import kr.rendog.nations.config.TerritoryConfiguration
import kr.rendog.nations.core.operation.*
import kr.rendog.nations.territory.*
import kr.rendog.nations.utils.ServerLoc

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