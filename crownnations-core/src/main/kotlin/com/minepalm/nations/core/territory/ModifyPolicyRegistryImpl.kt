package com.minepalm.nations.core.territory

import com.minepalm.nations.NationMember
import com.minepalm.nations.core.territory.policy.ProtectionMemberPVP
import com.minepalm.nations.core.territory.policy.ProtectionMemberPolicy
import java.util.concurrent.ConcurrentHashMap

class ModifyPolicyRegistryImpl : com.minepalm.nations.territory.ModifyPolicyRegistry {

    private val map = ConcurrentHashMap<com.minepalm.nations.territory.NationAction, MutableList<com.minepalm.nations.territory.ModifyPolicy>>()

    init {
        ProtectionMemberPolicy().apply {
            registerPolicy(com.minepalm.nations.territory.NationAction.INTERACT, this)
            registerPolicy(com.minepalm.nations.territory.NationAction.DESTROY, this)
            registerPolicy(com.minepalm.nations.territory.NationAction.PLACE, this)
        }
        registerPolicy(com.minepalm.nations.territory.NationAction.PLAYER_ATTACK, ProtectionMemberPVP())
    }

    @Synchronized
    override fun registerPolicy(action: com.minepalm.nations.territory.NationAction, policy: com.minepalm.nations.territory.ModifyPolicy) {
        if(!map.containsKey(action))
            map[action] = mutableListOf()
        map[action]!!.add(policy)
    }

    override operator fun get(action: com.minepalm.nations.territory.NationAction): List<com.minepalm.nations.territory.ModifyPolicy> {
        return mutableListOf<com.minepalm.nations.territory.ModifyPolicy>().apply { map[action]?.forEach { add(it) } }
    }

    override fun test(
        operator: NationMember,
        action: com.minepalm.nations.territory.NationAction,
        location: com.minepalm.nations.utils.ServerLoc,
        monument: com.minepalm.nations.territory.NationMonument
    ): Boolean {
        var permit = true
        for (modifyPolicy in this[action]) {
            permit = modifyPolicy.test(action, operator.uniqueId, location, monument)
            if(!permit)
                break
        }
        return permit
    }


}