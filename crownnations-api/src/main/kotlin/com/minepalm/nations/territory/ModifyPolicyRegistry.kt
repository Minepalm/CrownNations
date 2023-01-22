package com.minepalm.nations.territory

import com.minepalm.nations.NationMember

interface ModifyPolicyRegistry {

    fun registerPolicy(
        action: NationAction,
        policy: ModifyPolicy
    )

    operator fun get(action: NationAction): List<ModifyPolicy>

    fun test(
        operator: NationMember,
        action: NationAction,
        location: com.minepalm.nations.utils.ServerLoc,
        monument: NationMonument
    ): Boolean
}