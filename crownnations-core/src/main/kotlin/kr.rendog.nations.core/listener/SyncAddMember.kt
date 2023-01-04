package kr.rendog.nations.core.listener

import kr.rendog.nations.NationEventListener
import kr.rendog.nations.NationService
import kr.rendog.nations.event.NationAddMemberEvent
import java.util.concurrent.ExecutorService

class SyncAddMember(
    val service: NationService,
    val executor: ExecutorService
) : NationEventListener<NationAddMemberEvent> {
    override fun onEvent(event: NationAddMemberEvent) {
        executor.execute {
            service.nationRegistry[event.nationId]?.cache?.update(event.userId)
            service.memberRegistry.update(event.userId)
        }
    }

}