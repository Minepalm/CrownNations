package com.minepalm.nations.core.war

import com.minepalm.nations.core.mysql.MySQLWarRatingDatabase
import java.util.concurrent.ConcurrentHashMap

class PalmNationRatingRegistry(
    private val database: MySQLWarRatingDatabase
) : com.minepalm.nations.war.NationRatingRegistry {

    private val map = ConcurrentHashMap<Int, com.minepalm.nations.war.NationRating>()

    override fun get(nationId: Int): com.minepalm.nations.war.NationRating {
        return map[nationId] ?: build(nationId).apply { map[nationId] = this }
    }

    private fun build(nationId: Int): com.minepalm.nations.war.NationRating {
        return PalmNationRating(nationId, database)
    }
}