package kr.rendog.nations.core.mysql

import com.minepalm.library.database.impl.internal.MySQLDB
import java.sql.Connection
import java.util.concurrent.CompletableFuture

class MySQLWarShieldDatabase(
    val database: MySQLDB,
    val table: String
) {

    init{
        database.execute { connection ->
            connection.prepareStatement("CREATE TABLE IF NOT EXISTS $table (" +
                    "`nation_id` INT, " +
                    "`time` BIGINT DEFAULT 0, " +
                    "PRIMARY KEY (`nation_id`), " +
                    "FOREIGN KEY (`nation_id`) REFERENCES `crownnations_ids`(`nation_id`) ON DELETE CASCADE" +
                    ")" +
                    "charset=utf8mb4")
                .apply {
                    setString(1, "NONE")
                }
                .execute()
        }
    }

    fun getTime(nationId: Int): CompletableFuture<Long> {
        return database.executeAsync<Long> { connection ->
            getTime0(connection, nationId)
        }
    }

    private fun getTime0(connection: Connection, nationId: Int): Long{
        return connection.prepareStatement("SELECT `time` FROM $table WHERE `nation_id`=?")
            .apply { setInt(1, nationId) }
            .executeQuery()
            .let {
                if(it.next())
                    it.getLong(1)
                else
                    0L
            }
    }

    fun setTime(nationId: Int, time: Long): CompletableFuture<Long> {
        return database.executeAsync<Long> { connection ->
            connection.prepareStatement("INSERT INTO $table (`nation_id`, `time`) VALUES(?, ?) " +
                    "ON DUPLICATE KEY UPDATE `time`=VALUES(`time`)")
                .apply {
                    setInt(1, nationId)
                    setLong(2, time)
                }.execute()
            getTime0(connection, nationId)
        }
    }

    fun addTime(nationId: Int, duration: Long): CompletableFuture<Long> {
        return database.executeAsync<Long> { connection ->
            connection.prepareStatement("UPDATE $table SET `time`=`time` + ? WHERE `nation_id`=?")
                .apply {
                    setLong(1, duration)
                    setInt(2, nationId)
                }.execute()
            getTime0(connection, nationId)
        }
    }

    fun subtractTime(nationId: Int, time: Long): CompletableFuture<Long> {
        return database.executeAsync<Long> { connection ->
            connection.prepareStatement("UPDATE $table SET `time`=`time` - ? WHERE `nation_id`=?")
                .apply {
                    setLong(1, time)
                    setInt(2, nationId)
                }.execute()
            getTime0(connection, nationId)
        }
    }


}