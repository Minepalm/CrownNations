package kr.rendog.nations.core.mysql

import com.minepalm.library.database.impl.internal.MySQLDB
import kr.rendog.nations.war.WarTime
import java.util.concurrent.CompletableFuture

class MySQLWarSessionTimeDatabase(
    val database: MySQLDB,
    val table: String
) {

    init{
        database.execute { connection ->
            connection.prepareStatement("CREATE TABLE IF NOT EXISTS $table (" +
                    "`match_id` INT, " +
                    "`time_begin` BIGINT, " +
                    "`time_prepare` BIGINT, " +
                    "`time_end` BIGINT," +
                    "FOREIGN KEY (`match_id`) REFERENCES `crownnations_matches`(`match_id`), " +
                    "PRIMARY KEY (`match_id`)" +
                    ") charset=utf8mb4")
                .execute()
        }
    }

    fun insert(gameId: Int, time: WarTime): CompletableFuture<Unit> {
        return database.executeAsync<Unit> { connection ->
            connection.prepareStatement("INSERT INTO $table (`match_id`, `time_begin`, `time_prepare`, `time_end`) " +
                    "VALUES(?, ?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE " +
                    "`time_begin`=VALUES(`time_begin`), " +
                    "`time_prepare`=VALUES(`time_prepare`), " +
                    "`time_end`=VALUES(`time_end`)")
                .apply {
                    setInt(1, gameId)
                    setLong(2, time.timeBegin)
                    setLong(3, time.timePrepare)
                    setLong(4, time.timeEnd)
                }.execute()
        }
    }

    fun select(gameId: Int): CompletableFuture<WarTime?> {
        return database.executeAsync<WarTime?> { connection ->
            connection.prepareStatement("SELECT `time_begin`, `time_prepare`, `time_end` FROM $table " +
                    "WHERE `match_id`=?")
                .apply { setInt(1, gameId) }
                .executeQuery()
                .let {
                    if(it.next()){
                        WarTime(it.getLong(1), it.getLong(2), it.getLong(3))
                    }else
                        null
                }
        }
    }
}