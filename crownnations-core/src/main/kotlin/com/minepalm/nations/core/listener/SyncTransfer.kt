package com.minepalm.nations.core.listener

import com.minepalm.nations.NationEventListener
import com.minepalm.nations.NationService
import java.util.concurrent.ExecutorService

class SyncTransfer(
    val service: NationService,
    val executor: ExecutorService
) : NationEventListener<com.minepalm.nations.event.NationTransferEvent> {

    override fun onEvent(event: com.minepalm.nations.event.NationTransferEvent) {
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