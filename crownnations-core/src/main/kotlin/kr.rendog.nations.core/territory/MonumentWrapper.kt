package kr.rendog.nations.core.territory

import kr.rendog.nations.Nation
import kr.rendog.nations.NationMember
import kr.rendog.nations.NationOperation
import kr.rendog.nations.territory.*
import kr.rendog.nations.utils.ServerLoc
import java.util.concurrent.CompletableFuture

open class MonumentWrapper(
    private val monument: NationMonument,
) : NationMonument{
    override val id: Int
        get() = monument.id
    override val type: String
        get() = monument.type
    override val world: NationWorld
        get() = monument.world
    override var center: ServerLoc
        get() = monument.center
        set(value){
            monument.center = value
        }
    override val range: ProtectionRange
        get() = monument.range

    override val nationId: Int
        get() = monument.nationId
    override val owner: Nation
        get() = monument.owner
    override val isLocal: Boolean
        get() = monument.isLocal

    override fun test(loc: ServerLoc, action: NationAction, member: NationMember): NationOperation<Boolean> {
        return monument.test(loc, action, member)
    }

    override fun toData(): CompletableFuture<MonumentBlob> {
        return monument.toData()
    }

    override fun toSchema(): MonumentSchema {
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