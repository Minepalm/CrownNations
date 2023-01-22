package com.minepalm.nations.grade

import com.minepalm.nations.Nation
import com.minepalm.nations.NationMember
import com.minepalm.nations.NationOperation
import java.util.concurrent.CompletableFuture

interface NationGrade {

    val parent: Nation
    val modifier: Modifier

    var currentLevel: Int
    val displayName: String

    fun operateLevelUp(commander: NationMember): NationOperation<PromoteResult>

    fun sync(): CompletableFuture<Unit>

    interface Modifier {

        fun getLevel(): CompletableFuture<Int>

        fun setLevel(level: Int): CompletableFuture<Unit>

    }
}