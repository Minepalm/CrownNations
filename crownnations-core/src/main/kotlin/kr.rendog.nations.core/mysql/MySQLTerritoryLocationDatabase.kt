package kr.rendog.nations.core.mysql

import com.minepalm.library.database.impl.internal.MySQLDB
import kr.rendog.nations.territory.MonumentSchema
import kr.rendog.nations.territory.ProtectionRange
import kr.rendog.nations.utils.ServerLoc
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.util.concurrent.CompletableFuture

class MySQLTerritoryLocationDatabase(
    val database: MySQLDB,
    val table: String
) {

    init {
        database.execute { connection ->
            connection.prepareStatement("CREATE TABLE IF NOT EXISTS  $table (" +
                    "`monument_id` INT UNIQUE AUTO_INCREMENT, "+ //1
                    "`server` VARCHAR(32), "+ //2
                    "`world` VARCHAR(64), "+ //3
                    "`nation_id` INT, " + //4
                    "`type` VARCHAR(64), " + //5
                    "`min_x` INT," + //6
                    "`min_y` INT," + //7
                    "`min_z` INT, "+ //8
                    "`max_x` INT," + //9
                    "`max_y` INT," + //10
                    "`max_z` INT, "+ //11
                    "`center_x` INT, "+ //12
                    "`center_y` INT, "+ //13
                    "`center_z` INT, " + //14
                    "PRIMARY KEY(`monument_id`))" +
                    "charset=utf8mb4")
                .execute()

            //인덱스는 `server`, `world` 쌍으로 하나, nation_id 로 하나 걸면 좋을듯.
        }
    }

    fun createNewMonument(schema: MonumentSchema): CompletableFuture<MonumentSchema>{
        return database.executeAsync<MonumentSchema> { connection ->
             connection.prepareStatement("INSERT INTO $table " +
                    "(`server`, `world`, `nation_id`, `type`, " +
                    "`min_x`, `min_y`, `min_z`, " +
                    "`max_x`, `max_y`, `max_z`, " +
                    "`center_x`, `center_y`, `center_z`)" +
                    "VALUES(?, ?, ?, ?, " +
                    "?, ?, ?, " +
                    "?, ?, ?, " +
                    "?, ?, ?)")
                .apply {
                    setString(1, schema.center.server)
                    setString(2, schema.center.world)
                    setInt(3, schema.nationId)
                    setString(4, schema.type)
                    setInt(5, schema.range.minimumLocation.x)
                    setInt(6, schema.range.minimumLocation.y)
                    setInt(7, schema.range.minimumLocation.z)
                    setInt(8, schema.range.maximumLocation.x)
                    setInt(9, schema.range.maximumLocation.y)
                    setInt(10, schema.range.maximumLocation.z)
                    setInt(11, schema.center.x)
                    setInt(12, schema.center.y)
                    setInt(13, schema.center.z)
                }.execute()
            val id = connection.prepareStatement("SELECT LAST_INSERT_ID()").executeQuery().let {
                if(it.next()) it.getInt(1) else -1
            }
            MonumentSchema(id,
                schema.nationId,
                schema.type,
                schema.center,
                schema.range)
        }
    }

    fun getMonument(id: Int): CompletableFuture<MonumentSchema?> {
        return database.executeAsync<MonumentSchema?> { connection ->
            connection.prepareStatement("SELECT `monument_id`, `server`, `world`, `nation_id`, `type`, " +
                    "`min_x`, `min_y`, `min_z`, " +
                    "`max_x`, `max_y`, `max_z`, " +
                    "`center_x`, `center_y`, `center_z` FROM $table WHERE `monument_id`=?")
                .apply {
                    setInt(1, id)
                }
                .executeQuery()
                .let {
                    if (it.next()){
                        val id = it.getInt(1)
                        val nationId = it.readNationId()
                        val type = it.readType()
                        val center = it.readCenter()
                        val max = it.readMaximum()
                        val min = it.readMinimum()
                        MonumentSchema(id, nationId, type, center, ProtectionRange(max, min))
                    }else
                        null
                }
        }
    }

    fun getServerMonuments(server: String): CompletableFuture<List<MonumentSchema>>{
        return database.executeAsync<List<MonumentSchema>> { connection ->
            connection.prepareStatement("SELECT `monument_id`, `server`, `world`, `nation_id`, `type`, " +
                    "`min_x`, `min_y`, `min_z`, " +
                    "`max_x`, `max_y`, `max_z`, " +
                    "`center_x`, `center_y`, `center_z` FROM $table WHERE `server`=?")
                .apply {
                    setString(1, server)
                }
                .executeQuery()
                .let {
                    mutableListOf<MonumentSchema>().apply {
                        while (it.next()){
                            val id = it.getInt(1)
                            val nationId = it.readNationId()
                            val type = it.readType()
                            val center = it.readCenter()
                            val max = it.readMaximum()
                            val min = it.readMinimum()
                            add(MonumentSchema(id, nationId, type, center, ProtectionRange(max, min)))
                        }
                    }
                }
        }
    }

    fun exists(monumentId: Int): CompletableFuture<Boolean>{
        return database.executeAsync<Boolean> { connection ->
            connection.prepareStatement("SELECT `monument_id` FROM $table WHERE `monumnet_id`=?")
                .apply { setInt(1, monumentId) }
                .executeQuery()
                .next()
        }
    }

    fun getServerWorldMonuments(server: String, world: String): CompletableFuture<List<MonumentSchema>>{
        return database.executeAsync<List<MonumentSchema>> { connection ->
            connection.prepareStatement("SELECT `monument_id`, `server`, `world`, `nation_id`, `type`, " +
                    "`min_x`, `min_y`, `min_z`, " +
                    "`max_x`, `max_y`, `max_z`, " +
                    "`center_x`, `center_y`, `center_z` FROM $table WHERE `server`=? AND `world`=?")
                .apply {
                    setString(1, server)
                    setString(2, world)
                }
                .executeQuery()
                .let {
                    mutableListOf<MonumentSchema>().apply {
                        while (it.next()){
                            val id = it.getInt(1)
                            val nationId = it.readNationId()
                            val type = it.readType()
                            val center = it.readCenter()
                            val max = it.readMaximum()
                            val min = it.readMinimum()
                            add(MonumentSchema(id, nationId, type, center, ProtectionRange(max, min)))
                        }
                    }
                }
        }
    }

    fun getNationMonuments(nationId: Int): CompletableFuture<List<MonumentSchema>>{
        return database.executeAsync<List<MonumentSchema>> { connection ->
            connection.prepareStatement("SELECT `monument_id`, `server`, `world`, `nation_id`, `type`, " +
                    "`min_x`, `min_y`, `min_z`, " +
                    "`max_x`, `max_y`, `max_z`, " +
                    "`center_x`, `center_y`, `center_z` FROM $table WHERE `nation_id`=?")
                .apply {
                    setInt(1, nationId)
                }
                .executeQuery()
                .let {
                    mutableListOf<MonumentSchema>().apply {
                        while (it.next()){
                            val id = it.getInt(1)
                            val type = it.readType()
                            val center = it.readCenter()
                            val max = it.readMaximum()
                            val min = it.readMinimum()
                            add(MonumentSchema(id, nationId, type, center, ProtectionRange(max, min)))
                        }
                    }
                }
        }
    }

    fun update(schema: MonumentSchema): CompletableFuture<Unit>{
        return database.executeAsync<Unit> { connection ->
            connection.prepareStatement("INSERT INTO $table " +
                    "(`server`, `world`, `nation_id`, `type`, " +
                    "`min_x`, `min_y`, `min_z`, " +
                    "`max_x`, `max_y`, `max_z`, " +
                    "`center_x`, `center_y`, `center_z`)" +
                    "VALUES(?, ?, ?, ?, " +
                    "?, ?, ?, " +
                    "?, ?, ?, " +
                    "?, ?, ?)" +
                    "ON DUPLICATE KEY UPDATE " +
                    "`server`=VALUES(`server`), "+
                    "`world`=VALUES(`world`), " +
                    "`nation_id`=VALUES(`nation_id`), " +
                    "`min_x`=VALUES(`min_x`), " +
                    "`min_y`=VALUES(`min_y`), " +
                    "`min_z`=VALUES(`min_z`), " +
                    "`max_x`=VALUES(`max_x`), " +
                    "`max_y`=VALUES(`max_y`), " +
                    "`max_z`=VALUES(`max_z`), " +
                    "`center_x`=VALUES(`center_x`), " +
                    "`center_y`=VALUES(`center_y`), " +
                    "`center_z`=VALUES(`center_z`)")
                .apply {
                    setString(1, schema.center.server)
                    setString(2, schema.center.world)
                    setInt(3, schema.nationId)
                    setString(4, schema.type)
                    setInt(5, schema.range.minimumLocation.x)
                    setInt(6, schema.range.minimumLocation.y)
                    setInt(7, schema.range.minimumLocation.z)
                    setInt(8, schema.range.maximumLocation.x)
                    setInt(9, schema.range.maximumLocation.y)
                    setInt(10, schema.range.maximumLocation.z)
                    setInt(11, schema.center.x)
                    setInt(12, schema.center.y)
                    setInt(13, schema.center.z)
                }.execute()
        }
    }

    fun deleteMonument(id: Int): CompletableFuture<Unit>{
        return database.executeAsync<Unit> { connection ->
            connection.prepareStatement("DELETE FROM $table WHERE `monument_id`=?")
                .apply { setInt(1, id) }
                .execute()
        }
    }

    fun deleteNationMonuments(nationId: Int): CompletableFuture<Unit>{
        return database.executeAsync<Unit> { connection ->
            connection.prepareStatement("DELETE FROM $table WHERE `nation_id`=?")
                .apply { setInt(1, nationId) }
                .execute()
        }
    }

    fun updateCenter(monumentId: Int, loc: ServerLoc): CompletableFuture<Boolean>{
        return database.executeAsync<Boolean> { connection ->
            connection.prepareStatement("SELECT `monument_id` FROM $table WHERE `monument_id`=?")
            .apply { setInt(1, monumentId) }
            .executeQuery()
                .next()
                .also { exists ->
                    if(exists){
                        connection.prepareStatement("UPDATE $table SET `center_x`=?, `center_y`=?, `center_z`=? ")
                            .apply {
                                setInt(1, loc.x)
                                setInt(2, loc.y)
                                setInt(3, loc.z)
                            }.execute()

                    }
                }
        }
    }

    fun updateMinimum(monumentId: Int, loc: ServerLoc): CompletableFuture<Boolean>{
        return database.executeAsync<Boolean> { connection ->
            connection.prepareStatement("SELECT `monument_id` FROM $table WHERE `monument_id`=?")
                .apply { setInt(1, monumentId) }
                .executeQuery()
                .next()
                .also { exists ->
                    if(exists){
                        connection.prepareStatement("UPDATE $table SET `min_x`=?, `min_y`=?, `min_z`=? ")
                            .apply {
                                setInt(1, loc.x)
                                setInt(2, loc.y)
                                setInt(3, loc.z)
                            }.execute()

                    }
                }
        }
    }

    fun updateMaximum(monumentId: Int, loc: ServerLoc): CompletableFuture<Boolean>{
        return database.executeAsync<Boolean> { connection ->
            connection.prepareStatement("SELECT `monument_id` FROM $table WHERE `monument_id`=?")
                .apply { setInt(1, monumentId) }
                .executeQuery()
                .next()
                .also { exists ->
                    if(exists){
                        connection.prepareStatement("UPDATE $table SET `max_x`=?, `max_y`=?, `max_z`=? ")
                            .apply {
                                setInt(1, loc.x)
                                setInt(2, loc.y)
                                setInt(3, loc.z)
                            }.execute()

                    }
                }
        }
    }

    private fun PreparedStatement.writeMinimum(loc: ServerLoc){
        setInt(6, loc.x)
        setInt(7, loc.y)
        setInt(8, loc.z)
    }

    private fun PreparedStatement.writeMaximum(loc: ServerLoc){
        setInt(9, loc.x)
        setInt(10, loc.y)
        setInt(11, loc.z)
    }

    private fun PreparedStatement.writeCenter(loc: ServerLoc){
        setInt(12, loc.x)
        setInt(13, loc.y)
        setInt(14, loc.z)
    }

    private fun PreparedStatement.writeWorld(world: String){
        setString(3, world)
    }

    private fun PreparedStatement.writeServer(server: String){
        setString(2, server)
    }

    private fun PreparedStatement.writeNationId(id: Int){
        setInt(4, id)
    }

    private fun PreparedStatement.writeType(type: String){
        setString(5, type)
    }

    private fun ResultSet.readMaximum(): ServerLoc{
        val server = readServer()
        val world = readWorld()
        val x = getInt(9)
        val y = getInt(10)
        val z = getInt(11)
        return ServerLoc(server, world, x, y, z)
    }

    private fun ResultSet.readMinimum(): ServerLoc{
        val server = readServer()
        val world = readWorld()
        val x = getInt(6)
        val y = getInt(7)
        val z = getInt(8)
        return ServerLoc(server, world, x, y, z)
    }

    private fun ResultSet.readCenter(): ServerLoc{
        val server = readServer()
        val world = readWorld()
        val x = getInt(12)
        val y = getInt(13)
        val z = getInt(14)
        return ServerLoc(server, world, x, y, z)
    }

    private fun ResultSet.readServer(): String{
        return getString(2)
    }

    private fun ResultSet.readWorld(): String{
        return getString(3)
    }

    private fun ResultSet.readNationId(): Int{
        return getInt(4)
    }

    private fun ResultSet.readType(): String{
        return getString(5)
    }

}