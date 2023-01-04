package kr.rendog.nations.war

import kr.rendog.nations.Nation
import java.time.Duration
import java.util.*
import java.util.concurrent.CompletableFuture

interface NationShield {

    val parent: Nation

    fun hasShield(): CompletableFuture<Boolean>

    fun isForced(): CompletableFuture<Boolean>

    fun getDuration(): CompletableFuture<Duration>

    fun getDate(): CompletableFuture<Date>

    fun isProtected(): CompletableFuture<Boolean>

    fun addDuration(time: Long, forced: Boolean): CompletableFuture<Date>

    fun setDuration(time: Long, forced: Boolean): CompletableFuture<Date>

    fun reset(): CompletableFuture<Unit>

}