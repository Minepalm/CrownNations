package com.minepalm.nations.core.operation

import com.minepalm.nations.NationService
import com.minepalm.nations.ResultCode
import com.minepalm.nations.war.WarSession
import com.minepalm.nations.war.WarStatus
import com.minepalm.nations.war.WarTime

class OperationWarStart(
    private val service: NationService,
    private val session: WarSession
) : AbstractNationOperation<WarTime>() {

    // 1. 세션 이미 실행중 -> X
    // 2. 세션 비활성화 -> X
    override fun checkOrThrow() {
        if (session.isActive().join()) {
            fail("ALREADY_ACTIVE", "이미 활성화된 세션입니다.")
        }
        if (session.getStatus().join() != WarStatus.IDLE) {
            fail("ALREADY_IN_GAME", "이미 게임 진행 중입니다.")
        }
    }

    override fun process0() {
        val event = com.minepalm.nations.event.WarPreDeclarationEvent(session.info, false)
        service.localEventBus.invoke(event)
        if (event.cancelled) {
            fail(ResultCode.EVENT_CANCELLED, "")
        }

        val complete = session.unsafe.startGame().join()
        if (!complete) {
            fail("ALREADY_ACTIVE", "이미 활성화된 세션입니다.")
        }
    }
}