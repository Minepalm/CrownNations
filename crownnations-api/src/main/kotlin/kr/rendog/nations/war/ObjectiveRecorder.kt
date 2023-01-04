package kr.rendog.nations.war

import kr.rendog.nations.Nation
import kr.rendog.nations.territory.NationMonument
import java.util.*

interface ObjectiveRecorder {

    val session: WarSession
    val nation: Nation

    fun recordPlayerKill(killer: UUID, victim: UUID)

    fun recordCastleDestroy(destroyer: UUID, destroyed: NationMonument)

    fun recordNationFallen()
}