package com.minepalm.nations.core.war

import com.minepalm.nations.Nation
import com.minepalm.nations.NationMember
import com.minepalm.nations.NationOperation
import com.minepalm.nations.core.mysql.MySQLWarSessionActiveDatabase
import com.minepalm.nations.core.mysql.MySQLWarSessionDatabase
import com.minepalm.nations.core.mysql.MySQLWarSessionTimeDatabase
import com.minepalm.nations.war.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService

class PalmWarSession(
    override val gameId: Int,
    override val info: WarInfo,
    private val service: PalmNationWarService,
) : WarSession {

    override val data: SessionData = SessionData(gameId, info)
    override val home: Nation
        get() = service.root.nationRegistry[info.homeNation]!!
    override val away: Nation
        get() = service.root.nationRegistry[info.awayNation]!!
    override val homeRecorder: ObjectiveRecorder
        get() = ObjectiveRecorderImpl(gameId, info.homeNation, service, service.objectiveDatabase)
    override val awayRecorder: ObjectiveRecorder
        get() = ObjectiveRecorderImpl(gameId, info.awayNation, service, service.objectiveDatabase)

    override val local: WarSession.Cache
        get() = cacheImpl
    private val cacheImpl = CacheImpl()
    override val unsafe: WarSession.Unsafe = UnsafeImpl(
        this, service,
        cacheImpl,
        service.timeDatabase,
        service.sessionDatabase,
        service.statusDatabase,
        service.executors
    )

    override fun isActive(): CompletableFuture<Boolean> {
        return service.statusDatabase.getActive(gameId)
    }

    override fun getStatus(): CompletableFuture<WarStatus> {
        return service.statusDatabase.getActive(gameId).thenCombine(getTime()) { active, time ->
            return@thenCombine when {
                time == null -> WarStatus.IDLE
                active -> time.status()
                else -> WarStatus.INVALID
            }
        }
    }

    override fun getTime(): CompletableFuture<WarTime?> {
        return service.timeDatabase.select(gameId)
    }

    override fun getObjectives(): CompletableFuture<List<WarObjective>> {
        return service.objectiveDatabase.select(gameId)
    }

    override fun operateStart(): NationOperation<WarTime> {
        return service.operationFactory.buildGameStart(this)
    }

    override fun operateEnd(reason: WarResult.Type): NationOperation<WarResult> {
        return service.operationFactory.buildGameEnd(this, reason)
    }

    override fun isHomeTeam(member: NationMember): Boolean {
        return isHomeTeam0(member.cache.getNation())
    }

    override fun isAwayTeam(member: NationMember): Boolean {
        return isAwayTeam(member.cache.getNation())
    }

    override fun getRecorder(nation: Nation): ObjectiveRecorder? {
        return when {
            isHomeTeam0(nation) -> homeRecorder
            isAwayTeam(nation) -> awayRecorder
            else -> null
        }
    }

    private fun isHomeTeam0(nation: Nation?): Boolean {
        return nation?.id == home.id
    }

    private fun isAwayTeam(nation: Nation?): Boolean {
        return nation?.id == away.id
    }

    private class CacheImpl : WarSession.Cache {

        override val status: WarStatus
            get() {
                return if (!invalidate) {
                    if (timeActual != null) {
                        timeActual!!.status()
                    } else {
                        WarStatus.IDLE
                    }
                } else {
                    WarStatus.INVALID
                }
            }
        override val time: WarTime?
            get() = timeActual

        private var invalidate: Boolean = false
        private var timeActual: WarTime? = null

        fun setTime(time: WarTime) {
            timeActual = time
        }

        fun markInvalidate() {
            invalidate = true
        }
    }

    private class UnsafeImpl(
        private val session: WarSession,
        private val service: NationWarService,
        private val cacheImpl: CacheImpl,
        private val timeDatabase: MySQLWarSessionTimeDatabase,
        private val sessionDatabase: MySQLWarSessionDatabase,
        private val statusDatabase: MySQLWarSessionActiveDatabase,
        private val executors: ExecutorService
    ) : WarSession.Unsafe {

        //분산락 필요한 부분
        override fun startGame(): CompletableFuture<Boolean> {
            val isActiveFuture = statusDatabase.getActive(session.gameId)
            val time = service.config.generateWarTime(System.currentTimeMillis())
            return isActiveFuture.thenCompose { isActive ->
                return@thenCompose if (isActive)
                    CompletableFuture.completedFuture(false)
                else {
                    val updateStatus = setActive(true)
                    timeDatabase.insert(session.gameId, time).thenCombineAsync(updateStatus, { _, _ ->
                        cacheImpl.setTime(time)
                        val server = service.root.network.host.name
                        val event = com.minepalm.nations.event.WarPostDeclarationEvent(server, session.data, time)
                        service.root.localEventBus.invoke(event)
                        true
                    }, executors)
                }
            }
        }

        //분산락 필요한 부분
        //
        // 1. 전쟁이 활성화 되어 잇는 경우
        // -> 전쟁을 비활성화
        // 2. 전쟁 종료 이벤트 활성화
        // 3. 전쟁 결과 ->
        override fun endGame(result: WarResult): CompletableFuture<Boolean> {
            val isActiveFuture = statusDatabase.getActive(session.gameId)
            return isActiveFuture.thenApplyAsync({ isActive ->
                if (isActive) {
                    invalidate()
                    cacheImpl.markInvalidate()
                    service.root.localEventBus.invoke(
                        com.minepalm.nations.event.WarEndEvent(
                            service.root.network.host.name,
                            session.gameId,
                            result
                        )
                    )
                }
                isActive
            }, executors)
        }

        override fun invalidate(): CompletableFuture<Unit> {
            return sessionDatabase.delete(session.gameId)
        }

        override fun setActive(active: Boolean): CompletableFuture<Unit> {
            return statusDatabase.setActive(session.gameId, active)
        }

    }
}