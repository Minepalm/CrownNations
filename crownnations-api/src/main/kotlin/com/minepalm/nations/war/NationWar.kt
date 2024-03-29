package com.minepalm.nations.war

import com.minepalm.nations.Nation
import com.minepalm.nations.NationMember
import com.minepalm.nations.NationOperation
import java.util.concurrent.CompletableFuture

interface NationWar {

    val parent: Nation

    val shield: NationShield
    val rating: NationRating

    val objectives: List<MonumentObjective>

    fun getCurrentMatch(): CompletableFuture<WarSession?>

    fun isInWar(): CompletableFuture<Boolean>

    fun getOpponent(): CompletableFuture<Nation?>

    fun getRecentOpponents(count: Int): CompletableFuture<List<Nation>>

    fun operateDeclareWar(
        commander: NationMember,
        type: WarType,
        opponent: Nation
    ): NationOperation<WarSession>

    fun operateSurrender(commander: NationMember): NationOperation<WarResult>

}