package com.minepalm.nations.core.listener

import com.minepalm.nations.NationEventListener
import com.minepalm.nations.NationService
import java.util.concurrent.ExecutorService

class SyncSetRank(
    val service: NationService,
    val executor: ExecutorService
) : NationEventListener<com.minepalm.nations.event.NationSetRankEvent> {

    override fun onEvent(event: com.minepalm.nations.event.NationSetRankEvent) {
        executor.execute {
            service.nationRegistry[event.nationId]?.cache?.update(event.user)
            service.memberRegistry.update(event.user)
        }
    }

}