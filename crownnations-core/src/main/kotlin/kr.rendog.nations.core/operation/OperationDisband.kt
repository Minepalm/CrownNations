package kr.rendog.nations.core.operation

import kr.rendog.nations.*
import kr.rendog.nations.core.network.MemberUpdate
import kr.rendog.nations.event.NationDisbandEvent

class OperationDisband(
    val nation : Nation,
    private val commander: NationMember,
    val service : NationService

) : AbstractNationOperation<Boolean>()  {

    override fun checkOrThrow() {
        if(!commander.cache.isAdmin()){
            val rankFuture = nation.direct.getRank(commander.uniqueId)
            val ownerFuture = nation.direct.getOwner()

            if(rankFuture.join() != NationRank.OWNER){
                fail(ResultCode.NO_PERMISSION, "권한이 없습니다.")
            }

            if(ownerFuture.join().uniqueId != commander.uniqueId){
                fail(ResultCode.OWNER_MISMATCH, "당신은 국가장이 아닙니다.")
            }
        }
    }

    override fun process0(){
        setResult(false)

        val event = NationDisbandEvent(nation.id, nation.name, commander.uniqueId)
        service.localEventBus.invoke(event)

        if(event.cancelled){
            fail(ResultCode.EVENT_CANCELLED, "")
        }

        val members = service.nationRegistry.local[event.nationId]?.run { direct.getMembers().join() }
        nation.unsafe.delete().join()
        members?.forEach { service.network.broadcast(MemberUpdate(it.uniqueId)) }

        service.network.send(event)
        success(ResultCode.SUCCESSFUL, true)
    }

}