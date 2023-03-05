package com.minepalm.nations.core.territory

import com.google.common.cache.CacheBuilder
import com.minepalm.nations.Nation
import com.minepalm.nations.core.mysql.MySQLTerritoryLocationDatabase
import com.minepalm.nations.event.TerritoryDecomposeEvent
import com.minepalm.nations.server.NationServer
import com.minepalm.nations.territory.MonumentSchema
import com.minepalm.nations.territory.NationMonument
import com.minepalm.nations.territory.NationTerritoryService
import com.minepalm.nations.territory.NationWorld
import com.minepalm.nations.utils.ServerLoc
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import kotlin.math.pow
import kotlin.math.sqrt

class PalmNationWorld(
    override val server: NationServer,
    override val name: String,
    override val isLocal: Boolean,
    service: NationTerritoryService,
    database: MySQLTerritoryLocationDatabase,
    factory: MonumentFactory
) : NationWorld {

    override val remote: NationWorld.Remote =
        RemoteImpl(server.name, name, database, factory)

    override val local: NationWorld.Local
        get() = if (isLocal) localImpl else throw IllegalStateException("cannot access remote world as local")

    private val localImpl: LocalImpl = LocalImpl(service, factory, database)

    init {
        if (isLocal) {
            remote.loadAll().thenApply { localImpl.loadAll(it) }.join()
        }
    }

    override fun get(id: Int): NationMonument? {
        return localImpl[id] ?: remote[id].join()
    }

    override fun get(nation: Nation): List<NationMonument> {
        return if (isLocal) localImpl[nation] else remote[nation].join()
    }

    class LocalImpl(
        private val service: NationTerritoryService,
        private val factory: MonumentFactory,
        private val database: MySQLTerritoryLocationDatabase
    ) : NationWorld.Local {

        private val localMap = ConcurrentHashMap<Int, NationMonument>()

        override fun exists(monumentId: Int): Boolean {
            return localMap.containsKey(monumentId)
        }

        override fun nearest(type: String, loc: ServerLoc): Double? {
            return localMap.values
                .filter { it.type == type }
                .also { if (it.isEmpty()) return null }
                .minOf {
                    val distanceX = it.center.x.rem(loc.x).toDouble().pow(2)
                    val distanceY = it.center.y.rem(loc.y).toDouble().pow(2)
                    val distanceZ = it.center.z.rem(loc.z).toDouble().pow(2)
                    sqrt(distanceX + distanceY + distanceZ)
                }
        }

        override fun getMonuments(): List<NationMonument> {
            return localMap.values.let {
                mutableListOf<NationMonument>().apply {
                    addAll(
                        it
                    )
                }
            }
        }

        override fun add(monument: NationMonument) {
            localMap[monument.id] = monument
        }

        override fun remove(monumentId: Int) {
            localMap.remove(monumentId)
        }

        override fun get(loc: ServerLoc): NationMonument? {
            localMap.forEach {
                if (it.value.range.isIn(loc)) {
                    return it.value
                }
            }
            return null
        }

        override operator fun get(id: Int): NationMonument? {
            return localMap[id]
        }

        override operator fun get(nation: Nation): List<NationMonument> {
            return localMap.values.filter { it.nationId == nation.id }
        }

        override fun create(schema: MonumentSchema): CompletableFuture<Boolean> {
            return database.createNewMonument(schema).thenApply { result ->
                if (result.id >= 0) {
                    buildAndRegister(result)
                    true
                } else
                    false
            }
        }

        override fun delete(monumentId: Int): CompletableFuture<Boolean> {
            return if (exists(monumentId)) {
                remove(monumentId)
                database.deleteMonument(monumentId).thenApply { true }
            } else {
                CompletableFuture.completedFuture(false)
            }

        }

        override fun deleteInvalidatedMonuments(): List<NationMonument> {
            return localMap.values.mapNotNull { monument ->
                service.root.nationRegistry.direct.exists(monument.nationId).thenApply { exist ->
                    if (exist.not()) {
                        val event = TerritoryDecomposeEvent(
                            monument.nationId,
                            monument.id,
                            monument.type,
                            monument.center
                        )
                        service.root.localEventBus.invoke(event)
                        service.root.network.send(event)
                        if (event.cancelled.not())
                            monument.apply { collapse().join() }
                        else
                            null
                    } else {
                        null
                    }
                }.join()
            }
        }

        internal fun loadAll(list: List<MonumentSchema>) {
            list.forEach { localMap[it.id] = factory.build(it) }
        }

        private fun buildAndRegister(schema: MonumentSchema): NationMonument {
            return factory.build(schema).apply { add(this) }
        }

    }

    class RemoteImpl(
        private val server: String,
        private val worldName: String,
        private val database: MySQLTerritoryLocationDatabase,
        private val factory: MonumentFactory
    ) : NationWorld.Remote {

        internal val cache = CacheBuilder.newBuilder()
            .expireAfterAccess(5, TimeUnit.MINUTES)
            .build<Int, NationMonument>()

        override fun get(id: Int): CompletableFuture<NationMonument?> {
            val monument = cache.getIfPresent(id)
            return if (monument != null) {
                CompletableFuture.completedFuture(monument)
            } else {
                database.getMonument(id).thenApply { it?.let { schema -> buildAndCache(schema) } }
            }
        }

        override fun get(nation: Nation): CompletableFuture<List<NationMonument>> {
            return database.getNationMonuments(nation.id)
                .thenApply { list ->
                    list.filter { it.center.server == server && it.center.world == worldName }
                        .map { buildOrGet(it) }
                }
        }

        override fun exists(monumentId: Int): CompletableFuture<Boolean> {
            return database.exists(monumentId)
        }

        private fun buildOrGet(schema: MonumentSchema): NationMonument {
            return cache.getIfPresent(schema.id) ?: buildAndCache(schema)
        }

        private fun buildAndCache(schema: MonumentSchema): NationMonument {
            return factory.build(schema).also { cache.put(schema.id, it) }
        }

        override fun getMonuments(): CompletableFuture<List<NationMonument>> {
            return loadAll().thenApply { it.map { schema -> buildOrGet(schema) } }
        }

        override fun loadAll(): CompletableFuture<List<MonumentSchema>> {
            return database.getServerWorldMonuments(server, worldName)
        }

        override fun update(id: Int) {
            exists(id).thenApply { present ->
                if (present.not()) cache.invalidate(id)
                else database.getMonument(id).thenApply { it?.let { schema -> buildAndCache(schema) } }
            }
        }

    }
}