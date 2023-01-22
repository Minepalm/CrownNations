package com.minepalm.nations.core.operation

import com.minepalm.nations.*

class OperationMetadataChange(
    val nation : Nation,
    private val commander: NationMember,
    val key : String,
    val value : String?,
    val service : NationService
) : AbstractNationOperation<Boolean>()  {

    override fun checkOrThrow() {
        if(!commander.cache.isAdmin()){
            if(nation.direct.getRank(commander.uniqueId).join().hasPermissibleOf(getRequiredRank())){
                fail(ResultCode.NO_PERMISSION, "당신은 권한이 없습니다.")
            }
        }
    }

    //
    // 1. 요구하는 권한보다 높아야 함
    // 2. null 이면 삭제
    // 3. 관리자면 걍 썡까고 수정 가능
    //
    override fun process0() {
        setResult(false)

        val before = nation.metadata.get(key)
        val event =
            com.minepalm.nations.event.NationMetadataChangeEvent(nation.id, key, before, value, commander.uniqueId)
        service.localEventBus.invoke(event)

        if(event.cancelled){
            fail(ResultCode.EVENT_CANCELLED, "")
        }

        nation.metadata.set(event.key, event.valueTo)

        if(nation.metadata.get(event.key) != event.valueTo){
            fail(ResultCode.ASSERT_FAILED, "")
        }

        service.network.send(event)
        success(ResultCode.SUCCESSFUL, true)

    }

    fun setRequiredRank(rank : NationRank){
        data["required_rank"] = rank
    }

    fun getRequiredRank() : NationRank {
        return data["required_rank"] as? NationRank ?: NationRank.OFFICER
    }
}