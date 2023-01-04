package kr.rendog.nations.core.listener

import kr.rendog.nations.NationEventListener
import kr.rendog.nations.NationService
import kr.rendog.nations.event.NationMetadataChangeEvent
import kr.rendog.nations.event.NationSetRankEvent
import java.util.concurrent.ExecutorService

class SyncMetadataChange(
    val service: NationService,
    val executor: ExecutorService
) : NationEventListener<NationMetadataChangeEvent> {

    override fun onEvent(event: NationMetadataChangeEvent) {
        service.nationRegistry[event.nationId]?.also { it.metadata.sync(event.key) }
    }
}