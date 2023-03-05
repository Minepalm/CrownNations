package com.minepalm.nations.core.territory.hellobungee

import com.minepalm.library.network.api.PalmNetwork
import com.minepalm.nations.territory.NationTerritoryService

class HelloBungeeTerritory {

    fun initialize(network: PalmNetwork, territoryService: NationTerritoryService) {
        network.apply {
            gateway.registerAdapter(RequestMonumentSave.Adapter())
            gateway.registerAdapter(RequestMonumentLoad.Adapter())
            gateway.registerAdapter(RequestMonumentCollapse.Adapter())
            callbackService.registerTransformer(RequestMonumentCollapse.Callback(territoryService))
            callbackService.registerTransformer(RequestMonumentLoad.Callback(territoryService))
            callbackService.registerTransformer(RequestMonumentSave.Callback(territoryService))
        }
    }
}