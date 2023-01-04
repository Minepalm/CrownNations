package kr.rendog.nations.core.territory

import com.google.common.cache.CacheBuilder
import kr.rendog.nations.core.mysql.MySQLTerritoryLocationDatabase
import kr.rendog.nations.territory.NationTerritory
import kr.rendog.nations.territory.NationTerritoryRegistry
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.TimeUnit

class RendogTerritoryRegistry(
    private val service: RendogNationTerritoryService,
    private val database: MySQLTerritoryLocationDatabase,
    private val worker: ExecutorService
) : NationTerritoryRegistry {

    private val map = ConcurrentHashMap<Int, NationTerritory>()
    private val cache = CacheBuilder.newBuilder()
        .expireAfterAccess(5, TimeUnit.MINUTES)
        .build<Int, NationTerritory>()

    override fun get(nationId: Int): NationTerritory? {
        return getLocal(nationId) ?: getCached(nationId)
    }

    override fun getCached(nationId: Int): NationTerritory? {
        return cache.getIfPresent(nationId) ?: loadCached(nationId)
    }

    override fun getLocal(nationId: Int): NationTerritory? {
        return map[nationId]
    }

    override fun load(nationId: Int): NationTerritory {
        if(!map.containsKey(nationId))
            map[nationId] = build(nationId)
        return map[nationId]!!
    }

    private fun loadCached(nationId: Int): NationTerritory?{
        cache.put(nationId, build(nationId))
        return cache.getIfPresent(nationId)
    }

    private fun build(nationId: Int): NationTerritory{
        return RendogTerritory(nationId, service, database, worker)
    }

    override fun unload(nationId: Int) {
        cache.invalidate(nationId)
        map.remove(nationId)
    }

    override fun shutdown() {
        worker.shutdown()
    }
}