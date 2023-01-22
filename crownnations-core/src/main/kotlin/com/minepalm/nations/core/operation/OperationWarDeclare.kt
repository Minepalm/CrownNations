package com.minepalm.nations.core.operation

import com.minepalm.nations.*
import com.minepalm.nations.war.WarSession

class OperationWarDeclare(
    private val commander: NationMember,
    private val warInfo: com.minepalm.nations.war.WarInfo,
    private val service: NationService
) : AbstractNationOperation<WarSession>() {


    private val home: Nation = service.nationRegistry[warInfo.homeNation]!!
    private val away: Nation = service.nationRegistry[warInfo.awayNation]!!

    /**
     * 1. home nation = commander nation
     * 2. commander = nation owner
     * 3. home has shield
     * 4. away has shield
     * 5. home != in war
     * 6. away != in war
     * 7. MMR in range ( ignored )
     */
    override fun checkOrThrow() {
        if (!commander.cache.isAdmin()) {
            val commanderNation = commander.direct.getNation().join()

            if (commanderNation == null) {
                fail(ResultCode.NO_NATION, "국가에 소속되어 있지 않습니다.")
                return
            }

            if (commanderNation.id != home.id) {
                fail(ResultCode.NATION_MISMATCH, "해당 국가에 소속되어 있지 않습니다.")
            }

            if (!home.cache.getRank(commander.uniqueId).hasPermissibleOf(NationRank.OWNER)) {
                fail(ResultCode.NO_PERMISSION, "그 국가의 왕이 아닙니다.")
            }

            val isForced = home.war.shield.isForced()
            val homeHasShield = home.war.shield.hasShield()
            val awayHasShield = away.war.shield.hasShield()

            if (homeHasShield.join() && isForced.join()) {
                fail(ResultCode.HOME_FORCED_SHIELD, "현재 국가는 전쟁이 비활성화 되어 있어 전쟁을 걸수 없습니다.")
            }

            if (awayHasShield.join()) {
                fail(ResultCode.AWAY_HAS_SHIELD, "상대 국가는 전쟁 보호가 걸려 있습니다. 전쟁을 걸수 없습니다.")
            }
        }

        val homeInWar = home.war.isInWar()
        val awayInWar = away.war.isInWar()

        if (homeInWar.join()) {
            fail(ResultCode.HOME_ALREADY_IN_WAR, "현재 이미 다른 국가와 전쟁중입니다.")
        }

        if (awayInWar.join()) {
            fail(ResultCode.HOME_ALREADY_IN_WAR, "이미 해당 국가는 다른 국가와 전쟁중입니다.")
        }
    }

    override fun process0() {
        val session = service.warService.matchMaker.createNewMatch(warInfo).join()

        val homeSession = home.war.getCurrentMatch()
        val awaySession = away.war.getCurrentMatch()

        if (homeSession.join()?.gameId != session.gameId) {
            session.unsafe.invalidate().join()
            fail(ResultCode.HOME_ALREADY_IN_WAR, "현재 이미 다른 국가와 전쟁중입니다.")
        }

        if (awaySession.join()?.gameId != session.gameId) {
            session.unsafe.invalidate().join()
            fail(ResultCode.HOME_ALREADY_IN_WAR, "이미 해당 국가는 다른 국가와 전쟁중입니다.")
        }

        session.operateStart().process()

        success(ResultCode.SUCCESSFUL, session)
    }
}