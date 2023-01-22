package com.minepalm.nations.core.mysql

import com.minepalm.library.database.impl.internal.MySQLDB
import com.minepalm.nations.war.WarType
import java.util.concurrent.CompletableFuture

class MySQLWarSessionDatabase(
    private val database: MySQLDB,
    private val table: String
) {

    init {
        database.execute { connection ->
            connection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS $table (" +
                        "`session_id` INT UNIQUE AUTO_INCREMENT, " +
                        "`type` VARCHAR(32) NOT NULL, " +
                        "`home_nation` INT NOT NULL, " +
                        "`away_nation` INT NOT NULL, " +
                        "PRIMARY KEY (`session_id`))" +
                        "charset=utf8mb4"
            )
                .execute()
        }
    }

    fun createNewSession(info: com.minepalm.nations.war.WarInfo): CompletableFuture<Int> {
        return database.executeAsync<Int> { connection ->
            connection.prepareStatement(
                "INSERT INTO $table " +
                        "(`type`, `home_nation`, `away_nation`) " +
                        "VALUES(?, ?, ?, ?)"
            )
                .apply {
                    setString(1, info.warType.name)
                    setInt(2, info.homeNation)
                    setInt(3, info.awayNation)
                }.execute()
            connection.prepareStatement("SELECT LAST_INSERT_ID();").executeQuery().let {
                if (it.next())
                    it.getInt(1)
                else
                    -1
            }
        }
    }

    fun getSessionByNation(nationId: Int): CompletableFuture<com.minepalm.nations.war.SessionData?> {
        return database.executeAsync<com.minepalm.nations.war.SessionData?> { connection ->
            connection.prepareStatement(
                "SELECT `match_id`, `type`, `home_nation`, `away_nation`" +
                        "FROM $table WHERE `home_nation`=? OR `away_nation`=?"
            )
                .apply {
                    setInt(1, nationId)
                    setInt(2, nationId)
                }.executeQuery()
                .let {
                    if (it.next()) {
                        val matchId = it.getInt(1)
                        val warData = com.minepalm.nations.war.WarInfo(
                            WarType.valueOf(it.getString(2)),
                            it.getInt(3),
                            it.getInt(4)
                        )
                        com.minepalm.nations.war.SessionData(matchId, warData)
                    } else
                        null
                }
        }
    }

    fun getAll(): CompletableFuture<List<com.minepalm.nations.war.SessionData>> {
        return database.executeAsync<List<com.minepalm.nations.war.SessionData>> { connection ->
            connection.prepareStatement(
                "SELECT `match_id`, `type`, `home_nation`, `away_nation`" +
                        "FROM $table "
            )
                .executeQuery()
                .let {
                    mutableListOf<com.minepalm.nations.war.SessionData>().apply {
                        val matchId = it.getInt(1)
                        val warData = com.minepalm.nations.war.WarInfo(
                            WarType.valueOf(it.getString(2)),
                            it.getInt(3),
                            it.getInt(4)
                        )
                        add(com.minepalm.nations.war.SessionData(matchId, warData))
                    }
                }
        }
    }

    fun getSession(matchId: Int): CompletableFuture<com.minepalm.nations.war.SessionData?> {
        return database.executeAsync { connection ->
            connection.prepareStatement(
                "SELECT `match_id`, `type`, `home_nation`, `away_nation`" +
                        "FROM $table WHERE `session_id`=?"
            )
                .apply {
                    setInt(1, matchId)
                }.executeQuery()
                .let {
                    if (it.next()) {
                        val warData = com.minepalm.nations.war.WarInfo(
                            WarType.valueOf(it.getString(2)),
                            it.getInt(3),
                            it.getInt(4)
                        )
                        com.minepalm.nations.war.SessionData(matchId, warData)
                    } else
                        null
                }
        }
    }

    fun delete(matchId: Int): CompletableFuture<Unit> {
        return database.executeAsync<Unit> { connection ->
            connection.prepareStatement("DELETE FROM $table WHERE `match_id`=?")
                .apply { setInt(1, matchId) }
                .execute()
        }
    }

}