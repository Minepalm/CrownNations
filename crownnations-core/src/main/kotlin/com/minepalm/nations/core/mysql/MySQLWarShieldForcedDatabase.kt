package com.minepalm.nations.core.mysql

import com.minepalm.library.database.impl.internal.MySQLDB
import java.util.concurrent.CompletableFuture

class MySQLWarShieldForcedDatabase(
    val database: MySQLDB,
    val table: String
) {

    init {
        database.execute { connection ->
            connection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS $table (" +
                        "`match_id` INT, " +
                        "`expire_time` BIGINT, " +
                        "PRIMARY KEY (`match_id`), " +
                        "FOREIGN KEY (`match_id`) REFERENCES `crownnations_matches`(`match_id`))"
            ).execute()
        }
    }

    fun isForced(nationId: Int): CompletableFuture<Boolean> {
        return database.executeAsync { connection ->
            connection.prepareStatement("SELECT `expire_time` FROM $table WHERE `match_id`=?")
                .apply { setInt(1, nationId) }
                .executeQuery()
                .let { if (it.next()) it.getLong(1) >= System.currentTimeMillis() else false }
        }
    }

    fun getExpireTime(nationId: Int): CompletableFuture<Long> {
        return database.executeAsync { connection ->
            connection.prepareStatement("SELECT `expire_time` FROM $table WHERE `match_id`=?")
                .apply { setInt(1, nationId) }
                .executeQuery()
                .let { if (it.next()) it.getLong(1) else 0L }
        }
    }

    fun setForced(nationId: Int, timeToExpire: Long): CompletableFuture<Unit> {
        return database.executeAsync { connection ->
            connection.prepareStatement(
                "INSERT INTO $table (`match_id`, `expire_time`) VALUES(?, ?) " +
                        "ON DUPLICATE KEY UPDATE `expire_time`=VALUES(`expire_time`)"
            )
                .apply {
                    setInt(1, nationId)
                    setLong(2, timeToExpire)
                }.execute()
        }
    }
}