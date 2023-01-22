package com.minepalm.nations.core.network

import com.minepalm.nations.NationEventBus

class EventBusGateway(
    val eventBus: NationEventBus,
    val network: com.minepalm.nations.server.NationNetwork
) {

    fun handle(event: com.minepalm.nations.event.NationEvent) {
        if (event !is com.minepalm.nations.event.Cancellable || !event.cancelled)
            eventBus.invoke(event)

    }
}