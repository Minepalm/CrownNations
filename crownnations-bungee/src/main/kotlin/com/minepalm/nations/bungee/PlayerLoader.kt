package com.minepalm.nations.bungee

import com.minepalm.nations.NationService
import java.util.*

class PlayerLoader(
    private val service: NationService
) {

    fun onLogin(uuid: UUID){
        service.memberRegistry.local.load(uuid).thenApply { it.cache.getNation() }.join()
    }

    fun onLeft(uuid: UUID){
        service.memberRegistry.local.unload(uuid)
    }

}