package kr.rendog.nations.core.war

import kr.rendog.nations.core.mysql.MySQLWarSessionDatabase
import kr.rendog.nations.war.NationWarSessionRegistry
import kr.rendog.nations.war.SessionData
import kr.rendog.nations.war.WarSession
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap

class RendogWarSessionRegistry(
    factory: MatchSessionFactory,
    sessionDatabase: MySQLWarSessionDatabase
): NationWarSessionRegistry {

    override val local = LocalImpl()
    override val direct = DirectImpl(sessionDatabase, factory, local)
    override fun get(matchId: Int): WarSession? {
        return local[matchId] ?: direct[matchId].join()
    }

    override fun getMatch(nation: Int): WarSession? {
        return local.getMatch(nation) ?: direct.getMatch(nation).join()
    }

    class LocalImpl : NationWarSessionRegistry.Local {

        private val map = ConcurrentHashMap<Int, WarSession>()

        override fun get(matchId: Int): WarSession? {
            return map[matchId]
        }

        override fun getMatch(nation: Int): WarSession? {
            return map.values.firstOrNull { it.home.id == nation || it.away.id == nation }
        }

        override fun add(session: WarSession) {
            map[session.gameId] = session
        }

        override fun remove(matchId: Int) {
            map.remove(matchId)
        }

        override fun clear() {
            map.clear()
        }

    }

    class DirectImpl(
        private val database: MySQLWarSessionDatabase,
        private val factory: MatchSessionFactory,
        private val local: NationWarSessionRegistry.Local
    ) : NationWarSessionRegistry.Direct {

        override fun get(matchId: Int): CompletableFuture<WarSession?> {
            val localSession = local.get(matchId)
            return if(localSession == null)
                database.getSession(matchId).thenApply { buildOrNull(it) }
            else
                CompletableFuture.completedFuture(localSession)
        }

        override fun getMatch(nation: Int): CompletableFuture<WarSession?> {
            val localSession = local.getMatch(nation)
            return if(localSession == null)
                database.getSessionByNation(nation).thenApply { buildOrNull(it) }
            else
                CompletableFuture.completedFuture(localSession)
        }

        override fun getCurrentSessions(): CompletableFuture<List<WarSession>> {
            return database.getAll().thenApply { list -> list.map { buildOrNull(it)!! } }
        }

        override fun delete(matchId: Int): CompletableFuture<Unit> {
            return database.delete(matchId)
        }

        private infix fun NationWarSessionRegistry.Local.add(session: WarSession) : WarSession{
            return session.also { local.add(it) }
        }

        private fun buildOrNull(data: SessionData?): WarSession?{
            return data?.let{ local add factory.build(data) }
        }
    }
}