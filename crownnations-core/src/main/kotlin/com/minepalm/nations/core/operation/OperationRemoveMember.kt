package com.minepalm.nations.core.operation

import com.minepalm.nations.*
import com.minepalm.nations.event.NationRemoveMemberEvent

class OperationRemoveMember(
    val nation : Nation,
    private val commander: NationMember,
    val user : NationMember,
    val service : NationService
) : AbstractNationOperation<Boolean>() {

    override fun checkOrThrow() {
        if(!commander.cache.isAdmin()){
            val rankFuture = nation.direct.getRank(user.uniqueId)
            val commanderRank = nation.cache.getRank(commander.uniqueId)
            val commanderNation = commander.direct.getNation().join()

            if(commanderNation == null || commanderNation.id != nation.id){
                fail(ResultCode.NATION_MISMATCH, "당신은 해당 국가에 소속되어 있지 않습니다.")
            }

            if(!commanderRank.hasPermissibleOf(NationRank.OFFICER)){
                fail(ResultCode.NO_PERMISSION, "당신은 국가 관리자가 아닙니다.")
            }

            if(!rankFuture.join().hasPermissibleOf(NationRank.RESIDENT)){
                fail(ResultCode.NATION_PLAYER_NOT_EXISTS, "해당 플레이어는 국가원이 아닙니다.")
            }

            if(!commanderRank.hasPermissibleOf(rankFuture.join())){
                fail(ResultCode.NO_PERMISSION_THAN_USER, "해당 플레이어는 당신보다 국가 등급이 높습니다.")
            }
        }
    }

    //
    // 1. 커맨더가 관리자 이상이 아니면, 국가가 같아야함
    // 2. 해당 국가에 그 유저가 있어야 함.
    // 3. 해당 국가에서 그 유저보다 권한이 높아야 함
    // 4. 사용하는 유저가 OFFICER 보다 등급이 높아야 함
    //
    override fun process0() {
        setResult(false)

        val event = NationRemoveMemberEvent(nation.id, commander.uniqueId, user.uniqueId, "KICK")
        service.localEventBus.invoke(event)

        if(event.cancelled){
            fail(ResultCode.EVENT_CANCELLED, "")
        }

        nation.unsafe.removeMember(user.uniqueId).join()

        if( user.direct.getNation().join()?.id == nation.id ){
            fail(ResultCode.ASSERT_FAILED, "명령어 실행 중 오류가 발생했습니다. 관리자에게 문의해주세요.")
        }

        service.network.send(event)
        success(true)
    }

}