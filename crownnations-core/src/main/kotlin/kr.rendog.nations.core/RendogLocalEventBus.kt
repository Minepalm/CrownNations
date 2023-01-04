package kr.rendog.nations.core

import kr.rendog.nations.event.NationEvent
import kr.rendog.nations.event.SendingEvent
import kr.rendog.nations.server.NationNetwork

class RendogLocalEventBus(
    private val network: NationNetwork
): RendogEventBus() {

}