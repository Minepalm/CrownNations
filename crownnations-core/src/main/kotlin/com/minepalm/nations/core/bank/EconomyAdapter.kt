package com.minepalm.nations.core.bank

import java.util.UUID
import java.util.concurrent.CompletableFuture

interface EconomyAdapter {

    fun hasMoney(uuid: UUID): CompletableFuture<Boolean>
    fun getMoney(uuid: UUID): CompletableFuture<Double>

    fun takeMoney(uuid: UUID, value: Double): CompletableFuture<Double>

    fun giveMoney(uuid: UUID, value: Double): CompletableFuture<Double>


}