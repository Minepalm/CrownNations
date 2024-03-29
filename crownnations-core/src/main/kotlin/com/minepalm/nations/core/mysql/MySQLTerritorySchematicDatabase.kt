package com.minepalm.nations.core.mysql

import com.minepalm.library.database.impl.internal.MySQLDB
import java.sql.Connection
import java.util.concurrent.CompletableFuture

class MySQLTerritorySchematicDatabase(
    val database: MySQLDB,
    val table: String
) {

    init {
        database.execute { connection ->
            connection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS $table ( " +
                        "`monument_id` INT, " +
                        "`data` MEDIUMBLOB, " +
                        "PRIMARY KEY(`monument_id`), " +
                        "FOREIGN KEY (`monument_id`) REFERENCES `crownnations_monuments`(`monument_id`) ON DELETE CASCADE)" +
                        "charset=utf8mb4"
            )
                .execute()
        }
    }

    fun loadAsync(id: Int): CompletableFuture<com.minepalm.nations.territory.MonumentBlob?> {
        return database.executeAsync { loadInternally(it, id) }
    }

    fun loadBlocking(id: Int): com.minepalm.nations.territory.MonumentBlob? {
        return database.execute { loadInternally(it, id) }
    }

    private fun loadInternally(connection: Connection, id: Int): com.minepalm.nations.territory.MonumentBlob? {
        return connection.prepareStatement("SELECT `data` FROM $table WHERE `monument_id`=?")
            .apply { setInt(1, id) }
            .executeQuery()
            .let {
                if (it.next()) {
                    com.minepalm.nations.territory.MonumentBlob(id, it.getBytes(2))
                } else
                    null
            }
    }

    fun saveAsync(data: com.minepalm.nations.territory.MonumentBlob): CompletableFuture<Unit> {
        return database.executeAsync { saveInternally(it, data) }
    }

    fun saveBlocking(data: com.minepalm.nations.territory.MonumentBlob) {
        database.execute { saveInternally(it, data) }
    }

    private fun saveInternally(connection: Connection, data: com.minepalm.nations.territory.MonumentBlob) {
        connection.prepareStatement(
            "INSERT INTO $table (`monument_id`, `data`) VALUES(?, ?) " +
                    "ON DUPLICATE KEY UPDATE `data`=VALUES(`data`)"
        )
            .apply {
                setInt(1, data.monumentId)
                setBytes(2, data.data)
            }.execute()
    }

    fun deleteAsync(id: Int): CompletableFuture<Unit> {
        return database.executeAsync { deleteInternally(it, id) }
    }

    fun deleteBlocking(id: Int) {
        return database.execute { deleteInternally(it, id) }
    }

    private fun deleteInternally(connection: Connection, id: Int) {
        connection.prepareStatement("DELETE FROM $table WHERE `monument_id`=?")
            .apply { setInt(1, id) }
            .execute()
    }
}