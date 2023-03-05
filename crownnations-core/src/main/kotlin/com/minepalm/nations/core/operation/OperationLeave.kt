package com.minepalm.nations.core.operation

import com.minepalm.nations.NationMember
import com.minepalm.nations.NationService
import com.minepalm.nations.ResultCode
import com.minepalm.nations.event.NationRemoveMemberEvent

class OperationLeave(
    private val commander: NationMember,
    private val service: NationService
) : AbstractNationOperation<Boolean>() {

    override fun checkOrThrow() {
        val nation = commander.direct.getNation()
        val rank = nation.thenCompose { it?.direct?.getOwner() }

        if (nation.join() == null) {
            fail(ResultCode.NO_NATION, "해당 플레이어는 국가에 소속되어 있지 않습니다.")
        }

        if (rank.join().uniqueId == commander.uniqueId) {
            fail(ResultCode.NATION_OWNER, "해당 플레이어는 국가의 소유주입니다.")
        }

    }

    override fun process0() {
        setResult(false)

        val nation = commander.direct.getNation()
        val event = NationRemoveMemberEvent(nation.join()!!.id, commander.uniqueId, commander.uniqueId, "LEAVE")
        service.localEventBus.invoke(event)

        if (event.cancelled) {
            fail(ResultCode.EVENT_CANCELLED, "")
        }

        nation.join()!!.unsafe.removeMember(commander.uniqueId).join()
        service.network.send(event)
        success(true)
    }

}