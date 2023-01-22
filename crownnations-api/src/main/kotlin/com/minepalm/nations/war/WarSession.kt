package com.minepalm.nations.war

import com.minepalm.nations.Nation
import com.minepalm.nations.NationMember
import com.minepalm.nations.NationOperation
import java.util.concurrent.CompletableFuture

interface WarSession {

    val gameId: Int
    val info: WarInfo
    val data: SessionData

    val home: Nation
    val away: Nation

    val homeRecorder: ObjectiveRecorder
    val awayRecorder: ObjectiveRecorder

    val local: Cache
    val unsafe: Unsafe

    fun isActive(): CompletableFuture<Boolean>

    fun getStatus(): CompletableFuture<WarStatus>

    fun getTime(): CompletableFuture<WarTime?>

    fun getObjectives(): CompletableFuture<List<WarObjective>>

    fun operateStart(): NationOperation<WarTime>

    fun operateEnd(reason: WarResult.Type): NationOperation<WarResult>

    fun isHomeTeam(member: NationMember): Boolean

    fun isAwayTeam(member: NationMember): Boolean

    fun getRecorder(nation: Nation): ObjectiveRecorder?

    interface Cache {

        val time: WarTime?
        val status: WarStatus

    }

    interface Unsafe {
        fun startGame(): CompletableFuture<Boolean>

        fun endGame(result: WarResult): CompletableFuture<Boolean>

        fun invalidate(): CompletableFuture<Unit>

        fun setActive(bool: Boolean): CompletableFuture<Unit>


    }
}