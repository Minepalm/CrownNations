package kr.rendog.nations.core.war

import kr.rendog.nations.war.NationWarService
import kr.rendog.nations.war.SessionData
import kr.rendog.nations.war.WarSession

class MatchSessionFactory(
    private val service: RendogNationWarService
) {

    fun build(data: SessionData): WarSession{
        return RendogWarSession(data.gameId, data.info, service)
    }
}