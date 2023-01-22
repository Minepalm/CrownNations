package com.minepalm.nations.core.war

import com.minepalm.nations.Nation
import com.minepalm.nations.core.mysql.MySQLWarShieldDatabase
import com.minepalm.nations.core.mysql.MySQLWarShieldForcedDatabase
import java.time.Duration
import java.time.Instant
import java.util.*
import java.util.concurrent.CompletableFuture

class PalmNationShield(
    private val nationId: Int,
    private val database: MySQLWarShieldDatabase,
    private val forceDatabase: MySQLWarShieldForcedDatabase,
    private val service: com.minepalm.nations.war.NationWarService
) : com.minepalm.nations.war.NationShield {

    companion object {
        val now: Long
            get() = System.currentTimeMillis()
    }

    override val parent: Nation
        get() = service.root.nationRegistry[nationId]!!

    override fun hasShield(): CompletableFuture<Boolean> {
        return database.getTime(nationId).thenApply { time -> time >= now }
    }

    override fun isForced(): CompletableFuture<Boolean> {
        return forceDatabase.isForced(nationId)
    }

    override fun getDuration(): CompletableFuture<Duration> {
        return database.getTime(nationId).thenApply {
            val now = Instant.now()
            val expired = Instant.ofEpochMilli(it)
            Duration.between(now, expired)
        }
    }

    override fun getDate(): CompletableFuture<Date> {
        return database.getTime(nationId).thenApply { Date(it) }
    }

    override fun isProtected(): CompletableFuture<Boolean> {
        return getDate().thenApply { it.after(Date()) }
    }

    override fun addDuration(time: Long, forced: Boolean): CompletableFuture<Date> {
        return database.addTime(nationId, time).thenCompose { after ->
            val future = if (forced) {
                forceDatabase.setForced(nationId, time)
            } else
                CompletableFuture.completedFuture(Unit)
            future.thenApply { Date(after) }
        }

    }

    override fun setDuration(time: Long, forced: Boolean): CompletableFuture<Date> {
        return database.setTime(nationId, time).thenCompose { after ->
            val future = if (forced) {
                forceDatabase.setForced(nationId, time)
            } else
                CompletableFuture.completedFuture(Unit)
            future.thenApply { Date(after) }
        }
    }

    override fun reset(): CompletableFuture<Unit> {
        return database.setTime(nationId, 0L).thenApply { }
    }
}