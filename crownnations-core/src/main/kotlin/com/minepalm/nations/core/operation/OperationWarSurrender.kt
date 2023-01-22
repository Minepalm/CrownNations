package com.minepalm.nations.core.operation

import com.minepalm.nations.Nation
import com.minepalm.nations.NationMember
import com.minepalm.nations.NationService

class OperationWarSurrender(
    private val commander: NationMember,
    private val nation: Nation,
    private val service: NationService
) : AbstractNationOperation<com.minepalm.nations.war.WarResult>() {

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