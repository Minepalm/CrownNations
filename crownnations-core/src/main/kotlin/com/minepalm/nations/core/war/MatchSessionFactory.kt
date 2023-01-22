package com.minepalm.nations.core.war

import com.minepalm.nations.war.SessionData
import com.minepalm.nations.war.WarSession

class MatchSessionFactory(
    private val service: PalmNationWarService
) {

    fun build(data: SessionData): WarSession {
        return PalmWarSession(data.gameId, data.info, service)
    }
}