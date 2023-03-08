package com.minepalm.nations.bukkit.bank

import com.minepalm.economy.PalmCoconut
import com.minepalm.economy.key
import com.minepalm.nations.core.bank.EconomyAdapter
import java.util.*
import java.util.concurrent.CompletableFuture

class PalmCoconutEconomyAdapter : EconomyAdapter {

    private val service
        get() = PalmCoconut.service
    override fun hasMoney(uuid: UUID, amount: Double): CompletableFuture<Boolean> {
        return service.registry[uuid.key()].has("MONEY", amount).let { CompletableFuture.completedFuture(it) }
    }

    override fun getMoney(uuid: UUID): CompletableFuture<Double> {
        return service.registry[uuid.key()]["MONEY"].let { CompletableFuture.completedFuture(it) }
    }

    override fun takeMoney(uuid: UUID, value: Double): CompletableFuture<Double> {
        return service.registry[uuid.key()].subtract("MONEY", value, "NATIONS")
    }

    override fun giveMoney(uuid: UUID, value: Double): CompletableFuture<Double> {
        return service.registry[uuid.key()].add("MONEY", value, "NATIONS")
    }

}