package com.minepalm.nations.core.listener

import com.minepalm.nations.NationEventListener

sealed class WarSessionSubscribeListener {

    class PostWar(private val service: com.minepalm.nations.war.NationWarService) :
        NationEventListener<com.minepalm.nations.event.WarPostDeclarationEvent> {
        override fun onEvent(event: com.minepalm.nations.event.WarPostDeclarationEvent) {
            service.sessionRegistry.run { direct[event.sessionData.gameId].thenApply { it?.let { local.add(it) } } }
            service.timer.subscribe(event.sessionData.gameId, event.time)
        }

    }

    class WarEnd(private val service: com.minepalm.nations.war.NationWarService) :
        NationEventListener<com.minepalm.nations.event.WarEndEvent> {
        override fun onEvent(event: com.minepalm.nations.event.WarEndEvent) {
            service.sessionRegistry.run { local.remove(event.matchId) }
            service.objectiveRegistry.resetObjectives(event.result.winner)
            service.objectiveRegistry.resetObjectives(event.result.loser)
            service.timer.unsubscribe(event.matchId)
        }
    }

    class WarTimeout(private val service: com.minepalm.nations.war.NationWarService) :
        NationEventListener<com.minepalm.nations.event.WarTimeoutEvent> {
        override fun onEvent(event: com.minepalm.nations.event.WarTimeoutEvent) {
            service.sessionRegistry.local.remove(event.matchId)
        }

    }
}