package kr.rendog.nations.core.war

import kr.rendog.nations.NationMember
import kr.rendog.nations.NationOperation
import kr.rendog.nations.config.WarConfiguration
import kr.rendog.nations.core.operation.OperationWarMonumentDamage
import kr.rendog.nations.territory.NationMonument
import kr.rendog.nations.territory.NationTerritoryService
import kr.rendog.nations.war.MonumentObjective
import java.util.concurrent.CompletableFuture

class LocalMonumentObjective(
    private val monumentId: Int,
    private val config: WarConfiguration,
    private val service: NationTerritoryService
) : MonumentObjective {

    override val monument: NationMonument = service.universe.host[monumentId]!!
    override var currentHealth: Int
        get() = currentHealth0
        set(value) {
            if(destroyed){
                throw IllegalStateException("monument already destroyed. cannot heal this objective")
            }
            currentHealth0 = when{
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
        currentHealth0 =- amount
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
        currentHealth =+ amount
    }
}