package kr.rendog.nations.core.mysql

import com.minepalm.library.database.impl.internal.MySQLDB
import java.util.UUID
import java.util.concurrent.CompletableFuture

class MySQLAdminDatabase(
    val database : MySQLDB,
    val table : String
) {

    init{
        database.execute { connection ->
            connection.prepareStatement("CREATE TABLE IF NOT EXISTS $table ( " +
                    "`uuid` VARCHAR(36)," +
                    "`level` INT DEFAULT 0, " +
                    "PRIMARY KEY(`uuid`))" +
                    "charset=utf8mb4")
                .execute()
        }
    }

    fun setAdmin(uniqueId : UUID, level : Int) : CompletableFuture<Unit>{
        return database.executeAsync { connection ->
            connection.prepareStatement("INSERT INTO $table (`uuid`, `level`) VALUES(?, ?) " +
                    "ON DUPLICATE KEY UPDATE `level`=VALUES(`level`)")
                .apply {
                    setString(1, uniqueId.toString())
                    setInt(2, level)
                }.execute()
        }
    }

    fun removeAdmin(uniqueId: UUID) : CompletableFuture<Unit> {
        return database.executeAsync { connection ->
            connection.prepareStatement("DELETE FROM $table WHERE `uuid`=?")
                .apply { setString(1, uniqueId.toString()) }
                .execute()
        }
    }

    fun getAdmins() : CompletableFuture<Map<UUID, Int>> {
        return database.executeAsync { connection ->
            connection.prepareStatement("SELECT `uuid`, `level` FROM $table")
                .executeQuery()
                .let {
                    mutableMapOf<UUID, Int>().apply {
                        while (it.next()){
                            put(UUID.fromString(it.getString(1)), it.getInt(2))
                        }
                    }
                }
        }
    }
}