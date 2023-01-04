package kr.rendog.nations.core.listener

import kr.rendog.nations.NationEventListener
import kr.rendog.nations.NationService
import kr.rendog.nations.event.WarMonumentDestroyEvent

class WarMonumentDestroyListener(
    val service: NationService
) : NationEventListener<WarMonumentDestroyEvent> {

    override fun onEvent(event: WarMonumentDestroyEvent) {
        val monument = service.territoryService.universe.host[event.monumentId]
        monument?.owner?.war?.isInWar()?.thenApply { isInWar ->
            if(isInWar){
                service.warService.objectiveRegistry.resetObjective(monument.id)
                monument.owner.war.getCurrentMatch().thenApply { match ->
                    match?.getRecorder(monument.owner)?.recordCastleDestroy(event.destroyer, monument)
                }
            }
        }
    }

}