package com.minepalm.nations.core.grade

import com.minepalm.nations.Nation
import com.minepalm.nations.NationMember
import com.minepalm.nations.NationRank
import com.minepalm.nations.ResultCode
import com.minepalm.nations.core.operation.AbstractNationOperation

class OperationNationUpgrade(
    private val nation: Nation,
    private val service: com.minepalm.nations.grade.NationGradeService,
    private val commander: NationMember
) : AbstractNationOperation<com.minepalm.nations.grade.PromoteResult>() {

    private val grade = nation.grade

    override fun checkOrThrow() {
        if (!commander.cache.isAdmin()) {
            val rankFuture = nation.direct.getRank(commander.uniqueId)
            val ownerFuture = nation.direct.getOwner()

            if (rankFuture.join() != NationRank.OWNER) {
                fail(ResultCode.NO_PERMISSION, "권한이 없습니다.")
            }

            if (ownerFuture.join().uniqueId != commander.uniqueId) {
                fail(ResultCode.OWNER_MISMATCH, "당신은 국가장이 아닙니다.")
            }
        }

        if (grade.currentLevel >= service.promotionRegistry.getMaxLevel()) {
            fail(ResultCode.REACHED_MAX_LEVEL, "이미 국가의 최대 레벨에 도달했습니다.")
        }
    }

    override fun process0() {
        val promotion = service.promotionRegistry[grade.currentLevel]
        val result = service.promotionRegistry[grade.currentLevel].test(nation)
        val event = com.minepalm.nations.event.NationPromoteEvent(
            nation.id,
            commander.uniqueId,
            promotion.targetLevel,
            promotion.nextLevel,
            result
        )

        service.root.localEventBus.invoke(event)
        if (event.cancelled) {
            fail(ResultCode.EVENT_CANCELLED, "")
        }

        success(event.result)
    }
}