package kr.rendog.nations.core.territory.policy

import kr.rendog.nations.NationRank
import kr.rendog.nations.territory.ModifyPolicy
import kr.rendog.nations.territory.NationAction
import kr.rendog.nations.territory.NationMonument
import kr.rendog.nations.utils.ServerLoc
import java.util.*

class ProtectionMemberPolicy : ModifyPolicy{
    override fun test(action: NationAction, actor: UUID, loc: ServerLoc, monument: NationMonument): Boolean {
        return monument.owner.cache.getRank(actor).hasPermissibleOf(NationRank.RESIDENT)
    }
}