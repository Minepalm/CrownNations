package com.minepalm.nations.core

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.minepalm.nations.NationMember
import com.minepalm.nations.NationMemberRegistry
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.TimeUnit

class PalmNationMemberRegistry(
    factory: PalmNationMemberFactory,
    private val executor : ExecutorService
) : NationMemberRegistry {

    override val cache = CacheImpl(factory)
    override val local = LocalImpl(factory, executor)

    override fun get(uuid: UUID): NationMember {
        return local[uuid] ?: cache[uuid]
    }

    override fun update(uuid: UUID) {
        executor.execute {
            local.updateInternally(uuid)
            cache.updateInternally(uuid)
        }
    }

    class LocalImpl(
        private val factory: PalmNationMemberFactory,
        private val executor : ExecutorService
        ) : NationMemberRegistry.Local{

        private val membersLocal = ConcurrentHashMap<UUID, NationMember>()

        override operator fun get(uuid: UUID): NationMember? {
            return membersLocal[uuid]
        }

        override fun load(uuid: UUID): CompletableFuture<NationMember> {
            return factory.build(uuid).thenApply { it.apply { membersLocal[it.uniqueId] = it } }
        }

        override fun unload(uuid: UUID): CompletableFuture<Boolean> {
            return CompletableFuture.supplyAsync({ membersLocal[uuid]?.cache?.invalidate() != null }, executor)
        }

        internal fun updateInternally(uuid: UUID){
            membersLocal[uuid]?.cache?.update()
        }
    }

    class CacheImpl(factory: PalmNationMemberFactory) : NationMemberRegistry.Cache{

        private val membersCache = CacheBuilder.newBuilder()
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .build(object : CacheLoader<UUID, NationMember>(){

                override fun load(key: UUID): NationMember {
                    return factory.build(key).join()
                }

            })

        override operator fun get(uuid: UUID): NationMember {
            return membersCache[uuid]
        }

        internal fun updateInternally(uuid: UUID){
            membersCache.refresh(uuid)
        }
    }
}