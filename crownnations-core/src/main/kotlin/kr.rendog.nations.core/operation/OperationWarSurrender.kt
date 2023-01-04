package kr.rendog.nations.core.operation

import kr.rendog.nations.Nation
import kr.rendog.nations.NationMember
import kr.rendog.nations.NationService
import kr.rendog.nations.war.WarResult

class OperationWarSurrender(
    private val commander: NationMember,
    private val nation: Nation,
    private val service: NationService
) : AbstractNationOperation<WarResult>() {

    /**
     * TODO:
     *  1. home in war
     *  2. commander = owner
     *  3. status = prepare or in game
     *  4. -> end game
     */
    override fun checkOrThrow() {
        TODO("Not yet implemented")
    }

    override fun process0() {
        TODO("Not yet implemented")
    }
}