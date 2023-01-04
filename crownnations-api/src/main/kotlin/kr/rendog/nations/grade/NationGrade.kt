package kr.rendog.nations.grade

import kr.rendog.nations.Nation
import kr.rendog.nations.NationMember
import kr.rendog.nations.NationOperation
import java.util.concurrent.CompletableFuture

interface NationGrade {

    val parent: Nation
    val modifier: Modifier

    var currentLevel : Int
    val displayName : String

    fun operateLevelUp(commander: NationMember): NationOperation<PromoteResult>

    fun sync(): CompletableFuture<Unit>

    interface Modifier{

        fun getLevel() : CompletableFuture<Int>

        fun setLevel(level: Int) : CompletableFuture<Unit>

    }
}