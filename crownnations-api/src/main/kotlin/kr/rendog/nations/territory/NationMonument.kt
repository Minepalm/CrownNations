package kr.rendog.nations.territory

import kr.rendog.nations.Nation
import kr.rendog.nations.NationMember
import kr.rendog.nations.NationOperation
import kr.rendog.nations.utils.ServerLoc
import java.util.concurrent.CompletableFuture

interface NationMonument {

    val id: Int
    val type: String
    val world: NationWorld
    var center: ServerLoc
    val range: ProtectionRange
    val nationId: Int
    val owner: Nation
    val isLocal: Boolean

    fun test(loc: ServerLoc, action: NationAction, member: NationMember): NationOperation<Boolean>

    fun toData(): CompletableFuture<MonumentBlob>

    fun toSchema(): MonumentSchema

    fun save(): CompletableFuture<Unit>

    fun load(): CompletableFuture<Unit>

    fun collapse(): CompletableFuture<Boolean>
}