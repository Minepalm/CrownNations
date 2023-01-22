package com.minepalm.nations.core.listener

import com.minepalm.nations.NationEventListener
import com.minepalm.nations.NationService
import java.util.concurrent.ExecutorService

class SyncMetadataChange(
    val service: NationService,
    val executor: ExecutorService
) : NationEventListener<com.minepalm.nations.event.NationMetadataChangeEvent> {

    override fun onEvent(event: com.minepalm.nations.event.NationMetadataChangeEvent) {
        service.nationRegistry[event.nationId]?.also { it.metadata.sync(event.key) }
    }
}