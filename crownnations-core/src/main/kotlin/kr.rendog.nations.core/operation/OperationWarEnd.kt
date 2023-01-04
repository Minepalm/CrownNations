package kr.rendog.nations.core.operation

import kr.rendog.nations.core.war.ELOFormula
import kr.rendog.nations.core.war.WarResultDeterminer
import kr.rendog.nations.war.WarResult
import kr.rendog.nations.war.WarSession
import kr.rendog.nations.war.WarStatus

class OperationWarEnd(
    val resultType: WarResult.Type,
    val session: WarSession,
    val formula: ELOFormula
) : AbstractNationOperation<WarResult>(){

    override fun checkOrThrow() {
        if( !session.isActive().join() ) {
            fail("NOT_ACTIVE", "이미 비활성화된 세션입니다.")
        }

        val status = session.getStatus().join()

        if( !(status == WarStatus.IN_GAME || status == WarStatus.PREPARE) ) {
            fail("NOT_IN_GAME", "게임 진행 중이 아닙니다.")
        }
    }

    override fun process0() {
        val determiner =  WarResultDeterminer(session.home, session.away, formula)
        val result = determiner.determine(resultType, session.info, session.getObjectives().join())
        val complete = session.unsafe.endGame(result).join()
        if(!complete){
            fail("ALREADY_INACTIVE", "이미 비활성화된 세션입니다.")
        }
    }
}