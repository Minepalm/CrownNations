package com.minepalm.nations.core

import com.minepalm.nations.Nation
import com.minepalm.nations.NationMember
import com.minepalm.nations.NationMemberRegistry
import com.minepalm.nations.NationRank
import com.minepalm.nations.core.mysql.MySQLNationMemberDatabase
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap

class PalmNationCache(
    private val nationId : Int,
    private val registry : NationMemberRegistry,
    private val database : MySQLNationMemberDatabase
) : Nation.Cache {

    private val cache = ConcurrentHashMap<UUID, NationRank>()

    init {
        update()
    }
    override fun getOwner(): NationMember {
        cache.forEach{
            if(it.value == NationRank.OWNER)
                return registry[it.key]
        }

        throw IllegalStateException("owner is not found")
    }

    override fun getMembers(): List<NationMember> {
        return cache.keys.map { registry[it] }
    }

    override fun getRanks(): Map<UUID, NationRank> {
        return mutableMapOf<UUID, NationRank>().apply { putAll(cache) }
    }

    override fun getRank(uniqueId: UUID): NationRank {
        return cache.getOrDefault(uniqueId, NationRank.NONE)
    }

    override fun update(map: Map<UUID, NationRank>) {
        cache.putAll(map)
    }

    override fun update(): CompletableFuture<Unit> {
        return database.getMembers(nationId).thenApply { update(it) }
    }

    override fun update(uniqueId: UUID): CompletableFuture<Unit> {
        return database.getRank(uniqueId, nationId).thenApply{
            if(it != NationRank.NONE)
                cache[uniqueId] = it
            else
                cache.remove(uniqueId)
        }
    }

    override fun invalidate() {
        cache.clear()
    }
}