package kr.rendog.nations.core.mysql

import com.minepalm.library.database.impl.internal.MySQLDB
import kr.rendog.nations.NationRank
import java.sql.Connection
import java.sql.SQLException
import java.util.UUID
import java.util.concurrent.CompletableFuture

class MySQLNationMemberDatabase(
    val database : MySQLDB,
    val table : String
) {

    init{
        database.execute { connection ->
            connection.prepareStatement("CREATE TABLE IF NOT EXISTS $table (" +
                    "`uuid` VARCHAR(36), " +
                    "`nation_id` INT, " +
                    "`rank` VARCHAR(16) DEFAULT ?, " +
                    "PRIMARY KEY (`uuid`), " +
                    "FOREIGN KEY (`nation_id`) REFERENCES `crownnations_ids`(`nation_id`) ON DELETE CASCADE" +
                    ")" +
                    "charset=utf8mb4")
                .apply {
                    setString(1, "NONE")
                }
                .execute()
        }
    }

    fun getNation(uniqueId: UUID) : CompletableFuture<Int>{
        return database.executeAsync<Int> { connection ->
            connection.prepareStatement("SELECT `nation_id` FROM $table WHERE `uuid`=?")
                .apply {
                    setString(1, uniqueId.toString())
                }.executeQuery()
                .let {
                    if(it.next())
                        it.getInt(1)
                    else
                        -1
                }
        }
    }

    fun setMember(uniqueId : UUID, nationId : Int, rank : NationRank) : CompletableFuture<Boolean> {
        return database.executeAsync<Boolean> { connection ->
            setMember0(connection, uniqueId, nationId, rank)
        }
    }

    private fun setMember0(connection : Connection, uniqueId: UUID, nationId: Int, rank : NationRank) : Boolean {
        val ps = connection.prepareStatement("INSERT INTO $table " +
                " (`uuid`, `nation_id`, `rank`) VALUES(?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE `rank` = VALUES(`rank`)")
            .apply {
                setString(1, uniqueId.toString())
                setInt(2, nationId)
                setString(3, rank.name)
            }

        ps.execute()
        return true
    }

    fun transferOwnership(nationId : Int, uniqueId : UUID) : CompletableFuture<Boolean> {
        return database.executeAsync<Boolean> { connection ->
            val owner = connection.prepareStatement("SELECT `uuid` FROM $table WHERE `nation_id`=? AND `rank`=?")
                .apply {
                    setInt(1, nationId)
                    setString(2, NationRank.OWNER.name)
                }.executeQuery()
                .let {
                    if(it.next()){
                        UUID.fromString(it.getString(1))
                    }else
                        null
                }
            if(owner != null){
                setMember0(connection, uniqueId, nationId, NationRank.OWNER)
                setMember0(connection, owner, nationId, NationRank.RESIDENT)
                true
            }else
                false
        }
    }

    fun removeMember(nationId : Int, uniqueId: UUID) : CompletableFuture<Boolean>{
        return database.executeAsync<Boolean> { connection ->
            connection.prepareStatement("SELECT `uuid` FROM $table WHERE `nation_id` = ? AND `uuid` = ?")
                .apply {
                    setInt(1, nationId)
                    setString(2, uniqueId.toString())
                }.executeQuery().next()
                .apply {
                    if (this) {
                        connection.prepareStatement("DELETE FROM $table WHERE `nation_id` = ? AND `uuid` = ?")
                            .apply {
                                setInt(1, nationId)
                                setString(2, uniqueId.toString())
                            }.execute()
                    }
                }
        }
    }

    fun getRank(uniqueId: UUID, nationId : Int) : CompletableFuture<NationRank>{
        return database.executeAsync<NationRank> { connection ->
            connection.prepareStatement("SELECT `rank` FROM $table WHERE `uuid`=? AND `nation_id`=?")
                .apply {
                    setString(1, uniqueId.toString())
                    setInt(2, nationId)
                }.executeQuery()
                .let {
                    if(it.next()) NationRank.valueOf(it.getString(1)) else NationRank.NONE
                }
        }
    }

    fun getMembers(nationId : Int, rank : NationRank) : CompletableFuture<Set<UUID>> {
        return database.executeAsync<Set<UUID>> { connection ->
            connection.prepareStatement("SELECT `uuid` FROM $table WHERE `nation_id`=? AND `rank`=?")
                .apply {
                    setInt(1, nationId)
                    setString(2, rank.name)
                }.executeQuery()
                .let {
                    mutableSetOf<UUID>().apply {
                        while (it.next()){
                            add(UUID.fromString(it.getString(1)))
                        }
                    }
                }
        }
    }

    fun getMembers(nationId : Int) : CompletableFuture<Map<UUID, NationRank>> {
        return database.executeAsync { connection ->
            connection.prepareStatement("SELECT `uuid`, `rank` FROM $table WHERE `nation_id`=?")
                .apply {
                    setInt(1, nationId)
                }.executeQuery()
                .let {
                    mutableMapOf<UUID, NationRank>().apply {
                        while (it.next()){
                            put(UUID.fromString(it.getString(1)), NationRank.valueOf(it.getString(2)))
                        }
                    }
                }
        }
    }

}