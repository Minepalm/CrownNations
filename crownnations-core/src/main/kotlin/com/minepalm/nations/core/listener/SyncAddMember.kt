package com.minepalm.nations.core.listener

import com.minepalm.nations.NationEventListener
import com.minepalm.nations.NationService
import java.util.concurrent.ExecutorService

class SyncAddMember(
    val service: NationService,
    val executor: ExecutorService
) : NationEventListener<com.minepalm.nations.event.NationAddMemberEvent> {
    override fun onEvent(event: com.minepalm.nations.event.NationAddMemberEvent) {
        executor.execute {
            service.nationRegistry[event.nationId]?.cache?.update(event.userId)
            service.memberRegistry.update(event.userId)
        }
    }

}