package kr.rendog.nations.core

import kr.rendog.nations.Nation
import kr.rendog.nations.NationRank
import kr.rendog.nations.core.mysql.MySQLNationIdDatabase
import kr.rendog.nations.core.mysql.MySQLNationMemberDatabase
import java.util.*
import java.util.concurrent.CompletableFuture

class RendogNationUnsafe(
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