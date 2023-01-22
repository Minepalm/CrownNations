package com.minepalm.nations.core.listener

import com.minepalm.nations.NationEventListener
import com.minepalm.nations.NationService
import java.util.concurrent.ExecutorService

class SyncUpdate(
    private val service: NationService,
    val executor: ExecutorService
) : NationEventListener<com.minepalm.nations.event.NationUpdateEvent> {

    override fun onEvent(event: com.minepalm.nations.event.NationUpdateEvent) {
        executor.execute {
            service.nationRegistry.direct.getNation(event.nationId).thenApply { it?.cache?.update() }
        }
    }
}