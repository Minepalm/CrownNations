package kr.rendog.nations.core.grade

import com.minepalm.library.database.impl.internal.MySQLDB
import java.util.concurrent.CompletableFuture

class MySQLNationGradeDatabase(
    val database: MySQLDB,
    val table: String
) {

    init {
        database.execute { connection ->
            connection.prepareStatement("CREATE TABLE IF NOT EXISTS $table ( " +
                    "`nation_id` INT, " +
                    "`level` INT DEFAULT 1, " +
                    "PRIMARY KEY(`nation_id`), " +
                    "FOREIGN KEY (`nation_id`) REFERENCES `crownnations_ids` (`nation_id`) ON DELETE CASCADE" +
                    ") charset=utf8mb4")
                .execute()
        }
    }

    fun getGrade(nationId: Int): CompletableFuture<Int>{
        return database.executeAsync<Int> { connection ->
            connection.prepareStatement("SELECT `level` FROM $table WHERE `nation_id`=?")
                .apply { setInt(1, nationId) }
                .executeQuery()
                .let {
                    if(it.next())
                        it.getInt(1)
                    else
                        1
                }
        }!!
    }

    fun setGrade(nation: Int, level: Int): CompletableFuture<Unit>{
        return database.executeAsync<Unit> { connection ->
            connection.prepareStatement("INSERT INTO $table (`nation_id`, `level`) VALUES(?, ?) " +
                    "ON DUPLICATE KEY UPDATE `level`=VALUES(`level`)")
                .apply {
                    setInt(1, nation)
                    setInt(2, level)
                }.execute()
        }!!
    }
}