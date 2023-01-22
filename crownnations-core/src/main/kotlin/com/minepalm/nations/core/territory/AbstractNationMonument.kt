package com.minepalm.nations.core.territory

import com.minepalm.nations.Nation
import com.minepalm.nations.NationMember
import com.minepalm.nations.NationOperation

abstract class AbstractNationMonument(
    override val id: Int,
    override val type: String,
    override val nationId: Int,
    override var center: com.minepalm.nations.utils.ServerLoc,
    override val range: com.minepalm.nations.territory.ProtectionRange,
    internal val service: com.minepalm.nations.territory.NationTerritoryService
) : com.minepalm.nations.territory.NationMonument {

    override val world: com.minepalm.nations.territory.NationWorld
        get() = service.universe.host[center.world]!!
    override val owner: Nation
        get() = service.root.nationRegistry[nationId]!!

    override fun test(loc: com.minepalm.nations.utils.ServerLoc, action: com.minepalm.nations.territory.NationAction, member: NationMember): NationOperation<Boolean> {
        return service.operationFactory.buildOperateModify(this, member, action, loc)
    }

    override fun toSchema(): com.minepalm.nations.territory.MonumentSchema {
        return com.minepalm.nations.territory.MonumentSchema(id, nationId, type, center, range)
    }
}