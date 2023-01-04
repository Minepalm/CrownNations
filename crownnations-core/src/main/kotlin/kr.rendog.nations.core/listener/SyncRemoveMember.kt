package kr.rendog.nations.core.listener

import kr.rendog.nations.NationEventListener
import kr.rendog.nations.NationService
import kr.rendog.nations.event.NationRemoveMemberEvent
import java.util.concurrent.ExecutorService

class SyncRemoveMember(
    val service: NationService,
    val executor: ExecutorService
) : NationEventListener<NationRemoveMemberEvent> {
    override fun onEvent(event: NationRemoveMemberEvent) {
        executor.execute {
            service.nationRegistry[event.nationId]?.cache?.update(event.removerId)
            service.memberRegistry.update(event.removerId)
        }
    }

}