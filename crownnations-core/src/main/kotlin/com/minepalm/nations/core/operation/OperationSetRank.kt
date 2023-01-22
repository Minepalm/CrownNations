package com.minepalm.nations.core.operation

import com.minepalm.nations.*

class OperationSetRank(
    val nation: Nation,
    private val commander: NationMember,
    val user: NationMember,
    val rank: NationRank,
    val service: NationService
) : AbstractNationOperation<Boolean>() {

    override fun checkOrThrow() {
        if (!commander.cache.isAdmin()) {
            val commanderRank = nation.cache.getRank(commander.uniqueId)
            val rankFuture = nation.direct.getRank(user.uniqueId)
            val commanderNation = commander.direct.getNation().join()

            if (commanderNation == null || commanderNation.id != nation.id) {
                fail(ResultCode.NATION_MISMATCH, "당신은 해당 국가에 소속되어 있지 않습니다.")
            }

            if (!nation.cache.getRank(commander.uniqueId).hasPermissibleOf(NationRank.OFFICER)) {
                fail(ResultCode.NO_PERMISSION, "당신은 국가 관리자가 아닙니다.")
            }

            if (!rankFuture.join().hasPermissibleOf(NationRank.RESIDENT)) {
                fail(ResultCode.NATION_PLAYER_NOT_EXISTS, "해당 플레이어는 국가원이 아닙니다.")
            }

            if (commanderRank.hasPermissibleOf(rankFuture.join())) {
                fail(ResultCode.NO_PERMISSION_THAN_USER, "해당 플레이어는 당신보다 국가 등급이 높습니다.")
            }

            if (rank.hasPermissibleOf(NationRank.OWNER)
                || !commanderRank.hasPermissibleOf(rank)
                || rank == NationRank.NONE
            ) {
                fail(ResultCode.INVALID_RANK, "해당 등급으로는 설정할수 없습니다.")
            }
        }
    }

    //
    // - 유저 권한이 관리자 이하일 경우, 설정하는 국가가 소속된 국가와 같아야 함
    // - 주려는 랭크보다 높아야함.
    // - 주려는 랭크가 OFFICER 보다 높아야함.
    // - OWNER 이상으론 설정 못함.
    // - 해당 유저가 국가원이여야 함.
    // - 국가 멤버 설정할때, 최대 임명 가능한 수 보다 적어야함.
    //
    override fun process0() {
        setResult(false)

        val event = com.minepalm.nations.event.NationSetRankEvent(nation.id, commander.uniqueId, user.uniqueId, rank)
        service.localEventBus.invoke(event)

        if (event.cancelled) {
            fail(ResultCode.EVENT_CANCELLED, "")
        }

        nation.unsafe.setMemberRank(user.uniqueId, event.rank).join()

        if (nation.direct.getRank(user.uniqueId).join() != event.rank) {
            fail(ResultCode.ASSERT_FAILED, "")
        }

        service.network.send(event)
        success(ResultCode.SUCCESSFUL, true)
    }

}