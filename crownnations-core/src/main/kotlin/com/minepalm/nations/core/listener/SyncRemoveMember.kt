package com.minepalm.nations.core.listener

import com.minepalm.nations.NationEventListener
import com.minepalm.nations.NationService
import com.minepalm.nations.event.NationRemoveMemberEvent
import java.util.concurrent.ExecutorService

class SyncRemoveMember(
    val service: NationService,
    val executor: ExecutorService
) : NationEventListener<NationRemoveMemberEvent> {
    override fun onEvent(event: NationRemoveMemberEvent) {
        executor.execute {
            service.nationRegistry[event.nationId]?.cache?.update(event.removerId)
                ?: service.nationRegistry.load(event.nationId)
            service.memberRegistry.update(event.removerId)
        }
    }

}