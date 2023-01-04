package kr.rendog.nations.core.listener

import kr.rendog.nations.NationEventListener
import kr.rendog.nations.NationService
import kr.rendog.nations.event.NationCreateEvent
import java.util.concurrent.ExecutorService

class SyncCreate(
    private val service: NationService,
    val executor: ExecutorService
) : NationEventListener<NationCreateEvent> {

    override fun onEvent(event: NationCreateEvent) {
        //System.out.println("on event invoke: $event")
        executor.execute {
            service.nationRegistry.refresh(event.nationId)
            service.memberRegistry.update(event.founder)
        }
    }
}