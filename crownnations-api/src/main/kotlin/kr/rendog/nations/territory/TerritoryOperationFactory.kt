package kr.rendog.nations.territory

import kr.rendog.nations.NationMember
import kr.rendog.nations.NationOperation
import kr.rendog.nations.utils.ServerLoc

interface TerritoryOperationFactory {

    fun buildOperateClaimCastleNationCreate(commander: NationMember, loc: ServerLoc)
    : NationOperation<NationCastle>

    fun buildOperateModify(monument: NationMonument, commander: NationMember, action: NationAction, loc: ServerLoc)
    : NationOperation<Boolean>

    fun buildOperateClaimCastle(territory: NationTerritory, commander: NationMember, loc: ServerLoc)
    : NationOperation<NationCastle>

    fun buildOperateClaimOutpost(territory: NationTerritory, commander: NationMember, loc: ServerLoc)
    : NationOperation<NationOutpost>

    fun buildOperationDecomposeCastle(monument: NationCastle, commander: NationMember, reason: String)
    : NationOperation<Boolean>

    fun buildOperationDecomposeOutpost(monument: NationOutpost, commander: NationMember, reason: String)
    : NationOperation<Boolean>

}