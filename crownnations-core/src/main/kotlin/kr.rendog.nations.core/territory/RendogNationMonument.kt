package kr.rendog.nations.core.territory

import kr.rendog.nations.core.mysql.MySQLTerritoryLocationDatabase
import kr.rendog.nations.core.mysql.MySQLTerritorySchematicDatabase
import kr.rendog.nations.core.territory.hellobungee.RequestMonumentCollapse
import kr.rendog.nations.core.territory.hellobungee.RequestMonumentLoad
import kr.rendog.nations.core.territory.hellobungee.RequestMonumentSave
import kr.rendog.nations.territory.MonumentBlob
import kr.rendog.nations.territory.MonumentSchema
import kr.rendog.nations.territory.NationTerritoryService
import kr.rendog.nations.territory.ProtectionRange
import kr.rendog.nations.utils.ServerLoc
import java.util.concurrent.CompletableFuture

sealed class RendogNationMonument {
    open class Local(
        id: Int,
        type: String,
        ownerId: Int,
        center: ServerLoc,
        range: ProtectionRange,
        service: NationTerritoryService,
        private val database: MySQLTerritoryLocationDatabase,
        private val schematicDatabase: MySQLTerritorySchematicDatabase
    ) : AbstractNationMonument(id, type, ownerId, center, range, service){

        override val isLocal: Boolean = true

        constructor(schema: MonumentSchema,
                    service: NationTerritoryService,
                    database: MySQLTerritoryLocationDatabase,
                    schematicDatabase: MySQLTerritorySchematicDatabase
        )
                : this(schema.id, schema.type, schema.nationId, schema.center, schema.range,
            service, database, schematicDatabase)

        override fun toData(): CompletableFuture<MonumentBlob> {
            return service.modifier.serialize(MonumentSchema(id, nationId, type, center, range))
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
        center: ServerLoc,
        range: ProtectionRange,
        service: NationTerritoryService,
        private val database: MySQLTerritoryLocationDatabase,
        private val schematicDatabase: MySQLTerritorySchematicDatabase
    ) : AbstractNationMonument(id, type, ownerId, center, range, service){

        override val isLocal: Boolean = false

        constructor(schema: MonumentSchema,
                    service: NationTerritoryService,
                    database: MySQLTerritoryLocationDatabase,
                    schematicDatabase: MySQLTerritorySchematicDatabase
        )
                : this(schema.id, schema.type, schema.nationId, schema.center, schema.range,
            service, database, schematicDatabase)

        override fun toData(): CompletableFuture<MonumentBlob> {
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