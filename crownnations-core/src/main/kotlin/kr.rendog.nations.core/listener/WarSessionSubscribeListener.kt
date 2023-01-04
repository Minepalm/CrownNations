package kr.rendog.nations.core.listener

import kr.rendog.nations.NationEventListener
import kr.rendog.nations.event.WarEndEvent
import kr.rendog.nations.event.WarPostDeclarationEvent
import kr.rendog.nations.event.WarTimeoutEvent
import kr.rendog.nations.war.NationWarService

sealed class WarSessionSubscribeListener {

    class PostWar(private val service: NationWarService) : NationEventListener<WarPostDeclarationEvent> {
        override fun onEvent(event: WarPostDeclarationEvent) {
            service.sessionRegistry.run { direct[event.sessionData.gameId].thenApply { it?.let { local.add(it) } } }
            service.timer.subscribe(event.sessionData.gameId, event.time)
        }

    }

    class WarEnd(private val service: NationWarService) : NationEventListener<WarEndEvent> {
        override fun onEvent(event: WarEndEvent) {
            service.sessionRegistry.run { local.remove(event.matchId) }
            service.objectiveRegistry.resetObjectives(event.result.winner)
            service.objectiveRegistry.resetObjectives(event.result.loser)
            service.timer.unsubscribe(event.matchId)
        }
    }

    class WarTimeout(private val service: NationWarService) : NationEventListener<WarTimeoutEvent> {
        override fun onEvent(event: WarTimeoutEvent) {
            service.sessionRegistry.local.remove(event.matchId)
        }

    }
}