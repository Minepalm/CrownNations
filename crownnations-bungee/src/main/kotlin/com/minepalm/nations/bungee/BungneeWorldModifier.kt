package com.minepalm.nations.bungee

import com.minepalm.nations.territory.MonumentBlob
import com.minepalm.nations.territory.MonumentSchema
import com.minepalm.nations.territory.NationMonument
import com.minepalm.nations.territory.WorldModifier
import com.minepalm.nations.utils.ServerLoc
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

    override fun delete(monumentType: String, center: ServerLoc): CompletableFuture<Unit> {
        return CompletableFuture.completedFuture(Unit)
    }

    override fun shutdown() {

    }
}