package com.minepalm.nations.core.territory

import com.minepalm.nations.core.mysql.MySQLTerritoryLocationDatabase
import com.minepalm.nations.core.mysql.MySQLTerritorySchematicDatabase

class MonumentFactory(
    private val service: com.minepalm.nations.territory.NationTerritoryService,
    private val database: MySQLTerritoryLocationDatabase,
    private val schematicDatabase: MySQLTerritorySchematicDatabase
) {

    private val currentServer = service.root.network.host

    fun build(schema: com.minepalm.nations.territory.MonumentSchema): com.minepalm.nations.territory.NationMonument {
        val controller =
            if(schema.center.server == currentServer.name)
                buildControllerLocal(schema)
            else
                buildControllerRemote(schema)
        return when(schema.type){
            "CASTLE" -> buildCastle(controller)
            "OUTPOST" -> buildOutpost(controller)
            else -> controller
        }
    }

    private fun buildCastle(controller: com.minepalm.nations.territory.NationMonument): com.minepalm.nations.territory.NationCastle {
        return PalmNationCastle(controller)
    }

    private fun buildOutpost(controller: com.minepalm.nations.territory.NationMonument): com.minepalm.nations.territory.NationOutpost {
        return PalmNationOutpost(controller)
    }

    private fun buildControllerLocal(schema: com.minepalm.nations.territory.MonumentSchema): com.minepalm.nations.territory.NationMonument {
        return PalmNationMonument.Local(schema, service, database, schematicDatabase)
    }

    private fun buildControllerRemote(schema: com.minepalm.nations.territory.MonumentSchema): com.minepalm.nations.territory.NationMonument {
        return PalmNationMonument.Remote(schema, service, database, schematicDatabase)
    }
}