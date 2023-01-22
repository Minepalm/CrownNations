package com.minepalm.nations.core.log

import com.minepalm.library.database.impl.internal.MySQLDB

class MySQLGradeLogDatabase(
    val table: String,
    val database: MySQLDB
) {

    init {
        database.execute { connection ->
            connection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS $table ( " +
                        "`log_id` BIGINT, " +
                        "`amount` DOUBLE, " +
                        "`before` DOUBLE, " +
                        "`after` DOUBLE, " +
                        "PRIMARY KEY (`log_id`), " +
                        "FOREIGN KEY `log_id` REFERENCES `crownnations_general_log`(`log_id`)" +
                        ") charset=utf8mb4"
            )
        }
    }


}