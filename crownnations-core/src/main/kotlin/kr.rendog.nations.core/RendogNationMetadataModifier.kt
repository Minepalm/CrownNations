package kr.rendog.nations.core

import kr.rendog.nations.NationMetadata
import kr.rendog.nations.core.mysql.MySQLNationDataDatabase
import java.util.concurrent.CompletableFuture

class RendogNationMetadataModifier(
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