package kr.rendog.nations.territory

import kr.rendog.nations.NationMember
import kr.rendog.nations.utils.ServerLoc

interface ModifyPolicyRegistry {

    fun registerPolicy(action: NationAction, policy: ModifyPolicy)

    operator fun get(action: NationAction): List<ModifyPolicy>

    fun test(operator: NationMember, action: NationAction, location: ServerLoc, monument: NationMonument): Boolean
}