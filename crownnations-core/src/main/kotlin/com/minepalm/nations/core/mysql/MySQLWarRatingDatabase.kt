package com.minepalm.nations.core.mysql

import com.minepalm.library.database.impl.internal.MySQLDB
import java.sql.Connection
import java.util.concurrent.CompletableFuture

class MySQLWarRatingDatabase(
    val database: MySQLDB,
    val table: String,
    val defaultRating: Int
) {

    init {
        database.execute { connection ->
            connection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS $table (" +
                        "`nation_id` INT, " +
                        "`rating` INT DEFAULT 1000, " +
                        "PRIMARY KEY (`nation_id`), " +
                        "FOREIGN KEY (`nation_id`) REFERENCES `crownnations_ids`(`nation_id`) ON DELETE CASCADE" +
                        ")" +
                        "charset=utf8mb4"
            )
                .apply {
                    setString(1, "NONE")
                }
                .execute()
        }
    }

    fun getRating(nationId: Int): CompletableFuture<Int> {
        return database.executeAsync<Int> { connection ->
            getRating0(connection, nationId)
        }
    }

    private fun getRating0(connection: Connection, nationId: Int): Int {
        return connection.prepareStatement("SELECT `rating` FROM $table WHERE `nation_id`=?")
            .apply { setInt(1, nationId) }
            .executeQuery()
            .let {
                if (it.next())
                    it.getInt(1)
                else
                    defaultRating
            }
    }

    fun setRating(nationId: Int, rating: Int): CompletableFuture<Int> {
        return database.executeAsync<Int> { connection ->
            connection.prepareStatement(
                "INSERT INTO $table (`nation_id`, `rating`) VALUES(?, ?) " +
                        "ON DUPLICATE KEY UPDATE `rating`=VALUES(`rating`)"
            )
                .apply {
                    setInt(1, nationId)
                    setInt(2, rating)
                }.execute()
            getRating0(connection, nationId)
        }
    }

    fun addRating(nationId: Int, rating: Int): CompletableFuture<Int> {
        return database.executeAsync<Int> { connection ->
            connection.prepareStatement("UPDATE $table SET `rating`=`rating` + ? WHERE `nation_id`=?")
                .apply {
                    setInt(1, rating)
                    setInt(2, nationId)
                }.execute()
            getRating0(connection, nationId)
        }
    }

    fun subtractRating(nationId: Int, rating: Int): CompletableFuture<Int> {
        return database.executeAsync<Int> { connection ->
            connection.prepareStatement("UPDATE $table SET `rating`=`rating` - ? WHERE `nation_id`=?")
                .apply {
                    setInt(1, rating)
                    setInt(2, nationId)
                }.execute()
            getRating0(connection, nationId)
        }
    }


}