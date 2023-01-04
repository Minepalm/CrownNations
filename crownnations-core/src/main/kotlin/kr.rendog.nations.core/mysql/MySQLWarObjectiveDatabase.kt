package kr.rendog.nations.core.mysql

import com.minepalm.library.database.impl.internal.MySQLDB
import kr.rendog.nations.war.WarObjective
import java.util.*
import java.util.concurrent.CompletableFuture

class MySQLWarObjectiveDatabase(
    val database: MySQLDB,
    val table: String,
) {

    init{
        database.execute { connection ->
            connection.prepareStatement("CREATE TABLE IF NOT EXISTS $table ( " +
                    "`match_id` INT, " +
                    "`nation_id` INT, "+
                    "`user` VARCHAR(36), "+
                    "`objective_type` VARCHAR(32), " +
                    "`time` BIGINT, "+
                    "`data` TEXT, " +
                    "FOREIGN KEY (`match_id`) REFERENCES `crownnations_matches`(`match_id`)" +
                    ") charset=utf8mb4")
                .execute()
        }
    }

    fun insert(objective: WarObjective): CompletableFuture<Unit> {
        return database.executeAsync<Unit> { connection ->
            connection.prepareStatement("INSERT INTO $table (`match_id`, `nation_id`, `user`, `objective_type`, `time`, `data`)" +
                    "VALUES(?, ?, ?, ?, ?, ?)")
                .apply {
                    setInt(1, objective.matchId)
                    setInt(2, objective.nationId)
                    setString(3, objective.user.toString())
                    setString(4, objective.objectiveType.name)
                    setLong(5, objective.time)
                    setString(6, objective.data)
                }.execute()
        }
    }

    fun select(matchId: Int): CompletableFuture<List<WarObjective>> {
        return database.executeAsync<List<WarObjective>> { connection ->
            connection.prepareStatement("SELECT `match_id`, `nation_id`, `user`, `objective_type`, `data` FROM $table " +
                    "WHERE `match_id`=?")
                .apply { setInt(1, matchId) }
                .executeQuery()
                .let {
                    mutableListOf<WarObjective>().apply {
                        while (it.next()){
                            val nationId = it.getInt(2)
                            val uuid = UUID.fromString(it.getString(3))
                            val objectiveType = it.getString(4).let { WarObjective.Type.valueOf(it) }
                            val time = it.getLong(5)
                            val data = it.getString(6)
                            add(WarObjective(matchId, nationId, objectiveType, uuid, time, data))
                        }
                    }
                }
        }
    }

    fun delete(matchId: Int): CompletableFuture<Unit> {
        return database.executeAsync { connection ->
            connection.prepareStatement("DELETE FROM $table WHERE `match_Id`=?")
                .apply { setInt(1, matchId) }
                .execute()
        }
    }

}