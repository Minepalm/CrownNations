package com.minepalm.nations.core.bank

import com.minepalm.nations.NationMember
import com.minepalm.nations.NationOperation
import com.minepalm.nations.NationService
import java.util.concurrent.CompletableFuture

class PalmNationBank(
    override val nationId: Int,
    private val database: MySQLBankDatabase,
    private val service: NationService
) : com.minepalm.nations.bank.NationBank {
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