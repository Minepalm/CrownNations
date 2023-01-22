package com.minepalm.nations.core.territory

import com.google.common.cache.CacheBuilder
import com.minepalm.nations.core.mysql.MySQLTerritoryLocationDatabase
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.TimeUnit

class PalmTerritoryRegistry(
    private val service: PalmNationTerritoryService,
    private val database: MySQLTerritoryLocationDatabase,
    private val worker: ExecutorService
) : com.minepalm.nations.territory.NationTerritoryRegistry {

    private val map = ConcurrentHashMap<Int, com.minepalm.nations.territory.NationTerritory>()
    private val cache = CacheBuilder.newBuilder()
        .expireAfterAccess(5, TimeUnit.MINUTES)
        .build<Int, com.minepalm.nations.territory.NationTerritory>()

    override fun get(nationId: Int): com.minepalm.nations.territory.NationTerritory? {
        return getLocal(nationId) ?: getCached(nationId)
    }

    override fun getCached(nationId: Int): com.minepalm.nations.territory.NationTerritory? {
        return cache.getIfPresent(nationId) ?: loadCached(nationId)
    }

    override fun getLocal(nationId: Int): com.minepalm.nations.territory.NationTerritory? {
        return map[nationId]
    }

    override fun load(nationId: Int): com.minepalm.nations.territory.NationTerritory {
        if(!map.containsKey(nationId))
            map[nationId] = build(nationId)
        return map[nationId]!!
    }

    private fun loadCached(nationId: Int): com.minepalm.nations.territory.NationTerritory?{
        cache.put(nationId, build(nationId))
        return cache.getIfPresent(nationId)
    }

    private fun build(nationId: Int): com.minepalm.nations.territory.NationTerritory {
        return PalmTerritory(nationId, service, database, worker)
    }

    override fun unload(nationId: Int) {
        cache.invalidate(nationId)
        map.remove(nationId)
    }

    override fun shutdown() {
        worker.shutdown()
    }
}