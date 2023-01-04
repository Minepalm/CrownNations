package kr.rendog.nations.core.territory

import com.minepalm.library.database.impl.internal.MySQLDB
import kr.rendog.nations.NationService
import kr.rendog.nations.config.TerritoryConfiguration
import kr.rendog.nations.core.mysql.MySQLTerritoryLocationDatabase
import kr.rendog.nations.core.mysql.MySQLTerritorySchematicDatabase
import kr.rendog.nations.territory.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService

class RendogNationTerritoryService(
    override val root: NationService,
    config: TerritoryConfiguration,
    override val modifier: WorldModifier,
    mysqlLocationDatabase: MySQLDB,
    mysqlSchematicDatabase: MySQLDB,
    executor: ExecutorService
): NationTerritoryService {

    override val universe: NationWorldUniverse
    override val territoryRegistry: NationTerritoryRegistry
    override val policyRegistry: ModifyPolicyRegistry
    override val operationFactory: TerritoryOperationFactory

    private val monumentFactory: MonumentFactory
    private val locationDatabase: MySQLTerritoryLocationDatabase
    private val schematicDatabase: MySQLTerritorySchematicDatabase

    init {
        locationDatabase =
            MySQLTerritoryLocationDatabase(mysqlLocationDatabase, "rendognations_monuments")
        schematicDatabase =
            MySQLTerritorySchematicDatabase(mysqlSchematicDatabase, "rendognations_monument_schematics")
        monumentFactory = MonumentFactory(this, locationDatabase, schematicDatabase)
        universe = RendogNationWorldUniverse(root.network, this, config.worlds, monumentFactory, locationDatabase)
        territoryRegistry = RendogTerritoryRegistry(this, locationDatabase, executor)
        policyRegistry = ModifyPolicyRegistryImpl()
        operationFactory = TerritoryOperationFactoryImpl(this, config)
    }

    override fun create(schema: MonumentSchema): CompletableFuture<NationMonument?> {
        return locationDatabase.createNewMonument(schema).thenApply {
            if(it.id != -1) {
                val success = modifier.create(schema).join()
                monumentFactory.build(schema)
            }else
                null
        }
    }

    override fun shutdown() {
        territoryRegistry.shutdown()
    }
}