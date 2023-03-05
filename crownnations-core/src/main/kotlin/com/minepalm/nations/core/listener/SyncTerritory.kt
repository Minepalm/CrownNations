package com.minepalm.nations.core.listener

import com.minepalm.nations.NationEventListener
import com.minepalm.nations.NationService
import com.minepalm.nations.event.TerritoryPostClaimEvent

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
    ) : NationEventListener<com.minepalm.nations.event.TerritoryDecomposeEvent> {
        override fun onEvent(event: com.minepalm.nations.event.TerritoryDecomposeEvent) {
            root.territoryService.universe.update(event.location, event.monumentId)
        }
    }

}