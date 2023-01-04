package kr.rendog.nations.core.listener

import kr.rendog.nations.NationEventListener
import kr.rendog.nations.NationService
import kr.rendog.nations.event.NationTransferEvent
import java.util.concurrent.ExecutorService

class SyncTransfer(
    val service: NationService,
    val executor: ExecutorService
): NationEventListener<NationTransferEvent> {

    override fun onEvent(event: NationTransferEvent) {
        executor.execute {
            service.nationRegistry[event.nationId]?.cache?.also {
                it.update(event.transferFrom)
                it.update(event.transferTo)
            }
            service.memberRegistry.also {
                it.update(event.transferFrom)
                it.update(event.transferTo)
            }
        }
    }
}