package com.minepalm.nations.core.mysql

import com.minepalm.library.database.impl.internal.MySQLDB
import java.util.concurrent.CompletableFuture

class MySQLNationDataDatabase(
    val database: MySQLDB,
    val table: String,
) {

    init {
        database.execute { connection ->
            connection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS  $table (" +
                        "`nation_id` INT, " +
                        "`key` VARCHAR(64), " +
                        "`value` VARCHAR(64), " +
                        "FOREIGN KEY (`nation_id`) REFERENCES `crownnations_ids`(`nation_id`) ON DELETE CASCADE)" +
                        "charset=utf8mb4"
            )
                .execute()
        }
    }

    fun setProperty(nationId: Int, key: String, value: String): CompletableFuture<Unit> {
        return database.executeAsync<Unit> { connection ->
            connection.prepareStatement(
                "INSERT INTO $table (`nation_id`, `key`, `value`) VALUES(?, ?, ?)" +
                        "ON DUPLICATE KEY UPDATE `key`=VALUES(`key`), `value`=VALUES(`value`)"
            )
                .apply {
                    setInt(1, nationId)
                    setString(2, key)
                    setString(3, value)
                }.execute()
        }
    }

    fun removeProperty(nationId: Int, key: String): CompletableFuture<Unit> {
        return database.executeAsync<Unit> { connection ->
            connection.prepareStatement("DELETE FROM $table WHERE `nation_id` = ? AND `key`=?")
                .apply {
                    setInt(1, nationId)
                    setString(2, key)
                }.execute()
        }
    }

    fun getProperty(nationId: Int, key: String): CompletableFuture<String?> {
        return database.executeAsync { connection ->
            connection.prepareStatement("SELECT `value` FROM $table WHERE `nation_id`=? AND `key`=?")
                .apply {
                    setInt(1, nationId)
                    setString(2, key)
                }.executeQuery()
                .let {
                    if (it.next()) {
                        it.getString(1)
                    } else
                        null
                }
        }
    }

    fun getProperties(nationId: Int): CompletableFuture<Map<String, String>> {
        return database.executeAsync { connection ->
            connection.prepareStatement("SELECT `key`, `value` FROM $table WHERE `nation_id` = ?")
                .apply { setInt(1, nationId) }
                .executeQuery()
                .let {
                    mutableMapOf<String, String>().apply {
                        while (it.next()) {
                            this[it.getString(1)] = it.getString(2)
                        }
                    }
                }
        }
    }
}