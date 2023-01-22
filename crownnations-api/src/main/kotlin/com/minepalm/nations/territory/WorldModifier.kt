package com.minepalm.nations.territory

import java.util.concurrent.CompletableFuture

interface WorldModifier {

    fun serialize(schema: MonumentSchema): CompletableFuture<MonumentBlob>

    fun paste(
        monument: NationMonument,
        blob: MonumentBlob
    ): CompletableFuture<Unit>

    fun create(schema: MonumentSchema): CompletableFuture<Boolean>

    fun delete(
        min: com.minepalm.nations.utils.ServerLoc,
        max: com.minepalm.nations.utils.ServerLoc
    ): CompletableFuture<Unit>

    fun shutdown()
}