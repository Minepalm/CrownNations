package com.minepalm.nations.core.territory

import com.minepalm.library.database.impl.internal.MySQLDB
import com.minepalm.nations.NationService
import com.minepalm.nations.core.mysql.MySQLTerritoryLocationDatabase
import com.minepalm.nations.core.mysql.MySQLTerritorySchematicDatabase
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService

class PalmNationTerritoryService(
    override val root: NationService,
    config: com.minepalm.nations.config.TerritoryConfiguration,
    override val modifier: com.minepalm.nations.territory.WorldModifier,
    mysqlLocationDatabase: MySQLDB,
    mysqlSchematicDatabase: MySQLDB,
    executor: ExecutorService
): com.minepalm.nations.territory.NationTerritoryService {

    override val universe: com.minepalm.nations.territory.NationWorldUniverse
    override val territoryRegistry: com.minepalm.nations.territory.NationTerritoryRegistry
    override val policyRegistry: com.minepalm.nations.territory.ModifyPolicyRegistry
    override val operationFactory: com.minepalm.nations.territory.TerritoryOperationFactory

    private val monumentFactory: MonumentFactory
    private val locationDatabase: MySQLTerritoryLocationDatabase
    private val schematicDatabase: MySQLTerritorySchematicDatabase

    init {
        locationDatabase =
            MySQLTerritoryLocationDatabase(mysqlLocationDatabase, "rendognations_monuments")
        schematicDatabase =
            MySQLTerritorySchematicDatabase(mysqlSchematicDatabase, "rendognations_monument_schematics")
        monumentFactory = MonumentFactory(this, locationDatabase, schematicDatabase)
        universe = PalmNationWorldUniverse(root.network, this, config.worlds, monumentFactory, locationDatabase)
        territoryRegistry = PalmTerritoryRegistry(this, locationDatabase, executor)
        policyRegistry = ModifyPolicyRegistryImpl()
        operationFactory = TerritoryOperationFactoryImpl(this, config)
    }

    override fun create(schema: com.minepalm.nations.territory.MonumentSchema): CompletableFuture<com.minepalm.nations.territory.NationMonument?> {
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