package kr.rendog.nations.core.territory

import kr.rendog.nations.core.mysql.MySQLTerritoryLocationDatabase
import kr.rendog.nations.core.mysql.MySQLTerritorySchematicDatabase
import kr.rendog.nations.territory.*

class MonumentFactory(
    private val service: NationTerritoryService,
    private val database: MySQLTerritoryLocationDatabase,
    private val schematicDatabase: MySQLTerritorySchematicDatabase
) {

    private val currentServer = service.root.network.host

    fun build(schema: MonumentSchema): NationMonument {
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

    private fun buildCastle(controller: NationMonument): NationCastle {
        return RendogNationCastle(controller)
    }

    private fun buildOutpost(controller: NationMonument): NationOutpost {
        return RendogNationOutpost(controller)
    }

    private fun buildControllerLocal(schema: MonumentSchema): NationMonument {
        return RendogNationMonument.Local(schema, service, database, schematicDatabase)
    }

    private fun buildControllerRemote(schema: MonumentSchema): NationMonument {
        return RendogNationMonument.Remote(schema, service, database, schematicDatabase)
    }
}