package com.minepalm.nations.core.operation

import com.minepalm.nations.core.war.ELOFormula
import com.minepalm.nations.core.war.WarResultDeterminer
import com.minepalm.nations.war.WarResult
import com.minepalm.nations.war.WarSession
import com.minepalm.nations.war.WarStatus

class OperationWarEnd(
    val resultType: WarResult.Type,
    val session: WarSession,
    val formula: ELOFormula
) : AbstractNationOperation<WarResult>() {

    override fun checkOrThrow() {
        if (!session.isActive().join()) {
            fail("NOT_ACTIVE", "이미 비활성화된 세션입니다.")
        }

        val status = session.getStatus().join()

        if (!(status == WarStatus.IN_GAME || status == WarStatus.PREPARE)) {
            fail("NOT_IN_GAME", "게임 진행 중이 아닙니다.")
        }
    }

    override fun process0() {
        val determiner = WarResultDeterminer(session.home, session.away, formula)
        val result = determiner.determine(resultType, session.info, session.getObjectives().join())
        val complete = session.unsafe.endGame(result).join()
        if (!complete) {
            fail("ALREADY_INACTIVE", "이미 비활성화된 세션입니다.")
        }
    }
}