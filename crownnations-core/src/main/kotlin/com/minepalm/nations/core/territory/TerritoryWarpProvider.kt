package com.minepalm.nations.core.territory

import com.minepalm.nations.Dependencies
import com.minepalm.nations.NationMember
import com.minepalm.nations.NationOperation
import com.minepalm.nations.NationService
import com.minepalm.nations.config.WarpConfiguration
import com.minepalm.nations.core.mysql.MySQLTerritoryWarpDatabase
import com.minepalm.nations.core.operation.OperationSetWarp
import com.minepalm.nations.territory.NationMonument
import com.minepalm.nations.territory.WarpMonument
import com.minepalm.nations.utils.ServerLoc
import java.util.concurrent.CompletableFuture

class TerritoryWarpProvider(
    private val config: WarpConfiguration,
    private val database: MySQLTerritoryWarpDatabase
) {
    private val root: NationService
        get() = Dependencies[NationService::class.java].get()

    fun getWarpLocation(monument: WarpMonument): CompletableFuture<ServerLoc> {
        return database.getWarpLocation(monument.id).thenApply {
            it ?: config.getDefaultMonumentOffset(monument.type).let { offset ->
                monument.center.add(offset.x, offset.y, offset.z)
            }
        }
    }

    fun forceSetWarpLocation(monument: WarpMonument, loc: ServerLoc): CompletableFuture<Unit> {
        return database.setWarpLocation(monument.id, loc)
    }

    fun operateSetWarpLocation(
        monument: WarpMonument,
        commander: NationMember,
        loc: ServerLoc
    ): NationOperation<Boolean> {
        return OperationSetWarp(monument, commander, loc, root)
    }
}