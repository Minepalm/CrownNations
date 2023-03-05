package com.minepalm.nations.territory

import com.minepalm.nations.utils.ServerLoc
import java.util.concurrent.CompletableFuture

interface WorldModifier {

    fun serialize(schema: MonumentSchema): CompletableFuture<MonumentBlob>

    fun paste(
        monument: NationMonument,
        blob: MonumentBlob
    ): CompletableFuture<Unit>

    fun create(schema: MonumentSchema): CompletableFuture<Boolean>

    fun delete(min: ServerLoc, max: ServerLoc): CompletableFuture<Unit>

    fun delete(monumentType: String, center: ServerLoc): CompletableFuture<Unit>

    fun shutdown()
}