package kr.rendog.nations.core.war

import kr.rendog.nations.Nation
import kr.rendog.nations.NationMember
import kr.rendog.nations.NationOperation
import kr.rendog.nations.core.operation.OperationWarDeclare
import kr.rendog.nations.core.operation.OperationWarEnd
import kr.rendog.nations.core.operation.OperationWarStart
import kr.rendog.nations.core.operation.OperationWarSurrender
import kr.rendog.nations.war.*

class WarOperationFactory(
    val service: NationWarService,
    val eloFormula: ELOFormula
) {

    fun buildWarDeclaration(commander: NationMember, info: WarInfo): NationOperation<WarSession> {
        return OperationWarDeclare(commander, info, service.root)
    }

    fun buildWarSurrender(commander: NationMember, nation: Nation): NationOperation<WarResult> {
        return OperationWarSurrender(commander, nation, service.root)
    }

    fun buildGameStart(session: WarSession): NationOperation<WarTime>{
        return OperationWarStart(service.root, session)
    }

    fun buildGameEnd(session: WarSession, resultType: WarResult.Type): NationOperation<WarResult>{
        return OperationWarEnd(resultType, session, eloFormula)
    }
}