package kr.rendog.nations.bank

import kr.rendog.nations.NationMember
import kr.rendog.nations.NationOperation
import java.util.concurrent.CompletableFuture

interface NationBank {

    val nationId: Int

    fun withdraw(reason: String, value: Double): CompletableFuture<Double>

    fun deposit(reason: String, value: Double): CompletableFuture<Double>

    fun operateWithdraw(commander: NationMember, reason: String, value: Double): NationOperation<Double>

    fun operateDeposit(commander: NationMember, reason: String, value: Double): NationOperation<Double>

    fun has(value: Double): CompletableFuture<Boolean>

    fun balance(): CompletableFuture<Double>
}