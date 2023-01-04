package kr.rendog.nations.core.war

import kr.rendog.nations.Nation
import kr.rendog.nations.NationMember
import kr.rendog.nations.NationOperation
import kr.rendog.nations.core.mysql.MySQLWarSessionDatabase
import kr.rendog.nations.war.*
import java.util.concurrent.CompletableFuture

class MySQLWarMatchMaker(
    private val sessionFactory: MatchSessionFactory,
    private val factory: WarOperationFactory,
    private val sessionDatabase: MySQLWarSessionDatabase
): NationMatchMaker {

    override fun operateMatchMake(
        commander: NationMember,
        type: WarType,
        nation: Nation,
        other: Nation
    ): NationOperation<WarSession> {
        return factory.buildWarDeclaration(commander, WarInfo(type, nation.id, other.id))
    }

    override fun createNewMatch(info: WarInfo): CompletableFuture<WarSession> {
        return sessionDatabase.createNewSession(info)
            .thenApply { sessionId -> sessionFactory.build(SessionData(sessionId, info)) }
    }
}