package com.minepalm.nations.core.listener

import com.minepalm.nations.NationEventListener
import com.minepalm.nations.NationService

class WarNationFallenListener(val service: NationService) :
    NationEventListener<com.minepalm.nations.event.WarNationFallenEvent> {

    //todo: operateEnd 작업 쓰레드 할당해주기
    override fun onEvent(event: com.minepalm.nations.event.WarNationFallenEvent) {
        service.warService.sessionRegistry.direct.getMatch(event.fallenId).thenApplyAsync { session ->
            session?.operateEnd(com.minepalm.nations.war.WarResult.Type.FALLEN)?.process()
        }
    }

}