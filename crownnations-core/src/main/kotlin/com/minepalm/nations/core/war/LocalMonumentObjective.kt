package com.minepalm.nations.core.war

import com.minepalm.nations.NationMember
import com.minepalm.nations.NationOperation
import com.minepalm.nations.core.operation.OperationWarMonumentDamage
import java.util.concurrent.CompletableFuture

class LocalMonumentObjective(
    private val monumentId: Int,
    private val config: com.minepalm.nations.config.WarConfiguration,
    private val service: com.minepalm.nations.territory.NationTerritoryService
) : com.minepalm.nations.war.MonumentObjective {

    override val monument: com.minepalm.nations.territory.NationMonument = service.universe.host[monumentId]!!
    override var currentHealth: Int
        get() = currentHealth0
        set(value) {
            if (destroyed) {
                throw IllegalStateException("monument already destroyed. cannot heal this objective")
            }
            currentHealth0 = when {
                value > maxHealth -> maxHealth
                value < 0 -> 0
                else -> value
            }
        }
    override val maxHealth: Int = config.castleMaxHealth

    private var currentHealth0: Int = maxHealth
    private var destroyed = false


    override fun operateDamage(commander: NationMember, amount: Int): NationOperation<Boolean> {
        return OperationWarMonumentDamage(commander, this, amount, service.root)
    }

    override fun damage(amount: Int) {
        currentHealth0 = -amount
    }

    override fun isDestroyed(): Boolean {
        return destroyed
    }

    override fun destroy(): CompletableFuture<Boolean> {
        destroyed = true
        return monument.collapse()
    }

    override fun reset() {
        currentHealth = maxHealth
        destroyed = false
    }

    override fun heal(amount: Int) {
        currentHealth = +amount
    }
}