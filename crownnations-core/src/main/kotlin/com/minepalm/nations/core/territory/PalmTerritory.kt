package com.minepalm.nations.core.territory

import com.minepalm.nations.Nation
import com.minepalm.nations.NationMember
import com.minepalm.nations.NationOperation
import com.minepalm.nations.core.mysql.MySQLTerritoryLocationDatabase
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService

class PalmTerritory(
    private val nationId: Int,
    private val service: com.minepalm.nations.territory.NationTerritoryService,
    database: MySQLTerritoryLocationDatabase,
    threadPool: ExecutorService
): com.minepalm.nations.territory.NationTerritory {

    override val nation: Nation
        get() = service.root.nationRegistry[nationId]!!

    override val direct: com.minepalm.nations.territory.NationTerritory.Direct = DirectImpl(this, service, database, threadPool)
    override val local: com.minepalm.nations.territory.NationTerritory.Local = LocalImpl(this, service)

    class LocalImpl(
        private val parent: com.minepalm.nations.territory.NationTerritory,
        private val service: com.minepalm.nations.territory.NationTerritoryService
    ) : com.minepalm.nations.territory.NationTerritory.Local{

        override fun getMonuments(): List<com.minepalm.nations.territory.NationMonument> {
            return mutableListOf<com.minepalm.nations.territory.NationMonument>().apply {
                service.universe.host.getWorlds().forEach { world ->
                    world[parent.nation].forEach { add(it) }
                }
            }
        }

        override fun getCastles(): List<com.minepalm.nations.territory.NationCastle> {
            return getMonuments().filterIsInstance<com.minepalm.nations.territory.NationCastle>()
        }

        override fun getCastle(monumentId: Int): com.minepalm.nations.territory.NationCastle? {
            return try {
                getCastles().first { it.id == monumentId }
            }catch (_: NoSuchElementException){
                null
            }
        }

        override fun getOutposts(): List<com.minepalm.nations.territory.NationOutpost> {
            return getMonuments().filterIsInstance<com.minepalm.nations.territory.NationOutpost>()
        }

        override fun getOutpost(monumentId: Int): com.minepalm.nations.territory.NationOutpost? {
            return try {
                getOutposts().first { it.id == monumentId }
            }catch (_: NoSuchElementException){
                null
            }
        }

        override fun operateNewCastle(commander: NationMember, loc: com.minepalm.nations.utils.ServerLoc): NationOperation<com.minepalm.nations.territory.NationCastle> {
            return service.operationFactory.buildOperateClaimCastle(parent, commander, loc)
        }

        override fun operateNewOutpost(commander: NationMember, loc: com.minepalm.nations.utils.ServerLoc): NationOperation<com.minepalm.nations.territory.NationOutpost> {
            return service.operationFactory.buildOperateClaimOutpost(parent, commander, loc)
        }

        override fun operateDisbandCastle(commander: NationMember, castle: com.minepalm.nations.territory.NationCastle, reason: String)
        : NationOperation<Boolean> {
            return service.operationFactory.buildOperationDecomposeCastle(castle, commander, reason)
        }

        override fun operateDisbandOutpost(commander: NationMember, outpost: com.minepalm.nations.territory.NationOutpost, reason: String)
        : NationOperation<Boolean> {
            return service.operationFactory.buildOperationDecomposeOutpost(outpost, commander, reason)
        }

    }

    class DirectImpl(
        private val parent: com.minepalm.nations.territory.NationTerritory,
        private val service: com.minepalm.nations.territory.NationTerritoryService,
        private val database: MySQLTerritoryLocationDatabase,
        private val executors: ExecutorService
    ) : com.minepalm.nations.territory.NationTerritory.Direct{

        override fun getMonuments(): CompletableFuture<List<com.minepalm.nations.territory.NationMonument>> {
            return database.getNationMonuments(parent.nation.id).thenApplyAsync( {schemaList ->
                schemaList.mapNotNull { schema ->
                    val world = service.universe.cache.getWorld(schema.center.server, schema.center.world)
                    world[schema.id]
                }
            }, executors)
        }

        override fun getCastles(): CompletableFuture<List<com.minepalm.nations.territory.NationCastle>> {
            return getMonuments().thenApply { it.filterIsInstance<com.minepalm.nations.territory.NationCastle>() }
        }

        override fun getCastlesCount(): CompletableFuture<Int> {
            return database.getNationMonuments(parent.nation.id).thenApply { it.count { schema -> schema.type == "CASTLE" } }
        }

        override fun getOutpostCount(): CompletableFuture<Int> {
            return database.getNationMonuments(parent.nation.id).thenApply { it.count { schema -> schema.type == "OUTPOST" } }
        }

        override fun getDefaultCastle(): CompletableFuture<com.minepalm.nations.territory.NationCastle?> {
            //todo: 상의하기
            throw UnsupportedOperationException("not support")
        }

        override fun getOutposts(): CompletableFuture<List<com.minepalm.nations.territory.NationOutpost>> {
            return getMonuments().thenApply { it.filterIsInstance<com.minepalm.nations.territory.NationOutpost>() }
        }

    }
}