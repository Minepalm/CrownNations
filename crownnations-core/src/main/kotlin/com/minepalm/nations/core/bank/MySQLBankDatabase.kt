package com.minepalm.nations.core.bank

import com.minepalm.library.database.impl.internal.MySQLDB
import java.util.concurrent.CompletableFuture

class MySQLBankDatabase(
    val database: MySQLDB,
    val table: String
) {

    init {
        database.execute { connection ->
            connection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS $table ( " +
                        "`nation_id` INT, " +
                        "`balance` DOUBLE DEFAULT 0, " +
                        "PRIMARY KEY(`nation_id`), " +
                        "FOREIGN KEY (`nation_id`) REFERENCES `crownnations_ids` (`nation_id`) ON DELETE CASCADE" +
                        ") charset=utf8mb4"
            )
                .execute()
        }
    }

    fun getMoney(nationId: Int): CompletableFuture<Double> {
        return database.executeAsync<Double> { connection ->
            connection.prepareStatement("SELECT `balance` FROM $table WHERE `nation_id`=? FOR UPDATE")
                .apply { setInt(1, nationId) }
                .executeQuery()
                .let {
                    if (it.next())
                        it.getDouble(1)
                    else
                        0.0
                }
        }
    }

    fun has(nationId: Int, value: Double): CompletableFuture<Boolean> {
        return database.executeAsync { connection ->
            connection.prepareStatement("SELECT `balance` FROM $table WHERE `nation_id`=?")
                .apply { setInt(1, nationId) }
                .executeQuery()
                .let {
                    if (it.next())
                        it.getDouble(1)
                    else
                        0.0
                } >= value
        }
    }

    fun setMoney(nationId: Int, value: Double): CompletableFuture<Boolean> {
        return database.executeAsync<Boolean> { connection ->
            try {
                connection.autoCommit = false
                val exists = connection.prepareStatement("SELECT `nation_id` FROM $table WHERE `nation_id`=?")
                    .apply { setInt(1, nationId) }.executeQuery().next()
                exists.also {
                    if (exists) {
                        connection.prepareStatement(
                            "INSERT INTO $table (`nation_id`, `balance`) VALUES(?, ?) " +
                                    "ON DUPLICATE KEY UPDATE `balance`=VALUES(`balance`)"
                        ).apply {
                            setInt(1, nationId)
                            setDouble(2, value)
                        }.execute()
                    }
                }
            } finally {
                connection.autoCommit = true
            }
        }
    }

    fun takeMoney(nationId: Int, value: Double): CompletableFuture<Double> {
        return database.executeAsync<Double> { connection ->
            try {
                connection.autoCommit = false
                connection.prepareStatement("UPDATE $table SET `balance` = VALUES(`balance`) - ? WHERE `nation_id`=?")
                    .apply {
                        setDouble(1, value)
                        setInt(2, nationId)
                    }.execute()

                connection.prepareStatement("SELECT `balance` FROM $table WHERE `nation_id`=?")
                    .apply { setInt(1, nationId) }
                    .executeQuery()
                    .let {
                        if (it.next()) {
                            it.getDouble(1)
                        } else
                            0.0
                    }
            } finally {
                connection.autoCommit = true
            }
        }
    }

    fun giveMoney(nationId: Int, value: Double): CompletableFuture<Double> {
        return database.executeAsync<Double> { connection ->
            try {
                connection.autoCommit = false
                connection.prepareStatement("UPDATE $table SET `balance` = VALUES(`balance`) + ? WHERE `nation_id`=?")
                    .apply {
                        setDouble(1, value)
                        setInt(2, nationId)
                    }.execute()

                connection.prepareStatement("SELECT `balance` FROM $table WHERE `nation_id`=?")
                    .apply { setInt(1, nationId) }
                    .executeQuery()
                    .let {
                        if (it.next()) {
                            it.getDouble(1)
                        } else
                            0.0
                    }
            } finally {
                connection.autoCommit = true
            }
        }
    }
}