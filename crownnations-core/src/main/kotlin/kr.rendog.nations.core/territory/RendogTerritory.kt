package kr.rendog.nations.core.territory

import kr.rendog.nations.Nation
import kr.rendog.nations.NationMember
import kr.rendog.nations.NationOperation
import kr.rendog.nations.core.mysql.MySQLTerritoryLocationDatabase
import kr.rendog.nations.territory.*
import kr.rendog.nations.utils.ServerLoc
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService

class RendogTerritory(
    private val nationId: Int,
    private val service: NationTerritoryService,
    database: MySQLTerritoryLocationDatabase,
    threadPool: ExecutorService
): NationTerritory {

    override val nation: Nation
        get() = service.root.nationRegistry[nationId]!!

    override val direct: NationTerritory.Direct = DirectImpl(this, service, database, threadPool)
    override val local: NationTerritory.Local = LocalImpl(this, service)

    class LocalImpl(
        private val parent: NationTerritory,
        private val service: NationTerritoryService
    ) : NationTerritory.Local{

        override fun getMonuments(): List<NationMonument> {
            return mutableListOf<NationMonument>().apply {
                service.universe.host.getWorlds().forEach { world ->
                    world[parent.nation].forEach { add(it) }
                }
            }
        }

        override fun getCastles(): List<NationCastle> {
            return getMonuments().filterIsInstance<NationCastle>()
        }

        override fun getCastle(monumentId: Int): NationCastle? {
            return try {
                getCastles().first { it.id == monumentId }
            }catch (_: NoSuchElementException){
                null
            }
        }

        override fun getOutposts(): List<NationOutpost> {
            return getMonuments().filterIsInstance<NationOutpost>()
        }

        override fun getOutpost(monumentId: Int): NationOutpost? {
            return try {
                getOutposts().first { it.id == monumentId }
            }catch (_: NoSuchElementException){
                null
            }
        }

        override fun operateNewCastle(commander: NationMember, loc: ServerLoc): NationOperation<NationCastle> {
            return service.operationFactory.buildOperateClaimCastle(parent, commander, loc)
        }

        override fun operateNewOutpost(commander: NationMember, loc: ServerLoc): NationOperation<NationOutpost> {
            return service.operationFactory.buildOperateClaimOutpost(parent, commander, loc)
        }

        override fun operateDisbandCastle(commander: NationMember, castle: NationCastle, reason: String)
        : NationOperation<Boolean> {
            return service.operationFactory.buildOperationDecomposeCastle(castle, commander, reason)
        }

        override fun operateDisbandOutpost(commander: NationMember, outpost: NationOutpost, reason: String)
        : NationOperation<Boolean> {
            return service.operationFactory.buildOperationDecomposeOutpost(outpost, commander, reason)
        }

    }

    class DirectImpl(
        private val parent: NationTerritory,
        private val service: NationTerritoryService,
        private val database: MySQLTerritoryLocationDatabase,
        private val executors: ExecutorService
    ) : NationTerritory.Direct{

        override fun getMonuments(): CompletableFuture<List<NationMonument>> {
            return database.getNationMonuments(parent.nation.id).thenApplyAsync( {schemaList ->
                schemaList.mapNotNull { schema ->
                    val world = service.universe.cache.getWorld(schema.center.server, schema.center.world)
                    world[schema.id]
                }
            }, executors)
        }

        override fun getCastles(): CompletableFuture<List<NationCastle>> {
            return getMonuments().thenApply { it.filterIsInstance<NationCastle>() }
        }

        override fun getCastlesCount(): CompletableFuture<Int> {
            return database.getNationMonuments(parent.nation.id).thenApply { it.count { schema -> schema.type == "CASTLE" } }
        }

        override fun getOutpostCount(): CompletableFuture<Int> {
            return database.getNationMonuments(parent.nation.id).thenApply { it.count { schema -> schema.type == "OUTPOST" } }
        }

        override fun getDefaultCastle(): CompletableFuture<NationCastle?> {
            //todo: 상의하기
            throw UnsupportedOperationException("not support")
        }

        override fun getOutposts(): CompletableFuture<List<NationOutpost>> {
            return getMonuments().thenApply { it.filterIsInstance<NationOutpost>() }
        }

    }
}