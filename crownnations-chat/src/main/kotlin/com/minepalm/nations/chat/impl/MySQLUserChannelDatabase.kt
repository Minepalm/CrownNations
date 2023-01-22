package com.minepalm.nations.chat.impl

import com.minepalm.library.database.impl.internal.MySQLDB
import com.minepalm.palmchat.api.ChatType
import java.util.*
import java.util.concurrent.CompletableFuture

class MySQLUserChannelDatabase(
    private val database: MySQLDB,
    private val table: String
) {

    init{
        database.execute { connection ->
            connection.prepareStatement("CREATE TABLE IF NOT EXISTS $table ( " +
                    "`uuid` VARCHAR(36), " +
                    "`channel` VARCHAR(16), " +
                    "PRIMARY KEY (`uuid`))" +
                    "charset=utf8mb4")
                .execute()
        }
    }

    fun setChatChannel(uuid: UUID, channelType: ChatType): CompletableFuture<Unit> {
        return database.executeAsync { connection ->
            connection.prepareStatement("INSERT INTO $table (`uuid`, `channel`) VALUES(?, ?) " +
                    "ON DUPLICATE KEY UPDATE `channel`=VALUES(?)")
                .apply {
                    setString(1, uuid.toString())
                    setString(2, channelType.name)
                }.execute()
        }
    }

    fun getChatChannel(uuid: UUID): CompletableFuture<ChatType> {
        return database.executeAsync { connection ->
            connection.prepareStatement("SELECT `channel` FROM $table WHERE `uuid`=?")
                .apply { setString(1, uuid.toString()) }
                .executeQuery()
                .let {
                    if(it.next()){
                        ChatTokens.find(it.getString(1))
                    }else{
                        ChatTokens.global
                    }
                }
        }
    }

}