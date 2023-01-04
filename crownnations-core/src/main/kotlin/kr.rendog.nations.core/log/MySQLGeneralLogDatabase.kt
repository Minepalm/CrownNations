package kr.rendog.nations.core.log

import com.minepalm.library.database.impl.internal.MySQLDB

class MySQLGeneralLogDatabase(
    val table: String,
    val database: MySQLDB
) {

    init{
        database.execute { connection ->
            connection.prepareStatement("CREATE TABLE IF NOT EXISTS $table ( " +
                    "`log_id` BIGINT UNIQUE AUTO_INCREMENTS, " +
                    "`type` VARCHAR(32), " +
                    "`commander` VARCHAR(36), " +
                    "`nation_id` BIGINT, " +
                    "`message` TEXT, " +
                    "`time` LONG, " +
                    "PRIMARY KEY (`log_id`) " +
                    ") charset=utf8mb4")
        }
    }

}