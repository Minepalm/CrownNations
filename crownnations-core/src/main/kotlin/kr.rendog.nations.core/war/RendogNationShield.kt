package kr.rendog.nations.core.war

import kr.rendog.nations.Nation
import kr.rendog.nations.core.mysql.MySQLWarShieldDatabase
import kr.rendog.nations.core.mysql.MySQLWarShieldForcedDatabase
import kr.rendog.nations.war.NationShield
import kr.rendog.nations.war.NationWarService
import java.time.Duration
import java.time.Instant
import java.util.*
import java.util.concurrent.CompletableFuture

class RendogNationShield(
    private val nationId: Int,
    private val database: MySQLWarShieldDatabase,
    private val forceDatabase: MySQLWarShieldForcedDatabase,
    private val service: NationWarService
) : NationShield {

    companion object{
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
            val future = if(forced){
                forceDatabase.setForced(nationId, time)
            }else
                CompletableFuture.completedFuture(Unit)
            future.thenApply { Date(after) }
        }

    }

    override fun setDuration(time: Long, forced: Boolean): CompletableFuture<Date> {
        return database.setTime(nationId, time).thenCompose { after ->
            val future = if(forced){
                forceDatabase.setForced(nationId, time)
            }else
                CompletableFuture.completedFuture(Unit)
            future.thenApply { Date(after) }
        }
    }

    override fun reset(): CompletableFuture<Unit> {
        return database.setTime(nationId, 0L).thenApply { Unit }
    }
}