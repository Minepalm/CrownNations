package com.minepalm.nations.core.bank

import com.minepalm.nations.*
import com.minepalm.nations.core.operation.AbstractNationOperation

class OperationDeposit(
    private val service: NationService,
    private val nation: Nation,
    private val commander: NationMember,
    private val reason: String,
    private val amount: Double
) : AbstractNationOperation<Double>() {

    override fun checkOrThrow() {
        if (!commander.cache.isAdmin()) {
            val commanderNation = commander.direct.getNation().join()

            if (commanderNation == null || commanderNation.id != nation.id) {
                fail(ResultCode.NATION_MISMATCH, "당신은 해당 국가에 소속되어 있지 않습니다.")
            }

            if (!nation.cache.getRank(commander.uniqueId).hasPermissibleOf(NationRank.RESIDENT)) {
                fail(ResultCode.NO_PERMISSION, "당신은 국가원이 아닙니다.")
            }

            if (amount <= 0) {
                fail(ResultCode.INVALID_AMOUNT, "0 이상의 금액을 입력해주세요.")
            }
        }
    }

    override fun process0() {
        val event = com.minepalm.nations.event.NationDepositEvent(nation.id, reason, amount)

        service.localEventBus.invoke(event) {
            if (event.cancelled) {
                fail(ResultCode.EVENT_CANCELLED, "")
            }

            val after = nation.bank.deposit(reason, amount).join()
            success(ResultCode.SUCCESSFUL, after)
        }
    }
}