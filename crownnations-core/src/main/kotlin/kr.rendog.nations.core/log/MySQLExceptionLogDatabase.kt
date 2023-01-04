package kr.rendog.nations.core.log

import com.minepalm.library.database.impl.internal.MySQLDB

class MySQLExceptionLogDatabase(
    val table: String,
    val database: MySQLDB
) {

    init{
        database.execute { connection ->
            connection.prepareStatement("CREATE TABLE IF NOT EXISTS $table ( " +
                    "`log_id` BIGINT, " +
                    "`error` VARCHAR(64), " +
                    "`cause` TEXT, "+
                    "`message` TEXT, " +
                    "`stacktrace` TEXT, " +
                    "PRIMARY KEY (`log_id`), " +
                    ") charset=utf8mb4")
        }
    }

}