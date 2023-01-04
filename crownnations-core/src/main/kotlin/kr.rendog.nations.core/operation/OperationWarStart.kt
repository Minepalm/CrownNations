package kr.rendog.nations.core.operation

import kr.rendog.nations.NationService
import kr.rendog.nations.ResultCode
import kr.rendog.nations.event.WarPreDeclarationEvent
import kr.rendog.nations.war.WarSession
import kr.rendog.nations.war.WarStatus
import kr.rendog.nations.war.WarTime

class OperationWarStart(
    private val service: NationService,
    private val session: WarSession
): AbstractNationOperation<WarTime>() {

    // 1. 세션 이미 실행중 -> X
    // 2. 세션 비활성화 -> X
    override fun checkOrThrow() {
        if(session.isActive().join()){
            fail("ALREADY_ACTIVE", "이미 활성화된 세션입니다.")
        }
        if(session.getStatus().join() != WarStatus.IDLE){
            fail("ALREADY_IN_GAME", "이미 게임 진행 중입니다.")
        }
    }

    override fun process0() {
        val event = WarPreDeclarationEvent(session.info, false)
        service.localEventBus.invoke(event)
        if(event.cancelled){
            fail(ResultCode.EVENT_CANCELLED, "")
        }

        val complete = session.unsafe.startGame().join()
        if(!complete){
            fail("ALREADY_ACTIVE", "이미 활성화된 세션입니다.")
        }
    }
}