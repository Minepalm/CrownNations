package kr.rendog.nations.bukkit

import kr.rendog.nations.NationService
import java.util.UUID

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