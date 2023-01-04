package kr.rendog.nations.core.war

import kr.rendog.nations.core.mysql.MySQLWarShieldDatabase
import kr.rendog.nations.core.mysql.MySQLWarShieldForcedDatabase
import kr.rendog.nations.war.NationShield
import kr.rendog.nations.war.NationShieldRegistry
import kr.rendog.nations.war.NationWarService
import java.util.concurrent.ConcurrentHashMap

class RendogNationShieldRegistry(
    private val warService: NationWarService,
    private val timeDatabase: MySQLWarShieldDatabase,
    private val forcedDatabase: MySQLWarShieldForcedDatabase
): NationShieldRegistry {

    private val map = ConcurrentHashMap<Int, NationShield>()

    override fun get(nationId: Int): NationShield {
        return map[nationId] ?: build(nationId).apply { map[nationId] = this }
    }

    private fun build(nationId: Int): NationShield {
        return RendogNationShield(nationId, timeDatabase, forcedDatabase, warService)
    }
}