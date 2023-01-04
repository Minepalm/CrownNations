package kr.rendog.nations.core.network

import kr.rendog.nations.NationEventBus
import kr.rendog.nations.event.Cancellable
import kr.rendog.nations.event.NationEvent
import kr.rendog.nations.server.NationNetwork

class EventBusGateway(
    val eventBus : NationEventBus,
    val network : NationNetwork
) {

    fun handle(event : NationEvent){
        if(event !is Cancellable || !event.cancelled)
            eventBus.invoke(event)

    }
}