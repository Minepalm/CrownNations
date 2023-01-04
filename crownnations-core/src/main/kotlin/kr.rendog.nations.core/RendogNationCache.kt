package kr.rendog.nations.core

import kr.rendog.nations.Nation
import kr.rendog.nations.NationMember
import kr.rendog.nations.NationMemberRegistry
import kr.rendog.nations.NationRank
import kr.rendog.nations.core.mysql.MySQLNationMemberDatabase
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap

class RendogNationCache(
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
        return database.getRank(uniqueId, nationId).thenApply{ if(it != NationRank.NONE) cache[uniqueId] = it }
    }

    override fun invalidate() {
        cache.clear()
    }
}