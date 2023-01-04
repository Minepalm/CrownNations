package kr.rendog.nations.core.war

import com.google.gson.JsonObject
import kr.rendog.nations.Nation
import kr.rendog.nations.core.mysql.MySQLWarObjectiveDatabase
import kr.rendog.nations.territory.NationMonument
import kr.rendog.nations.war.NationWarService
import kr.rendog.nations.war.ObjectiveRecorder
import kr.rendog.nations.war.WarObjective
import kr.rendog.nations.war.WarSession
import java.util.*

class ObjectiveRecorderImpl(
    private val sessionId: Int,
    private val nationId: Int,
    private val service: NationWarService,
    private val database: MySQLWarObjectiveDatabase
) : ObjectiveRecorder {

    override val nation: Nation
        get() = service.root.nationRegistry[nationId] ?: throw IllegalStateException("nation: $nationId is not exist")

    override val session: WarSession
        get() = service.sessionRegistry[sessionId] ?: throw IllegalStateException("session: $sessionId is not exist")


    override fun recordPlayerKill(killer: UUID, victim: UUID) {
        database.insert(WarObjective(sessionId, nationId, WarObjective.Type.PLAYER_KILL, killer,
            data =JsonObject().apply { addProperty("victim", victim.toString()) }.toString()))
    }

    override fun recordCastleDestroy(destroyer: UUID, destroyed: NationMonument) {
        database.insert(WarObjective(sessionId, nationId, WarObjective.Type.CASTLE_FALLEN, destroyer,
            data =JsonObject().apply { addProperty("monumentId", destroyed.id) }.toString()))
    }

    override fun recordNationFallen() {
        database.insert(WarObjective(sessionId, nationId, WarObjective.Type.NATION_FALLEN, UUID(0, 0),
            data =""))
    }

}