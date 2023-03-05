package com.minepalm.nations.bukkit.invitation

import com.minepalm.library.database.impl.internal.MySQLDB
import java.util.*
import java.util.concurrent.CompletableFuture

class MySQLNationInvitationDatabase(
    val database: MySQLDB,
    val table: String
) {

    init{
        database.execute { connection ->
            connection.prepareStatement("CREATE TABLE IF NOT EXISTS $table (" +
                    "`row_id` INT AUTO_INCREMENT, " +
                    "`receiver` UUID, " +
                    "`nation_name` VARCHAR(32), " +
                    "`invited` UUID, " +
                    "`expired_time` BIGINT, " +
                    "PRIMARY KEY(`row_id`) "+
                    ") charset=utf8mb4")
                .execute()
        }
    }

    fun getInvitedNations(uuid: UUID): CompletableFuture<Map<String, UUID>>{
        return database.executeAsync { connection ->
            connection.prepareStatement("SELECT `nation_name`, `invited` FROM $table WHERE `receiver`=? AND `expired_time`>=?")
                .apply {
                    setString(1, uuid.toString())
                    setLong(2, System.currentTimeMillis())
                }.executeQuery()
                .let {
                    mutableMapOf<String, UUID>().apply {
                        while (it.next()) {
                            put(it.getString(1), UUID.fromString(it.getString(2)))
                        }
                    }
                }
        }
    }

    fun addInvitedNation(receiver: UUID, nationName: String, invited: UUID, expiredTime: Long): CompletableFuture<Unit>{
        return database.executeAsync<Unit> { connection ->
            connection.prepareStatement("INSERT INTO $table " +
                    "(`receiver`, `nation_name`, `invited`, `expired_time`) " +
                    "VALUES(?, ?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE `nation_name`=VALUES(`nation_name`), `expired_time`=VALUES(`expired_time`)")
                .apply {
                    setString(1, receiver.toString())
                    setString(2, nationName)
                    setString(3, invited.toString())
                    setLong(4, expiredTime)
                }.execute()
        }
    }

    fun remove(sender: UUID): CompletableFuture<Unit>{
        return database.executeAsync<Unit> { connection ->
            connection.prepareStatement("DELETE FROM $table WHERE `invited`=?")
                .apply { setString(1, sender.toString()) }
                .execute()
        }
    }

    fun purge(timeToDelete: Long): CompletableFuture<Unit>{
        return database.executeAsync<Unit> { connection ->
            connection.prepareStatement("DELETE FROM $table WHERE `expired_time` < ?")
                .apply { setLong(1, timeToDelete) }
                .execute()
        }
    }

}