package com.minepalm.nations.core.listener

import com.minepalm.nations.NationEventListener
import com.minepalm.nations.NationService
import java.util.concurrent.ExecutorService

class SyncDisband(
    val service: NationService,
    val executor: ExecutorService
) : NationEventListener<com.minepalm.nations.event.NationDisbandEvent> {

    override fun onEvent(event: com.minepalm.nations.event.NationDisbandEvent) {
        executor.execute {
            service.nationRegistry.forceInvalidate(event.nationId)
            service.territoryService.universe.host.deleteInvalidateMonuments()
        }
    }
}