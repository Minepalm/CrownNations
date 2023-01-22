package com.minepalm.nations.core.territory

import com.google.common.cache.CacheBuilder
import com.minepalm.nations.core.mysql.MySQLTerritoryLocationDatabase
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

class PalmNationWorldUniverse(
    private val network: com.minepalm.nations.server.NationNetwork,
    service: com.minepalm.nations.territory.NationTerritoryService,
    worldList: List<String>,
    factory: MonumentFactory,
    database: MySQLTerritoryLocationDatabase,
) : com.minepalm.nations.territory.NationWorldUniverse {

    private val worldFactory = WorldFactory(network, factory, service, database)

    override val host: com.minepalm.nations.territory.NationWorldUniverse.Host = HostImpl()
    override val cache: com.minepalm.nations.territory.NationWorldUniverse.Cache = CacheImpl(worldFactory)

    init {
        worldList.map {
            worldFactory.build(network.host.name, it)
        }.forEach {
            host.add(it!!)
        }
    }

    override fun get(server: String, name: String): com.minepalm.nations.territory.NationWorld? {
        return if(server == network.host.name) {
            host[name]
        }else {
            cache.getWorld(server, name)
        }
    }

    override fun update(loc: com.minepalm.nations.utils.ServerLoc, monumentId: Int) {
        cache.getWorldUnchecked(loc.server, loc.world)?.remote?.update(monumentId)

    }


    class HostImpl: com.minepalm.nations.territory.NationWorldUniverse.Host {

        private val localWorlds = ConcurrentHashMap<String, com.minepalm.nations.territory.NationWorld>()

        override fun get(monumentId: Int): com.minepalm.nations.territory.NationMonument? {
            return localWorlds.values.firstOrNull { it.local[monumentId] != null }?.get(monumentId)
        }

        override fun get(worldName: String): com.minepalm.nations.territory.NationWorld? {
            return localWorlds[worldName]
        }

        override fun get(loc: com.minepalm.nations.utils.ServerLoc): com.minepalm.nations.territory.NationWorld? {
            return localWorlds[loc.world]
        }

        override fun getWorlds(): List<com.minepalm.nations.territory.NationWorld> {
            return localWorlds.values.toList()
        }

        override fun isInNationWorld(loc: com.minepalm.nations.utils.ServerLoc): Boolean {
            return localWorlds.containsKey(loc.world)
        }

        override fun add(world: com.minepalm.nations.territory.NationWorld) {
            localWorlds[world.name] = world
        }

        override fun remove(worldName: String) {
            localWorlds.remove(worldName)
        }

        override fun deleteInvalidateMonuments(): List<com.minepalm.nations.territory.NationMonument> {
            return mutableListOf<com.minepalm.nations.territory.NationMonument>().apply {
                getWorlds().forEach { if(it.isLocal) it.local.deleteInvalidatedMonuments().also { this.addAll(it) } }
            }
        }

    }

    class CacheImpl(private val factory: WorldFactory): com.minepalm.nations.territory.NationWorldUniverse.Cache {

        private val cache = CacheBuilder.newBuilder()
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .build<Pair<String, String>, com.minepalm.nations.territory.NationWorld>()

        override fun getWorld(server: String, name: String): com.minepalm.nations.territory.NationWorld {
            val key = server to name
            return cache.getIfPresent(key) ?: buildOrThrow(server, name).also { cache.put(key, it) }
        }

        override fun getWorldUnchecked(server: String, name: String): com.minepalm.nations.territory.NationWorld? {
            val key = server to name
            return cache.getIfPresent(key)
        }

        private fun buildOrThrow(server: String, name: String): com.minepalm.nations.territory.NationWorld {
            return factory.build(server, name) ?: throw IllegalArgumentException("server: $server is not exists")
        }

    }

    class WorldFactory(
        private val network: com.minepalm.nations.server.NationNetwork,
        private val factory: MonumentFactory,
        private val service: com.minepalm.nations.territory.NationTerritoryService,
        private val database: MySQLTerritoryLocationDatabase,
    ){

        fun build(server: String, name: String): com.minepalm.nations.territory.NationWorld?{
            val nationServer = network.getServer(server)
            val isLocal = network.host.name == server
            return nationServer?.let { PalmNationWorld(it, name, isLocal, service, database, factory) }
        }

    }

}