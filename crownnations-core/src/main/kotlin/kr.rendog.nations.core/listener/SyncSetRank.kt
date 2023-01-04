package kr.rendog.nations.core.listener

import kr.rendog.nations.NationEventListener
import kr.rendog.nations.NationService
import kr.rendog.nations.event.NationSetRankEvent
import java.util.concurrent.ExecutorService

class SyncSetRank(
    val service: NationService,
    val executor: ExecutorService
): NationEventListener<NationSetRankEvent> {

    override fun onEvent(event: NationSetRankEvent) {
        executor.execute {
            service.nationRegistry[event.nationId]?.cache?.update(event.user)
            service.memberRegistry.update(event.user)
        }
    }

}