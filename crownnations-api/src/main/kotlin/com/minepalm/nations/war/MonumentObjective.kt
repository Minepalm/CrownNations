package com.minepalm.nations.war

import com.minepalm.nations.NationMember
import com.minepalm.nations.NationOperation
import java.util.concurrent.CompletableFuture

interface MonumentObjective {

    val monument: com.minepalm.nations.territory.NationMonument
    var currentHealth: Int
    val maxHealth: Int

    fun operateDamage(commander: NationMember, amount: Int): NationOperation<Boolean>

    fun isDestroyed(): Boolean

    fun destroy(): CompletableFuture<Boolean>

    fun reset()

    fun heal(amount: Int)

    fun damage(amount: Int)
}