package kr.rendog.nations.core.listener

import kr.rendog.nations.NationEventListener
import kr.rendog.nations.NationService
import kr.rendog.nations.event.TerritoryPreClaimEvent
import kr.rendog.nations.event.TerritoryDecomposeEvent
import kr.rendog.nations.event.TerritoryPostClaimEvent
import kr.rendog.nations.event.TerritoryWorldLoadEvent
import kr.rendog.nations.territory.NationTerritoryService

sealed class SyncTerritory {

    class Claim(
        private val root: NationService
    ) : NationEventListener<TerritoryPostClaimEvent> {
        override fun onEvent(event: TerritoryPostClaimEvent) {
            root.territoryService.universe.update(event.location, event.monumentId)
        }
    }

    class Decompose(
        private val root: NationService
    ) : NationEventListener<TerritoryDecomposeEvent> {
        override fun onEvent(event: TerritoryDecomposeEvent) {
            root.territoryService.universe.update(event.location, event.monumentId)
        }
    }

}