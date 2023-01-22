package com.minepalm.nations

import java.util.*

interface NationHistory {

    fun getNation(): Nation

    fun logAddMember(commander: UUID, uniqueId: UUID)

    fun logKickMember(commander: UUID, uniqueId: UUID)

    fun logChangeMemberRank(commander: UUID, uniqueId: UUID, rankBefore: NationRank, rankAfter: NationRank)

    fun logCreate(commander: UUID)

    fun logDisband(commander: UUID)

    fun logDeposit(commander: UUID, reason: String, amount: Double)

    fun logWithdraw(commander: UUID, reason: String, amount: Double)

    fun logClaim(commander: UUID, schema: com.minepalm.nations.territory.MonumentSchema)

    fun logLogDecompose(commander: UUID, schema: com.minepalm.nations.territory.MonumentSchema)

}