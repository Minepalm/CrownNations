package com.minepalm.nations.core

import com.minepalm.nations.NationMetadata
import com.minepalm.nations.core.mysql.MySQLNationDataDatabase
import java.util.concurrent.CompletableFuture

class PalmNationMetadataModifier(
    val nationId : Int,
    val database : MySQLNationDataDatabase
) : NationMetadata.Modifier {
    override fun set(key: String, value: String?): CompletableFuture<Unit> {
        if(value != null)
            return database.setProperty(nationId, key, value)
        else
            return database.removeProperty(nationId, key)
    }

    override fun get(key: String): CompletableFuture<String?> {
        return database.getProperty(nationId, key)
    }

    override fun remove(key: String): CompletableFuture<Unit> {
        return database.removeProperty(nationId, key)
    }

    override fun getAll(): CompletableFuture<Map<String, String>> {
        return database.getProperties(nationId)
    }
}