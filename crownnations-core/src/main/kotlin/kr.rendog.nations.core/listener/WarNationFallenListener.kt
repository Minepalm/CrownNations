package kr.rendog.nations.core.listener

import kr.rendog.nations.NationEventListener
import kr.rendog.nations.NationService
import kr.rendog.nations.event.WarNationFallenEvent
import kr.rendog.nations.war.WarResult

class WarNationFallenListener(val service: NationService) : NationEventListener<WarNationFallenEvent> {

    //todo: operateEnd 작업 쓰레드 할당해주기
    override fun onEvent(event: WarNationFallenEvent) {
        service.warService.sessionRegistry.direct.getMatch(event.fallenId).thenApplyAsync { session ->
            session?.operateEnd(WarResult.Type.FALLEN)?.process()
        }
    }

}