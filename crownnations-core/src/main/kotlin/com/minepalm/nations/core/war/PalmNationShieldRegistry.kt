package com.minepalm.nations.core.war

import com.minepalm.nations.core.mysql.MySQLWarShieldDatabase
import com.minepalm.nations.core.mysql.MySQLWarShieldForcedDatabase
import java.util.concurrent.ConcurrentHashMap

class PalmNationShieldRegistry(
    private val warService: com.minepalm.nations.war.NationWarService,
    private val timeDatabase: MySQLWarShieldDatabase,
    private val forcedDatabase: MySQLWarShieldForcedDatabase
) : com.minepalm.nations.war.NationShieldRegistry {

    private val map = ConcurrentHashMap<Int, com.minepalm.nations.war.NationShield>()

    override fun get(nationId: Int): com.minepalm.nations.war.NationShield {
        return map[nationId] ?: build(nationId).apply { map[nationId] = this }
    }

    private fun build(nationId: Int): com.minepalm.nations.war.NationShield {
        return PalmNationShield(nationId, timeDatabase, forcedDatabase, warService)
    }
}