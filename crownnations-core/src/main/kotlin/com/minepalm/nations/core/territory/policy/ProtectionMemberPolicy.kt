package com.minepalm.nations.core.territory.policy

import com.minepalm.nations.NationRank
import java.util.*

class ProtectionMemberPolicy : com.minepalm.nations.territory.ModifyPolicy {
    override fun test(action: com.minepalm.nations.territory.NationAction, actor: UUID, loc: com.minepalm.nations.utils.ServerLoc, monument: com.minepalm.nations.territory.NationMonument): Boolean {
        return monument.owner.cache.getRank(actor).hasPermissibleOf(NationRank.RESIDENT)
    }
}