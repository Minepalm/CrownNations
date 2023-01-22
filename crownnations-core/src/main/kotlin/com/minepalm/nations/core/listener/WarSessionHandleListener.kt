package com.minepalm.nations.core.listener

import com.minepalm.nations.NationEventListener

sealed class WarSessionHandleListener {

    // 1. timer -> 끝나면 war end
    class PostWar(private val service: com.minepalm.nations.war.NationWarService) :
        NationEventListener<com.minepalm.nations.event.WarPostDeclarationEvent> {

        override fun onEvent(event: com.minepalm.nations.event.WarPostDeclarationEvent) {
            if (service.config.isHandling(event.sender)) {
                service.timer.subscribe(event.sessionData.gameId, event.time)
            }
        }

    }

    class WarEnd(private val service: com.minepalm.nations.war.NationWarService) :
        NationEventListener<com.minepalm.nations.event.WarEndEvent> {

        override fun onEvent(event: com.minepalm.nations.event.WarEndEvent) {
            if (service.config.isHandling(event.sender)) {
                service.ratingRegistry[event.result.winner].addRating(event.result.ratingResult)
                service.ratingRegistry[event.result.loser].subtractRating(event.result.ratingResult)
            }
        }
    }

    class WarTimeout(private val service: com.minepalm.nations.war.NationWarService) :
        NationEventListener<com.minepalm.nations.event.WarTimeoutEvent> {

        override fun onEvent(event: com.minepalm.nations.event.WarTimeoutEvent) {
            service.sessionRegistry.direct[event.matchId].thenApply {
                it?.operateEnd(com.minepalm.nations.war.WarResult.Type.TIMEOUT)?.process()
            }
        }

    }
}