package com.minepalm.nations.core.operation

import com.minepalm.nations.Nation
import com.minepalm.nations.NationMember
import com.minepalm.nations.NationService
import com.minepalm.nations.ResultCode

class OperationNationFoundation(
    private val service: NationService,
    private val commander: NationMember,
    displayName: String,
    loc: com.minepalm.nations.utils.ServerLoc
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
            it.result ?: throw com.minepalm.nations.exception.OperationFailedException(
                it.code,
                "unchecked exception failed",
                it.exception
            )
        }

        operationClaim["nationId"] = nation.id
        processClaimOperationInContext()

        val ownerFuture = nation.direct.getOwner()

        if(ownerFuture.join().uniqueId != commander.uniqueId){
            fail(ResultCode.OWNER_MISMATCH, "생성한 국가의 소유주가 일치하지 않습니다. 관리자에게 문의해주세요.")
        }

        val event = com.minepalm.nations.event.NationCreateEvent(nation.id, commander.uniqueId)
        service.localEventBus.invoke(event)

        service.network.send(event)
        success(ResultCode.SUCCESSFUL, nation)

    }

    private fun processClaimOperationInContext(){
        val operation = operationClaim as AbstractNationOperation<com.minepalm.nations.territory.NationCastle>
        try {
            operation.checkOrThrow()
            operation.process0()
        }catch (_: com.minepalm.nations.exception.OperationInterruptedException){
            //OperationClaimAsNationCreation 에서 success로 인한 interrupt 무시.
        }
    }
}