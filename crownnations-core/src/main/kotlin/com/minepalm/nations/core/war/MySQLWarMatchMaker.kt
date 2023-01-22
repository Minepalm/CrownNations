package com.minepalm.nations.core.war

import com.minepalm.nations.Nation
import com.minepalm.nations.NationMember
import com.minepalm.nations.NationOperation
import com.minepalm.nations.core.mysql.MySQLWarSessionDatabase
import com.minepalm.nations.war.*
import java.util.concurrent.CompletableFuture

class MySQLWarMatchMaker(
    private val sessionFactory: MatchSessionFactory,
    private val factory: WarOperationFactory,
    private val sessionDatabase: MySQLWarSessionDatabase
) : NationMatchMaker {

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