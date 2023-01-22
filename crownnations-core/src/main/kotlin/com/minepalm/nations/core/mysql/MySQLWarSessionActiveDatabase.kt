package com.minepalm.nations.core.mysql

import com.minepalm.library.database.impl.internal.MySQLDB
import java.util.concurrent.CompletableFuture

class MySQLWarSessionActiveDatabase(
    val database: MySQLDB,
    val table: String,
) {

    init {
        database.execute { connection ->
            connection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS $table ( " +
                        "`match_id` INT, " +
                        "`active` BOOLEAN, " +
                        "PRIMARY KEY(`match_id`), " +
                        "FOREIGN KEY (`match_id`) REFERENCES `crownnations_matches`(`match_id`)" +
                        ") charset=utf8mb4"
            ).execute()
        }
    }

    fun getActive(sessionId: Int): CompletableFuture<Boolean> {
        return database.executeAsync { connection ->
            connection.prepareStatement("SELECT `status` FROM $table WHERE `match_id`=?")
                .apply { setInt(1, sessionId) }
                .executeQuery()
                .let { if (it.next()) it.getBoolean(1) else false }
        }
    }

    fun setActive(sessionId: Int, active: Boolean): CompletableFuture<Unit> {
        return database.executeAsync { connection ->
            connection.prepareStatement(
                "INSERT INTO $table (`match_id`, `status`) VALUES(?, ?) " +
                        "ON DUPLICATE KEY UPDATE `status`=VALUES(`status`)"
            )
                .apply {
                    setInt(1, sessionId)
                    setBoolean(2, active)
                }.execute()
        }
    }
}