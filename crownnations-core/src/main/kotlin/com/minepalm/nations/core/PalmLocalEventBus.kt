package com.minepalm.nations.core

import com.minepalm.nations.event.NationEvent
import com.minepalm.nations.server.NationNetwork

class PalmLocalEventBus(
    private val network: NationNetwork
): PalmEventBus(){

}