package kr.rendog.nations.core.operation

import kr.rendog.nations.NationMember
import kr.rendog.nations.NationService
import kr.rendog.nations.ResultCode
import kr.rendog.nations.event.NationRemoveMemberEvent

class OperationLeave(
    private val commander: NationMember,
    private val service: NationService
): AbstractNationOperation<Boolean>() {

    override fun checkOrThrow() {
        val nation = commander.direct.getNation()
        val rank = nation.thenCompose { it?.direct?.getOwner() }

        if(nation.join() == null){
            fail(ResultCode.NO_NATION, "해당 플레이어는 국가에 소속되어 있지 않습니다.")
        }

        if(rank.join().uniqueId == commander.uniqueId){
            fail(ResultCode.NATION_OWNER, "해당 플레이어는 국가의 소유주입니다.")
        }

    }

    override fun process0() {
        setResult(false)

        val nation = commander.direct.getNation()
        val event = NationRemoveMemberEvent(nation.join()!!.id, commander.uniqueId, commander.uniqueId, "LEAVE")
        service.localEventBus.invoke(event)

        if(event.cancelled){
            fail(ResultCode.EVENT_CANCELLED, "")
        }

        service.network.send(event)
        success(ResultCode.SUCCESSFUL, true)
    }

}