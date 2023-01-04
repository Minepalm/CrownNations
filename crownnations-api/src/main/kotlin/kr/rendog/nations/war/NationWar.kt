package kr.rendog.nations.war

import kr.rendog.nations.Nation
import kr.rendog.nations.NationMember
import kr.rendog.nations.NationOperation
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

    fun operateDeclareWar(commander: NationMember, type: WarType, opponent: Nation): NationOperation<WarSession>

    fun operateSurrender(commander: NationMember): NationOperation<WarResult>

}