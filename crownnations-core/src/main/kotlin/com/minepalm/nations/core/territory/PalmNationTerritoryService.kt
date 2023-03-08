package com.minepalm.nations.core.territory

import com.minepalm.library.database.impl.internal.MySQLDB
import com.minepalm.nations.Dependencies
import com.minepalm.nations.NationService
import com.minepalm.nations.config.TerritoryConfiguration
import com.minepalm.nations.core.mysql.MySQLTerritoryLocationDatabase
import com.minepalm.nations.core.mysql.MySQLTerritorySchematicDatabase
import com.minepalm.nations.core.mysql.MySQLTerritoryWarpDatabase
import com.minepalm.nations.core.territory.listener.NationDisbandListener
import com.minepalm.nations.event.NationDisbandEvent
import com.minepalm.nations.territory.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService

class PalmNationTerritoryService(
    override val root: NationService,
    config: TerritoryConfiguration,
    override val modifier: WorldModifier,
    mysqlLocationDatabase: MySQLDB,
    mysqlSchematicDatabase: MySQLDB,
    executor: ExecutorService
) : NationTerritoryService {

    override val universe: NationWorldUniverse
    override val territoryRegistry: NationTerritoryRegistry
    override val policyRegistry: ModifyPolicyRegistry
    override val operationFactory: TerritoryOperationFactory

    private val monumentFactory: MonumentFactory
    private val locationDatabase: MySQLTerritoryLocationDatabase
    private val schematicDatabase: MySQLTerritorySchematicDatabase
    private val warpDatabase: MySQLTerritoryWarpDatabase


    private val warpProvider: TerritoryWarpProvider

    init {
        locationDatabase =
            MySQLTerritoryLocationDatabase(mysqlLocationDatabase, "crownnations_monuments")
        schematicDatabase =
            MySQLTerritorySchematicDatabase(mysqlSchematicDatabase, "crownnations_monument_schematics")
        warpDatabase = MySQLTerritoryWarpDatabase("crownnations_warps", mysqlLocationDatabase)

        monumentFactory = MonumentFactory(this, locationDatabase, schematicDatabase)
        universe = PalmNationWorldUniverse(root.network, this, config.worlds, monumentFactory, locationDatabase)
        territoryRegistry = PalmTerritoryRegistry(this, locationDatabase, executor)
        policyRegistry = ModifyPolicyRegistryImpl()
        operationFactory = TerritoryOperationFactoryImpl(this, config)
        warpProvider = TerritoryWarpProvider(config.warp, warpDatabase)
        Dependencies.register(TerritoryWarpProvider::class.java, warpProvider)

        root.localEventBus.addListener(NationDisbandEvent::class.java, NationDisbandListener(this))
    }

    override fun create(schema: MonumentSchema): CompletableFuture<NationMonument?> {
        val world = universe.host[schema.center.world]

        if (world == null || !world.isLocal)
            return CompletableFuture.completedFuture(null)

        return locationDatabase.createNewMonument(schema).thenApply {
            try {
                if (it.id != -1) {
                    val success = modifier.create(schema).join()
                    val monument = monumentFactory.build(schema)
                    if (success) {
                        world.local.add(monument)
                        monument
                    } else {
                        locationDatabase.deleteMonument(schema.id)
                        null
                    }
                } else
                    null
            } catch (e: Exception) {
                e.printStackTrace()
                locationDatabase.deleteMonument(schema.id)
                null
            }
        }
    }

    override fun shutdown() {
        territoryRegistry.shutdown()
    }
}