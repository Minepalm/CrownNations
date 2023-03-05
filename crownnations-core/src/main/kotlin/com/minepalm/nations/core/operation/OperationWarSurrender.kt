package com.minepalm.nations.core.operation

import com.minepalm.nations.Nation
import com.minepalm.nations.NationMember
import com.minepalm.nations.NationService
import com.minepalm.nations.war.WarResult
import com.minepalm.nations.war.WarStatus
import java.util.concurrent.CompletableFuture

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
        val currentMatchFuture = nation.war.getCurrentMatch()
        if (currentMatchFuture.join() == null) {
            fail("NATION_NO_WAR", "현재 해당 국가는 전쟁중이지 않습니다.")
        }
        if (nation.direct.getOwner().join().uniqueId != commander.uniqueId) {
            fail("NATION_NOT_OWNER", "국가장만 명령어를 칠수 있습니다.")
        }

        val status = currentMatchFuture.thenCompose {
            it?.getStatus() ?: CompletableFuture.completedFuture(WarStatus.INVALID)
        }.join()

        when (status) {
            WarStatus.IN_GAME -> {}
            WarStatus.INVALID -> {}
            WarStatus.IDLE -> {}
            WarStatus.PREPARE -> {}
            WarStatus.END -> {}
        }
    }

    override fun process0() {
        TODO("Not yet implemented")
    }
}