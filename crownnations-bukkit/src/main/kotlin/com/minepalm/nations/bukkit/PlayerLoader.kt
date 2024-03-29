package com.minepalm.nations.bukkit

import com.minepalm.nations.NationService
import java.util.*

class PlayerLoader(
    private val service: NationService
) {

    fun onLogin(uuid: UUID){
        service.memberRegistry.local.load(uuid).thenApply { it.cache.nation }.join()
    }

    fun onLeft(uuid: UUID){
        service.memberRegistry.local.unload(uuid)
    }
}