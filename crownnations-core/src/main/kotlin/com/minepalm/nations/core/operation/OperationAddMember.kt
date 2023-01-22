package com.minepalm.nations.core.operation

import com.minepalm.nations.*

class OperationAddMember(
    val nation: Nation,
    private val commander: NationMember,
    val user: NationMember,
    val service: NationService
) : AbstractNationOperation<Boolean>() {

    //
    // 1. 커맨더가 관리자 이상이 아니면, 국가가 같아야함
    // 2. 국가 최대 인원 수 보다 적어야 함
    // 3. 유저가 국가가 없어야 함
    //
    override fun process0() {
        setResult(false)

        val event = com.minepalm.nations.event.NationAddMemberEvent(nation.id, commander.uniqueId, user.uniqueId)
        service.localEventBus.invoke(event)

        if (event.cancelled) {
            fail(ResultCode.EVENT_CANCELLED, "")
        }

        nation.unsafe.addMember(user.uniqueId).join()

        if (user.direct.getNation().join()?.id != nation.id) {
            fail(ResultCode.ASSERT_FAILED, "명령어 실행 중 오류가 발생했습니다. 관리자에게 문의해주세요.")
        }

        service.network.send(event)
        success(ResultCode.SUCCESSFUL, true)

    }

    override fun checkOrThrow() {
        if (!commander.cache.isAdmin()) {
            val hasNationFuture = user.direct.hasNation()
            val sizeFuture = nation.direct.getMembers()
            val commanderNation = commander.direct.getNation().join()

            if (commanderNation == null || commanderNation.id != nation.id) {
                fail(ResultCode.NATION_MISMATCH, "해당 국가에 소속되어 있지 않습니다.")
            }

            if (!nation.cache.getRank(commander.uniqueId).hasPermissibleOf(NationRank.OFFICER)) {
                fail(ResultCode.NO_PERMISSION, "그 국가의 관리자가 아닙니다.")
            }

            if (sizeFuture.join().size >= getMaximumPlayers()) {
                fail(ResultCode.REACH_MAXIMUM_MEMBER, "이미 해당 국가의 최대 인원수에 도달했습니다.")
            }

            if (!hasNationFuture.join()) {
                fail(ResultCode.ALREADY_HAS_NATION, "해당 유저는 이미 국가를 가지고 있습니다.")
            }
        }
    }

    fun setMaximumPlayers(size: Int) {
        data["maximumPlayer"] = size
    }

    fun getMaximumPlayers(): Int {
        return data["maximumPlayer"] as? Int ?: Int.MAX_VALUE
    }
}