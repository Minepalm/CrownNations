package com.minepalm.nations.core.listener

import com.minepalm.nations.NationEventListener
import com.minepalm.nations.NationService
import java.util.concurrent.ExecutorService

class SyncCreate(
    private val service: NationService,
    val executor: ExecutorService
) : NationEventListener<com.minepalm.nations.event.NationCreateEvent> {

    override fun onEvent(event: com.minepalm.nations.event.NationCreateEvent) {
        //System.out.println("on event invoke: $event")
        executor.execute {
            service.nationRegistry.refresh(event.nationId)
            service.memberRegistry.update(event.founder)
        }
    }
}