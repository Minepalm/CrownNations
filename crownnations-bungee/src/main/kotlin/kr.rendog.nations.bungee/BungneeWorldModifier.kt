package kr.rendog.nations.bungee

import kr.rendog.nations.territory.MonumentBlob
import kr.rendog.nations.territory.MonumentSchema
import kr.rendog.nations.territory.NationMonument
import kr.rendog.nations.territory.WorldModifier
import kr.rendog.nations.utils.ServerLoc
import java.util.concurrent.CompletableFuture

class BungneeWorldModifier : WorldModifier {
    override fun serialize(schema: MonumentSchema): CompletableFuture<MonumentBlob> {
        return CompletableFuture.completedFuture(null)
    }

    override fun paste(monument: NationMonument, blob: MonumentBlob): CompletableFuture<Unit> {
        return CompletableFuture.completedFuture(Unit)
    }

    override fun create(schema: MonumentSchema): CompletableFuture<Boolean> {
        return CompletableFuture.completedFuture(false)
    }

    override fun delete(min: ServerLoc, max: ServerLoc): CompletableFuture<Unit> {
        return CompletableFuture.completedFuture(Unit)
    }

    override fun shutdown() {

    }
}