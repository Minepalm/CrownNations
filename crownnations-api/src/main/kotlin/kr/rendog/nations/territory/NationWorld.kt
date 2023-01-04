package kr.rendog.nations.territory

import kr.rendog.nations.Nation
import kr.rendog.nations.server.NationServer
import kr.rendog.nations.utils.ServerLoc
import java.util.concurrent.CompletableFuture

interface NationWorld {

    val server : NationServer
    val name : String
    val local: Local
    val remote: Remote
    val isLocal: Boolean

    operator fun get(id: Int): NationMonument?

    operator fun get(nation: Nation): List<NationMonument>

    interface Local{

        fun exists(monumentId: Int): Boolean

        fun nearest(type: String, loc: ServerLoc): Double?

        fun getMonuments() : List<NationMonument>

        fun add(monument: NationMonument)

        fun remove(monumentId: Int)

        operator fun get(loc: ServerLoc): NationMonument?

        operator fun get(id: Int): NationMonument?

        operator fun get(nation: Nation): List<NationMonument>

        fun create(schema: MonumentSchema): CompletableFuture<Boolean>

        fun delete(monumentId: Int): CompletableFuture<Boolean>

        fun deleteInvalidatedMonuments(): List<NationMonument>

    }

    interface Remote{

        operator fun get(id: Int): CompletableFuture<NationMonument?>

        operator fun get(nation: Nation): CompletableFuture<List<NationMonument>>

        fun exists(monumentId: Int): CompletableFuture<Boolean>

        fun getMonuments(): CompletableFuture<List<NationMonument>>

        fun loadAll(): CompletableFuture<List<MonumentSchema>>

        fun update(id: Int)

    }

}