package kr.rendog.nations.core.mysql

import com.minepalm.library.database.impl.internal.MySQLDB
import java.sql.Connection
import java.util.concurrent.CompletableFuture

class MySQLNationIdDatabase(
    val database : MySQLDB,
    val table : String = "`crownnations_ids`"
) {

    init{
        database.execute { connection ->
            connection.prepareStatement("CREATE TABLE IF NOT EXISTS $table (" +
                    "`nation_id` INT UNIQUE AUTO_INCREMENT," +
                    "`name` VARCHAR(32) UNIQUE NOT NULL, " +
                    "PRIMARY KEY(`nation_id`)" +
                    ") charset=utf8mb4")
                .execute()
        }
    }

    fun generateNewId(name : String) : CompletableFuture<Int> {
        return database.executeAsync<Int> { connection ->
            try {
                connection.autoCommit = false
                val hasName = hasName0(connection, name)
                if(hasName){
                    -1
                }else {
                    connection.prepareStatement("INSERT INTO $table (`name`) VALUES(?);")
                        .apply { setString(1, name) }.execute()
                    connection.prepareStatement("SELECT LAST_INSERT_ID();").executeQuery().let {
                        if (it.next())
                            it.getInt(1)
                        else
                            -1
                    }
                }
            }finally {
                connection.autoCommit = true
            }
        }
    }

    fun exists(nationId : Int) : CompletableFuture<Boolean> {
        return getName(nationId).thenApply { it != null }
    }

    fun hasName(name : String) : CompletableFuture<Boolean> {
        return database.executeAsync<Boolean> { hasName0(it, name) }
    }

    private fun hasName0(connection : Connection, name : String) : Boolean {
        return connection.prepareStatement("SELECT `nation_id` FROM $table WHERE `name`=?")
            .apply { setString(1, name) }
            .executeQuery()
            .let { it.next() }
    }

    fun getId(name : String) : CompletableFuture<Int?> {
        return database.executeAsync<Int?> { connection ->
            connection.prepareStatement("SELECT `nation_id` FROM $table WHERE `name`=?")
                .apply { setString(1, name) }
                .executeQuery()
                .let {
                    if(it.next()){
                        it.getInt(1)
                    }else{
                        null
                    }
                }
        }
    }

    fun getName(nationId : Int) : CompletableFuture<String?> {
        return database.executeAsync { connection ->
            connection.prepareStatement("SELECT `name` FROM $table WHERE `nation_id`=?")
                .apply { setInt(1, nationId) }
                .executeQuery()
                .let {
                    if(it.next()){
                        it.getString(1)
                    }else
                        null
                }
        }
    }

    fun deleteId(nationId : Int) : CompletableFuture<Boolean> {
        return database.executeAsync<Boolean> { connection ->
            connection.prepareStatement("SELECT `nation_id` FROM $table WHERE `nation_id`=? FOR UPDATE")
                .apply { setInt(1, nationId) }
                .executeQuery().next()
                .also { has ->
                    if(has){
                        connection.prepareStatement("DELETE FROM $table WHERE `nation_id`=?")
                            .apply { setInt(1, nationId) }
                            .execute()
                    }
                }
        }
    }

}