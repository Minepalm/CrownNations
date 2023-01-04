package kr.rendog.nations.core.operation

import kr.rendog.nations.Nation
import kr.rendog.nations.NationMember
import kr.rendog.nations.NationService
import kr.rendog.nations.ResultCode
import kr.rendog.nations.event.NationCreateEvent
import kr.rendog.nations.exception.OperationFailedException
import kr.rendog.nations.exception.OperationInterruptedException
import kr.rendog.nations.territory.NationCastle
import kr.rendog.nations.utils.ServerLoc

class OperationNationFoundation(
    private val service: NationService,
    private val commander: NationMember,
    displayName: String,
    loc: ServerLoc
) : AbstractNationOperation<Nation>(){

    private val operationCreate = service.operationFactory.buildCreate(commander, displayName)
    private val operationClaim = service.territoryService.operationFactory
        .buildOperateClaimCastleNationCreate(commander, loc)

    override fun checkOrThrow() {
        ( operationCreate as AbstractNationOperation<*> ).checkOrThrow()
        ( operationClaim as AbstractNationOperation<*> ).checkOrThrow()
    }

    override fun process0() {
        val nation = operationCreate.process().let {
            it.result ?: throw OperationFailedException(it.code, "unchecked exception failed", it.exception)
        }

        operationClaim["nationId"] = nation.id
        processClaimOperationInContext()

        val ownerFuture = nation.direct.getOwner()

        if(ownerFuture.join().uniqueId != commander.uniqueId){
            fail(ResultCode.OWNER_MISMATCH, "생성한 국가의 소유주가 일치하지 않습니다. 관리자에게 문의해주세요.")
        }

        val event = NationCreateEvent(nation.id, commander.uniqueId)
        service.localEventBus.invoke(event)

        service.network.send(event)
        success(ResultCode.SUCCESSFUL, nation)

    }

    private fun processClaimOperationInContext(){
        val operation = operationClaim as AbstractNationOperation<NationCastle>
        try {
            operation.checkOrThrow()
            operation.process0()
        }catch (_: OperationInterruptedException){
            //OperationClaimAsNationCreation 에서 success로 인한 interrupt 무시.
        }
    }
}