package kr.rendog.nations.core.bank

import kr.rendog.nations.*
import kr.rendog.nations.core.operation.AbstractNationOperation
import kr.rendog.nations.event.NationWithdrawEvent

class OperationWithdraw(
    private val service: NationService,
    private val nation: Nation,
    private val commander: NationMember,
    private val reason: String,
    private val amount: Double
) : AbstractNationOperation<Double>() {

    override fun checkOrThrow() {
        if(!commander.cache.isAdmin()){
            val commanderNation = commander.direct.getNation().join()

            if(commanderNation == null || commanderNation.id != nation.id){
                fail(ResultCode.NATION_MISMATCH, "당신은 해당 국가에 소속되어 있지 않습니다.")
            }

            if(!nation.cache.getRank(commander.uniqueId).hasPermissibleOf(NationRank.OFFICER)){
                fail(ResultCode.NO_PERMISSION, "당신은 국가의 관리자가 아닙니다.")
            }

            val balanceFuture = nation.bank.balance()

            if(amount <= 0){
                fail(ResultCode.INVALID_AMOUNT, "0 이상의 금액을 입력해주세요.")
            }

            if(amount > balanceFuture.join()){
                fail(ResultCode.EXCEEDED_AMOUNT, "출금 가능한 금액을 초과했습니다.")
            }
        }
    }

    override fun process0() {
        val event = NationWithdrawEvent(nation.id, reason, amount)

        service.localEventBus.invoke(event)
        if(event.cancelled){
            fail(ResultCode.EVENT_CANCELLED, "")
        }

        val after = nation.bank.withdraw(reason, amount).join()
        success(ResultCode.SUCCESSFUL, after)
    }
}