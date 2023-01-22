package com.minepalm.nations.core.territory

import com.minepalm.nations.Nation
import com.minepalm.nations.NationMember
import com.minepalm.nations.NationOperation
import java.util.concurrent.CompletableFuture

open class MonumentWrapper(
    private val monument: com.minepalm.nations.territory.NationMonument,
) : com.minepalm.nations.territory.NationMonument {
    override val id: Int
        get() = monument.id
    override val type: String
        get() = monument.type
    override val world: com.minepalm.nations.territory.NationWorld
        get() = monument.world
    override var center: com.minepalm.nations.utils.ServerLoc
        get() = monument.center
        set(value){
            monument.center = value
        }
    override val range: com.minepalm.nations.territory.ProtectionRange
        get() = monument.range

    override val nationId: Int
        get() = monument.nationId
    override val owner: Nation
        get() = monument.owner
    override val isLocal: Boolean
        get() = monument.isLocal

    override fun test(loc: com.minepalm.nations.utils.ServerLoc, action: com.minepalm.nations.territory.NationAction, member: NationMember): NationOperation<Boolean> {
        return monument.test(loc, action, member)
    }

    override fun toData(): CompletableFuture<com.minepalm.nations.territory.MonumentBlob> {
        return monument.toData()
    }

    override fun toSchema(): com.minepalm.nations.territory.MonumentSchema {
        return monument.toSchema()
    }

    override fun save(): CompletableFuture<Unit> {
        return monument.save()
    }

    override fun load(): CompletableFuture<Unit> {
        return monument.load()
    }

    override fun collapse(): CompletableFuture<Boolean> {
        return monument.collapse()
    }
}