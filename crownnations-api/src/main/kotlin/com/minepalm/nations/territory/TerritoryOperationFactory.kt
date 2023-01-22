package com.minepalm.nations.territory

import com.minepalm.nations.NationMember
import com.minepalm.nations.NationOperation

interface TerritoryOperationFactory {

    fun buildOperateClaimCastleNationCreate(commander: NationMember, loc: com.minepalm.nations.utils.ServerLoc)
            : NationOperation<NationCastle>

    fun buildOperateModify(
        monument: NationMonument,
        commander: NationMember,
        action: NationAction,
        loc: com.minepalm.nations.utils.ServerLoc
    )
            : NationOperation<Boolean>

    fun buildOperateClaimCastle(
        territory: NationTerritory,
        commander: NationMember,
        loc: com.minepalm.nations.utils.ServerLoc
    )
            : NationOperation<NationCastle>

    fun buildOperateClaimOutpost(
        territory: NationTerritory,
        commander: NationMember,
        loc: com.minepalm.nations.utils.ServerLoc
    )
            : NationOperation<NationOutpost>

    fun buildOperationDecomposeCastle(
        monument: NationCastle,
        commander: NationMember,
        reason: String
    )
            : NationOperation<Boolean>

    fun buildOperationDecomposeOutpost(
        monument: NationOutpost,
        commander: NationMember,
        reason: String
    )
            : NationOperation<Boolean>

}