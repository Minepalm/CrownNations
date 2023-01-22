package com.minepalm.nations.bungee

import java.util.concurrent.CompletableFuture

class BungneeWorldModifier : com.minepalm.nations.territory.WorldModifier {
    override fun serialize(schema: com.minepalm.nations.territory.MonumentSchema): CompletableFuture<com.minepalm.nations.territory.MonumentBlob> {
        return CompletableFuture.completedFuture(null)
    }

    override fun paste(monument: com.minepalm.nations.territory.NationMonument, blob: com.minepalm.nations.territory.MonumentBlob): CompletableFuture<Unit> {
        return CompletableFuture.completedFuture(Unit)
    }

    override fun create(schema: com.minepalm.nations.territory.MonumentSchema): CompletableFuture<Boolean> {
        return CompletableFuture.completedFuture(false)
    }

    override fun delete(min: com.minepalm.nations.utils.ServerLoc, max: com.minepalm.nations.utils.ServerLoc): CompletableFuture<Unit> {
        return CompletableFuture.completedFuture(Unit)
    }

    override fun shutdown() {

    }
}