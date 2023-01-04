package kr.rendog.nations.core.war

import kr.rendog.nations.core.mysql.MySQLWarRatingDatabase
import kr.rendog.nations.war.NationRating
import kr.rendog.nations.war.NationRatingRegistry
import java.util.concurrent.ConcurrentHashMap

class RendogNationRatingRegistry(
    private val database: MySQLWarRatingDatabase
) : NationRatingRegistry {

    private val map = ConcurrentHashMap<Int, NationRating>()

    override fun get(nationId: Int): NationRating {
        return map[nationId] ?: build(nationId).apply { map[nationId] = this }
    }

    private fun build(nationId: Int): NationRating {
        return RendogNationRating(nationId, database)
    }
}