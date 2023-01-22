package com.minepalm.nations.core.listener

import com.minepalm.nations.NationEventListener
import com.minepalm.nations.NationService

class WarMonumentDestroyListener(
    val service: NationService
) : NationEventListener<com.minepalm.nations.event.WarMonumentDestroyEvent> {

    override fun onEvent(event: com.minepalm.nations.event.WarMonumentDestroyEvent) {
        val monument = service.territoryService.universe.host[event.monumentId]
        monument?.owner?.war?.isInWar()?.thenApply { isInWar ->
            if (isInWar) {
                service.warService.objectiveRegistry.resetObjective(monument.id)
                monument.owner.war.getCurrentMatch().thenApply { match ->
                    match?.getRecorder(monument.owner)?.recordCastleDestroy(event.destroyer, monument)
                }
            }
        }
    }

}