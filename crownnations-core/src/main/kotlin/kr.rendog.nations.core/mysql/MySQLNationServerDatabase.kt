package kr.rendog.nations.core.mysql

import com.minepalm.library.database.impl.internal.MySQLDB
import java.util.concurrent.CompletableFuture

class MySQLNationServerDatabase(
    val database : MySQLDB,
    val table : String
) {

    init{
        database.execute { connection ->
            connection.prepareStatement("CREATE TABLE IF NOT EXISTS $table (" +
                    "`serverName` VARCHAR(32), " +
                    "`online` BOOLEAN DEFAULT FALSE, " +
                    "PRIMARY KEY(`serverName`)" +
                    ") charset=utf8mb4")
                .execute()
        }
    }

    fun getServers() : CompletableFuture<List<String>> {
        return database.executeAsync<List<String>> { connection ->
            connection.prepareStatement("SELECT `serverName` FROM $table")
                .executeQuery()
                .let {
                    mutableListOf<String>().apply {
                        while (it.next())
                            add(it.getString(1))
                    }
                }

        }
    }

    fun getOnlineServers() : CompletableFuture<List<String>> {
        return database.executeAsync { connection ->
            connection.prepareStatement("SELECT `serverName` FROM $table WHERE `online`=?")
                .apply { setBoolean(1, true) }
                .executeQuery()
                .let {
                    mutableListOf<String>().apply {
                        while (it.next())
                            add(it.getString(1))
                    }
                }

        }
    }

    fun setOnline(name : String, online : Boolean) : CompletableFuture<Unit> {
        return database.executeAsync { connection ->
            connection.prepareStatement(
                "INSERT INTO $table (`serverName`, `online`) VALUES(?, ?) " +
                        "ON DUPLICATE KEY UPDATE `online`=VALUES(`online`)"
            )
                .apply {
                    setString(1, name)
                    setBoolean(2, online)
                }.execute()
        }
    }

    fun isOnline(name : String) : CompletableFuture<Boolean> {
        return database.executeAsync { connection ->
            connection.prepareStatement("SELECT `online` FROM $table WHERE `serverName`=?")
                .apply { setString(1, name) }
                .executeQuery()
                .let {
                    if(it.next()){
                        it.getBoolean(1)
                    }else
                        false
                }
        }
    }
}