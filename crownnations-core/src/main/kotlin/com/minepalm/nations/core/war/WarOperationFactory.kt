package com.minepalm.nations.core.war

import com.minepalm.nations.Nation
import com.minepalm.nations.NationMember
import com.minepalm.nations.NationOperation
import com.minepalm.nations.core.operation.OperationWarDeclare
import com.minepalm.nations.core.operation.OperationWarEnd
import com.minepalm.nations.core.operation.OperationWarStart
import com.minepalm.nations.core.operation.OperationWarSurrender
import com.minepalm.nations.war.*

class WarOperationFactory(
    val service: NationWarService,
    val eloFormula: ELOFormula
) {

    fun buildWarDeclaration(
        commander: NationMember,
        info: WarInfo
    ): NationOperation<WarSession> {
        return OperationWarDeclare(commander, info, service.root)
    }

    fun buildWarSurrender(
        commander: NationMember,
        nation: Nation
    ): NationOperation<WarResult> {
        return OperationWarSurrender(commander, nation, service.root)
    }

    fun buildGameStart(session: WarSession): NationOperation<WarTime> {
        return OperationWarStart(service.root, session)
    }

    fun buildGameEnd(
        session: WarSession,
        resultType: WarResult.Type
    ): NationOperation<WarResult> {
        return OperationWarEnd(resultType, session, eloFormula)
    }
}