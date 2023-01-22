package com.minepalm.nations.core.listener

import com.minepalm.nations.NationEventListener
import com.minepalm.nations.NationService
import java.util.concurrent.ExecutorService

class SyncRemoveMember(
    val service: NationService,
    val executor: ExecutorService
) : NationEventListener<com.minepalm.nations.event.NationRemoveMemberEvent> {
    override fun onEvent(event: com.minepalm.nations.event.NationRemoveMemberEvent) {
        executor.execute {
            service.nationRegistry[event.nationId]?.cache?.update(event.removerId)
            service.memberRegistry.update(event.removerId)
        }
    }

}