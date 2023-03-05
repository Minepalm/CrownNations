package com.minepalm.nations.bukkit

import com.minepalm.nations.*
import com.minepalm.nations.utils.ServerLoc
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class CreationSessionRegistry(
    private val service: NationService
) {

    private val map = ConcurrentHashMap<UUID, String>()

    fun exists(uuid: UUID) = map.containsKey(uuid)

    operator fun get(uuid: UUID): String? = map[uuid]

    fun prepare(uuid: UUID, nationName: String): OperationResult<Boolean> {
        return service.operationFactory.buildCreate(member(uuid), nationName).check()
            .apply {
                if(result == true){
                    map[uuid] = nationName
                }
            }
    }

    fun operate(uuid: UUID, loc: ServerLoc): NationOperation<Nation>? {
        return if (map.containsKey(uuid)) {
            service.operateFoundation(member(uuid), map[uuid]!!, loc).also { map.remove(uuid) }
        } else
            null
    }

    private fun Any?.member(uuid: UUID): NationMember {
        return service.memberRegistry[uuid]
    }
}