package com.minepalm.nations.core.territory.listener

import com.minepalm.nations.NationEventListener
import com.minepalm.nations.event.NationDisbandEvent
import com.minepalm.nations.territory.NationTerritoryService

class NationDisbandListener(
    val service: NationTerritoryService
) : NationEventListener<NationDisbandEvent> {
    override fun onEvent(event: NationDisbandEvent) {
        service.territoryRegistry[event.nationId]?.let {
            it.local.getMonuments().forEach { monument ->
                monument.collapse()
            }
        }
    }
}