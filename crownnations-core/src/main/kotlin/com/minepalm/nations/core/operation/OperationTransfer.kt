package com.minepalm.nations.core.operation

import com.minepalm.nations.*

class OperationTransfer(
    val nation: Nation,
    private val commander: NationMember,
    val transferTo: NationMember,
    val service: NationService
) : AbstractNationOperation<Boolean>() {

    override fun checkOrThrow() {
        if (!commander.cache.isAdmin()) {
            val rankFuture = nation.direct.getRank(transferTo.uniqueId)
            val commanderNation = commander.direct.getNation()

            if (commanderNation.join() == null || commanderNation.join()?.id != nation.id) {
                fail(ResultCode.NATION_MISMATCH, "당신은 해당 국가에 소속되어 있지 않습니다.")
            }

            if (!nation.cache.getRank(commander.uniqueId).hasPermissibleOf(NationRank.OWNER)) {
                fail(ResultCode.NO_PERMISSION, "당신은 국가장이 아닙니다.")
            }

            if (!rankFuture.join().hasPermissibleOf(NationRank.RESIDENT)) {
                fail(ResultCode.NATION_PLAYER_NOT_EXISTS, "해당 플레이어는 국가원이 아닙니다.")
            }

        }
    }

    override fun process0() {
        setResult(false)

        val event = com.minepalm.nations.event.NationTransferEvent(nation.id, commander.uniqueId, transferTo.uniqueId)
        service.localEventBus.invoke(event)

        if (event.cancelled) {
            fail(ResultCode.EVENT_CANCELLED, "")
        }

        nation.unsafe.setOwner(transferTo.uniqueId).join()
        val ownerFuture = nation.direct.getOwner()

        if (!commander.cache.isAdmin()) {
            if (nation.direct.getRank(commander.uniqueId).join() == NationRank.OWNER) {
                fail(ResultCode.ASSERT_FAILED, "위임 실행 중 문제가 발생했습니다. 관리자에게 문의해주세요.")
            }
        }

        if (ownerFuture.join().uniqueId != transferTo.uniqueId) {
            fail(ResultCode.ASSERT_FAILED, "위임 실행 중 문제가 발생했습니다. 관리자에게 문의해주세요.")
        }

        service.network.send(event)
        success(ResultCode.SUCCESSFUL, true)

    }

}