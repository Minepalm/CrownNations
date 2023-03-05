package com.minepalm.nations.territory

import com.minepalm.nations.Nation
import com.minepalm.nations.NationMember
import com.minepalm.nations.NationOperation
import com.minepalm.nations.utils.ServerLoc
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

    fun test(
        loc: ServerLoc,
        action: NationAction,
        member: NationMember
    ): NationOperation<Boolean>

    fun toData(): CompletableFuture<MonumentBlob>

    fun toSchema(): MonumentSchema

    fun save(): CompletableFuture<Unit>

    fun load(): CompletableFuture<Unit>

    fun collapse(): CompletableFuture<Boolean>
}