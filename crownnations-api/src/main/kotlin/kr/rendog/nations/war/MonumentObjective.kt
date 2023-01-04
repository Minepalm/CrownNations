package kr.rendog.nations.war

import kr.rendog.nations.NationMember
import kr.rendog.nations.NationOperation
import kr.rendog.nations.territory.NationMonument
import java.util.concurrent.CompletableFuture

interface MonumentObjective {

    val monument: NationMonument
    var currentHealth: Int
    val maxHealth: Int

    fun operateDamage(commander: NationMember, amount: Int): NationOperation<Boolean>

    fun isDestroyed(): Boolean

    fun destroy(): CompletableFuture<Boolean>

    fun reset()

    fun heal(amount: Int)

    fun damage(amount: Int)
}