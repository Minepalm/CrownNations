package com.minepalm.nations.war

import com.minepalm.nations.Nation
import java.util.*

interface ObjectiveRecorder {

    val session: WarSession
    val nation: Nation

    fun recordPlayerKill(killer: UUID, victim: UUID)

    fun recordCastleDestroy(destroyer: UUID, destroyed: com.minepalm.nations.territory.NationMonument)

    fun recordNationFallen()
}