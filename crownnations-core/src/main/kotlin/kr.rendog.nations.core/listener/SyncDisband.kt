package kr.rendog.nations.core.listener

import kr.rendog.nations.NationEventListener
import kr.rendog.nations.NationService
import kr.rendog.nations.event.NationDisbandEvent
import java.util.concurrent.ExecutorService

class SyncDisband(
    val service: NationService,
    val executor: ExecutorService
) : NationEventListener<NationDisbandEvent> {

    override fun onEvent(event: NationDisbandEvent) {
        executor.execute {
            service.nationRegistry.forceInvalidate(event.nationId)
            service.territoryService.universe.host.deleteInvalidateMonuments()
        }
    }
}