package com.minepalm.nations.core

import com.minepalm.nations.Nation
import com.minepalm.nations.NationMember
import com.minepalm.nations.NationMemberRegistry
import com.minepalm.nations.NationRank
import com.minepalm.nations.core.mysql.MySQLNationMemberDatabase
import java.util.*
import java.util.concurrent.CompletableFuture

class PalmNationDirect(
    private val nationId : Int,
    private val registry : NationMemberRegistry,
    private val database : MySQLNationMemberDatabase
) : Nation.Direct {

    override fun getOwner(): CompletableFuture<NationMember> {
        return database.getMembers(nationId, NationRank.OWNER)
            .thenApply { set -> set.first().let { registry[it] } }
    }

    override fun getMembers(): CompletableFuture<List<NationMember>> {
        return database.getMembers(nationId)
            .thenApply { it.map { entry -> registry[entry.key] } }
    }

    override fun getMembers(rank: NationRank): CompletableFuture<List<NationMember>> {
        return database.getMembers(nationId, rank)
            .thenApply { it.map { uuid -> registry[uuid] } }
    }

    override fun getRank(uniqueId: UUID): CompletableFuture<NationRank> {
        return database.getRank(uniqueId, nationId)
    }

    override fun getRanks(): CompletableFuture<Map<UUID, NationRank>> {
        return database.getMembers(nationId)
    }

}