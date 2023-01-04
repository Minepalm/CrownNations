package kr.rendog.nations.core.territory

import kr.rendog.nations.NationMember
import kr.rendog.nations.core.territory.policy.ProtectionMemberPVP
import kr.rendog.nations.core.territory.policy.ProtectionMemberPolicy
import kr.rendog.nations.territory.ModifyPolicy
import kr.rendog.nations.territory.ModifyPolicyRegistry
import kr.rendog.nations.territory.NationAction
import kr.rendog.nations.territory.NationMonument
import kr.rendog.nations.utils.ServerLoc
import java.util.concurrent.ConcurrentHashMap

class ModifyPolicyRegistryImpl : ModifyPolicyRegistry {

    private val map = ConcurrentHashMap<NationAction, MutableList<ModifyPolicy>>()

    init {
        ProtectionMemberPolicy().apply {
            registerPolicy(NationAction.INTERACT, this)
            registerPolicy(NationAction.DESTROY, this)
            registerPolicy(NationAction.PLACE, this)
        }
        registerPolicy(NationAction.PLAYER_ATTACK, ProtectionMemberPVP())
    }

    @Synchronized
    override fun registerPolicy(action: NationAction, policy: ModifyPolicy) {
        if(!map.containsKey(action))
            map[action] = mutableListOf()
        map[action]!!.add(policy)
    }

    override operator fun get(action: NationAction): List<ModifyPolicy> {
        return mutableListOf<ModifyPolicy>().apply { map[action]?.forEach { add(it) } }
    }

    override fun test(
        operator: NationMember,
        action: NationAction,
        location: ServerLoc,
        monument: NationMonument
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