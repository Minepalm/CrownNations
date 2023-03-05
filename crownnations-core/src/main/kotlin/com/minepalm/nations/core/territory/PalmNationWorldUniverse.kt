package com.minepalm.nations.core.territory

import com.google.common.cache.CacheBuilder
import com.minepalm.nations.core.mysql.MySQLTerritoryLocationDatabase
import com.minepalm.nations.server.NationNetwork
import com.minepalm.nations.territory.NationMonument
import com.minepalm.nations.territory.NationTerritoryService
import com.minepalm.nations.territory.NationWorld
import com.minepalm.nations.territory.NationWorldUniverse
import com.minepalm.nations.utils.ServerLoc
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

class PalmNationWorldUniverse(
    private val network: NationNetwork,
    service: NationTerritoryService,
    worldList: List<String>,
    factory: MonumentFactory,
    database: MySQLTerritoryLocationDatabase,
) : NationWorldUniverse {

    private val worldFactory = WorldFactory(network, factory, service, database)

    override val host: NationWorldUniverse.Host = HostImpl()
    override val cache: NationWorldUniverse.Cache = CacheImpl(worldFactory)

    init {
        worldList.map {
            worldFactory.build(network.host.name, it)
        }.forEach {
            host.add(it!!)
        }
    }

    override fun get(server: String, name: String): NationWorld? {
        return if (server == network.host.name) {
            host[name]
        } else {
            cache.getWorld(server, name)
        }
    }

    override fun update(loc: ServerLoc, monumentId: Int) {
        cache.getWorldUnchecked(loc.server, loc.world)?.remote?.update(monumentId)

    }


    class HostImpl : NationWorldUniverse.Host {

        private val localWorlds = ConcurrentHashMap<String, NationWorld>()

        override fun get(monumentId: Int): NationMonument? {
            return localWorlds.values.firstOrNull { it.local[monumentId] != null }?.get(monumentId)
        }

        override fun get(worldName: String): NationWorld? {
            return localWorlds[worldName]
        }

        override fun get(loc: ServerLoc): NationWorld? {
            return localWorlds[loc.world]
        }

        override fun getWorlds(): List<NationWorld> {
            return localWorlds.values.toList()
        }

        override fun isInNationWorld(loc: ServerLoc): Boolean {
            return localWorlds.containsKey(loc.world)
        }

        override fun add(world: NationWorld) {
            localWorlds[world.name] = world
        }

        override fun remove(worldName: String) {
            localWorlds.remove(worldName)
        }

        override fun deleteInvalidateMonuments(): List<NationMonument> {
            return mutableListOf<NationMonument>().apply {
                getWorlds().forEach { if (it.isLocal) it.local.deleteInvalidatedMonuments().also { this.addAll(it) } }
            }
        }

    }

    class CacheImpl(private val factory: WorldFactory) : NationWorldUniverse.Cache {

        private val cache = CacheBuilder.newBuilder()
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .build<Pair<String, String>, NationWorld>()

        override fun getWorld(server: String, name: String): NationWorld {
            val key = server to name
            return cache.getIfPresent(key) ?: buildOrThrow(server, name).also { cache.put(key, it) }
        }

        override fun getWorldUnchecked(server: String, name: String): NationWorld? {
            val key = server to name
            return cache.getIfPresent(key)
        }

        private fun buildOrThrow(server: String, name: String): NationWorld {
            return factory.build(server, name) ?: throw IllegalArgumentException("server: $server is not exists")
        }

    }

    class WorldFactory(
        private val network: NationNetwork,
        private val factory: MonumentFactory,
        private val service: NationTerritoryService,
        private val database: MySQLTerritoryLocationDatabase,
    ){

        fun build(server: String, name: String): NationWorld? {
            val nationServer = network.getServer(server)
            val isLocal = network.host.name == server
            return nationServer?.let { PalmNationWorld(it, name, isLocal, service, database, factory) }
        }

    }

}