package com.minepalm.nations.core.territory

import com.minepalm.nations.core.mysql.MySQLTerritoryLocationDatabase
import com.minepalm.nations.core.mysql.MySQLTerritorySchematicDatabase
import com.minepalm.nations.core.territory.hellobungee.RequestMonumentCollapse
import com.minepalm.nations.core.territory.hellobungee.RequestMonumentLoad
import com.minepalm.nations.core.territory.hellobungee.RequestMonumentSave
import java.util.concurrent.CompletableFuture

sealed class PalmNationMonument {
    open class Local(
        id: Int,
        type: String,
        ownerId: Int,
        center: com.minepalm.nations.utils.ServerLoc,
        range: com.minepalm.nations.territory.ProtectionRange,
        service: com.minepalm.nations.territory.NationTerritoryService,
        private val database: MySQLTerritoryLocationDatabase,
        private val schematicDatabase: MySQLTerritorySchematicDatabase
    ) : AbstractNationMonument(id, type, ownerId, center, range, service){

        override val isLocal: Boolean = true

        constructor(schema: com.minepalm.nations.territory.MonumentSchema,
                    service: com.minepalm.nations.territory.NationTerritoryService,
                    database: MySQLTerritoryLocationDatabase,
                    schematicDatabase: MySQLTerritorySchematicDatabase
        )
                : this(schema.id, schema.type, schema.nationId, schema.center, schema.range,
            service, database, schematicDatabase)

        override fun toData(): CompletableFuture<com.minepalm.nations.territory.MonumentBlob> {
            return service.modifier.serialize(
                com.minepalm.nations.territory.MonumentSchema(
                    id,
                    nationId,
                    type,
                    center,
                    range
                )
            )
        }

        override fun save(): CompletableFuture<Unit> {
            return service.modifier.serialize(toSchema()).thenCompose { schematicDatabase.saveAsync(it) }
        }

        override fun load(): CompletableFuture<Unit> {
            return CompletableFuture.completedFuture(null)
        }

        override fun collapse(): CompletableFuture<Boolean> {
            return world.local.delete(id).thenApply {
                it.also { success ->
                    if(success)
                        service.modifier.delete(range.minimumLocation, range.maximumLocation)
                }
            }
        }

    }

    open class Remote(
        id: Int,
        type: String,
        ownerId: Int,
        center: com.minepalm.nations.utils.ServerLoc,
        range: com.minepalm.nations.territory.ProtectionRange,
        service: com.minepalm.nations.territory.NationTerritoryService,
        private val database: MySQLTerritoryLocationDatabase,
        private val schematicDatabase: MySQLTerritorySchematicDatabase
    ) : AbstractNationMonument(id, type, ownerId, center, range, service){

        override val isLocal: Boolean = false

        constructor(schema: com.minepalm.nations.territory.MonumentSchema,
                    service: com.minepalm.nations.territory.NationTerritoryService,
                    database: MySQLTerritoryLocationDatabase,
                    schematicDatabase: MySQLTerritorySchematicDatabase
        )
                : this(schema.id, schema.type, schema.nationId, schema.center, schema.range,
            service, database, schematicDatabase)

        override fun toData(): CompletableFuture<com.minepalm.nations.territory.MonumentBlob> {
            return world.server
                .callback(RequestMonumentSave(id), Boolean::class.java)
                .thenCompose { schematicDatabase.loadAsync(id) }
        }

        override fun save(): CompletableFuture<Unit> {
            return world.server.callback(RequestMonumentSave(id), Boolean::class.java).thenApply {  }
        }

        override fun load(): CompletableFuture<Unit> {
            return world.server.callback(RequestMonumentLoad(id), Boolean::class.java).thenApply {  }
        }

        override fun collapse(): CompletableFuture<Boolean> {
            return world.server.callback(RequestMonumentCollapse(id), Boolean::class.java)
        }

    }
}