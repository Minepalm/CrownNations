package com.minepalm.nations.core.mysql

import com.minepalm.library.database.impl.internal.MySQLDB
import com.minepalm.nations.utils.ServerLoc
import java.util.concurrent.CompletableFuture

class MySQLTerritoryWarpDatabase(
    val table: String = "`crownnations_warps`",
    val database: MySQLDB
) {

    init {
        database.execute {
            it.prepareStatement("CREATE TABLE IF NOT EXISTS $table ("
                    + "`id` INT NOT NULL AUTO_INCREMENT,"
                    + "`monument_id` INT NOT NULL,"
                    + "`server` VARCHAR(255) NOT NULL,"
                    + "`world` VARCHAR(255) NOT NULL,"
                    + "`x` INT NOT NULL,"
                    + "`y` INT NOT NULL,"
                    + "`z` INT NOT NULL,"
                    + "PRIMARY KEY (`id`),"
                    + "FOREIGN KEY (`monument_id`) REFERENCES `crownnations_monuments`(`monument_id`)"
                    + ") ENGINE=InnoDB DEFAULT CHARSET=utf8;")
                .execute()
        }
    }

    fun getWarpLocation(monumentId: Int): CompletableFuture<ServerLoc?> {
        return database.executeAsync {
            it.prepareStatement("SELECT * FROM $table WHERE monument_id = ?")
                .apply { setInt(1, monumentId) }
                .executeQuery()
                .use { rs ->
                    if (rs.next()) {
                        ServerLoc(
                            rs.getString("server"),
                            rs.getString("world"),
                            rs.getInt("x"),
                            rs.getInt("y"),
                            rs.getInt("z")
                        )
                    } else {
                        null
                    }
                }
        }
    }

    fun setWarpLocation(monumentId: Int, loc: ServerLoc): CompletableFuture<Unit> {
        return database.executeAsync {
            it.prepareStatement("INSERT INTO $table (monument_id, server, world, x, y, z) VALUES (?, ?, ?, ?, ?, ?)")
                .apply {
                    setInt(1, monumentId)
                    setString(2, loc.server)
                    setString(3, loc.world)
                    setInt(4, loc.x)
                    setInt(5, loc.y)
                    setInt(6, loc.z)
                }.execute()
        }
    }


}