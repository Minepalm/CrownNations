package kr.rendog.nations.core.bank

import kr.rendog.nations.NationEventBus
import kr.rendog.nations.NationMember
import kr.rendog.nations.NationOperation
import kr.rendog.nations.NationService
import kr.rendog.nations.bank.NationBank
import kr.rendog.nations.event.NationDepositEvent
import kr.rendog.nations.event.NationWithdrawEvent
import java.util.concurrent.CompletableFuture

class RendogNationBank(
    override val nationId : Int,
    private val database : MySQLBankDatabase,
    private val service: NationService
    ) : NationBank {
    override fun withdraw(reason: String, value: Double): CompletableFuture<Double> {
        return database.takeMoney(nationId, value)
    }

    override fun deposit(reason: String, value: Double): CompletableFuture<Double> {
        return database.takeMoney(nationId, value)
    }

    override fun operateWithdraw(commander: NationMember, reason: String, value: Double): NationOperation<Double> {
        return OperationWithdraw(service, service.nationRegistry[nationId]!!, commander, reason, value)
    }

    override fun operateDeposit(commander: NationMember, reason: String, value: Double): NationOperation<Double> {
        return OperationDeposit(service, service.nationRegistry[nationId]!!, commander, reason, value)
    }

    override fun has(value: Double): CompletableFuture<Boolean> {
        return database.has(nationId, value)
    }

    override fun balance(): CompletableFuture<Double> {
        return database.getMoney(nationId)
    }
}