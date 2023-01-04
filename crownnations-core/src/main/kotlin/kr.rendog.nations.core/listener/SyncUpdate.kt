package kr.rendog.nations.core.listener

import kr.rendog.nations.NationEventListener
import kr.rendog.nations.NationService
import kr.rendog.nations.event.NationUpdateEvent
import java.util.concurrent.ExecutorService

class SyncUpdate(
    private val service: NationService,
    val executor: ExecutorService
) : NationEventListener<NationUpdateEvent> {

    override fun onEvent(event: NationUpdateEvent) {
        executor.execute {
            service.nationRegistry.direct.getNation(event.nationId).thenApply { it?.cache?.update() }
        }
    }
}