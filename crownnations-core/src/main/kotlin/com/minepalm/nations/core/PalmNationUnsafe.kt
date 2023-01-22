package com.minepalm.nations.core

import com.minepalm.nations.Nation
import com.minepalm.nations.NationRank
import com.minepalm.nations.core.mysql.MySQLNationIdDatabase
import com.minepalm.nations.core.mysql.MySQLNationMemberDatabase
import java.util.*
import java.util.concurrent.CompletableFuture

class PalmNationUnsafe(
    private val nationId : Int,
    private val database : MySQLNationMemberDatabase,
    private val idDatabase : MySQLNationIdDatabase
) : Nation.Unsafe {
    override fun setOwner(uniqueId: UUID): CompletableFuture<Boolean> {
        return database.transferOwnership(nationId, uniqueId)
    }

    override fun addMember(uniqueId: UUID): CompletableFuture<Boolean> {
        return database.setMember(uniqueId, nationId, NationRank.RESIDENT)
    }

    override fun removeMember(uniqueId: UUID): CompletableFuture<Boolean> {
        return database.removeMember(nationId, uniqueId)
    }

    override fun setMemberRank(uniqueID: UUID, rank: NationRank): CompletableFuture<Boolean> {
        return database.setMember(uniqueID, nationId, rank)
    }

    override fun delete(): CompletableFuture<Boolean> {
        return idDatabase.deleteId(nationId)
    }
}