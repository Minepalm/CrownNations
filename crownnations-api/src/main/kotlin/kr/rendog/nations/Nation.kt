package kr.rendog.nations

import kr.rendog.nations.bank.NationBank
import kr.rendog.nations.grade.NationGrade
import kr.rendog.nations.territory.NationTerritory
import kr.rendog.nations.war.NationWar
import java.util.*
import java.util.concurrent.CompletableFuture

interface Nation {

    val id: Int
    val name: String
    val cache: Cache
    val unsafe: Unsafe
    val direct: Direct
    val metadata: NationMetadata
    val grade: NationGrade
    val bank: NationBank
    val territory: NationTerritory
    val war: NationWar

    interface Cache{

        fun getOwner(): NationMember

        fun getMembers(): List<NationMember>

        fun getRanks(): Map<UUID, NationRank>

        fun getRank(uniqueId: UUID): NationRank

        fun update(map: Map<UUID, NationRank>)

        fun update(): CompletableFuture<Unit>

        fun update(uniqueId: UUID): CompletableFuture<Unit>

        fun invalidate()

    }

    interface Direct{

        fun getOwner(): CompletableFuture<NationMember>

        fun getMembers(): CompletableFuture<List<NationMember>>

        fun getRank(uniqueId: UUID): CompletableFuture<NationRank>

        fun getRanks(): CompletableFuture<Map<UUID, NationRank>>

        fun getMembers(rank: NationRank): CompletableFuture<List<NationMember>>

    }

    interface Unsafe{

        fun setOwner(uniqueId: UUID) : CompletableFuture<Boolean>

        fun addMember(uniqueId: UUID) : CompletableFuture<Boolean>

        fun removeMember(uniqueId: UUID) : CompletableFuture<Boolean>

        fun setMemberRank(uniqueID: UUID, rank: NationRank) : CompletableFuture<Boolean>

        fun delete(): CompletableFuture<Boolean>

    }
    fun operateSetMember(commander : NationMember, uniqueId : UUID, rank : NationRank) : NationOperation<Boolean>

    fun operateAddMember(commander : NationMember, uniqueId : UUID) : NationOperation<Boolean>

    fun operateKickMember(commander : NationMember, uniqueId : UUID) : NationOperation<Boolean>

    fun operateTransferOwner(commander : NationMember, uniqueId : UUID) : NationOperation<Boolean>

    fun operateDisband(commander : NationMember) : NationOperation<Boolean>

    fun operateChangeMetadata(commander : NationMember, key : String, value : String) : NationOperation<Boolean>

}