package kr.rendog.nations.core.territory

import kr.rendog.nations.Nation
import kr.rendog.nations.NationMember
import kr.rendog.nations.NationOperation
import kr.rendog.nations.territory.*
import kr.rendog.nations.utils.ServerLoc

abstract class AbstractNationMonument(
    override val id: Int,
    override val type: String,
    override val nationId: Int,
    override var center: ServerLoc,
    override val range: ProtectionRange,
    internal val service: NationTerritoryService
) : NationMonument {

    override val world: NationWorld
        get() = service.universe.host[center.world]!!
    override val owner: Nation
        get() = service.root.nationRegistry[nationId]!!

    override fun test(loc: ServerLoc, action: NationAction, member: NationMember): NationOperation<Boolean> {
        return service.operationFactory.buildOperateModify(this, member, action, loc)
    }

    override fun toSchema(): MonumentSchema {
        return MonumentSchema(id, nationId, type, center, range)
    }
}