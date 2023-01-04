package kr.rendog.nations.core.listener

import kr.rendog.nations.NationEventListener
import kr.rendog.nations.config.WarConfiguration
import kr.rendog.nations.event.WarEndEvent
import kr.rendog.nations.event.WarPostDeclarationEvent
import kr.rendog.nations.event.WarTimeoutEvent
import kr.rendog.nations.war.NationWarService
import kr.rendog.nations.war.NationWarTimer
import kr.rendog.nations.war.WarResult

sealed class WarSessionHandleListener {

    // 1. timer -> 끝나면 war end
    class PostWar(private val service: NationWarService) : NationEventListener<WarPostDeclarationEvent> {

        override fun onEvent(event: WarPostDeclarationEvent) {
            if(service.config.isHandling(event.sender)) {
                service.timer.subscribe(event.sessionData.gameId, event.time)
            }
        }

    }

    class WarEnd(private val service: NationWarService) : NationEventListener<WarEndEvent> {

        override fun onEvent(event: WarEndEvent) {
            if (service.config.isHandling(event.sender)) {
                service.ratingRegistry[event.result.winner].addRating(event.result.ratingResult)
                service.ratingRegistry[event.result.loser].subtractRating(event.result.ratingResult)
            }
        }
    }

    class WarTimeout(private val service: NationWarService) : NationEventListener<WarTimeoutEvent> {

        override fun onEvent(event: WarTimeoutEvent) {
            service.sessionRegistry.direct[event.matchId].thenApply {
                it?.operateEnd(WarResult.Type.TIMEOUT)?.process()
            }
        }

    }
}