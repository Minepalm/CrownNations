package com.minepalm.nations.core.territory

import com.minepalm.nations.Dependencies
import com.minepalm.nations.NationMember
import com.minepalm.nations.NationOperation
import com.minepalm.nations.territory.NationCastle
import com.minepalm.nations.territory.NationMonument
import com.minepalm.nations.utils.ServerLoc
import java.util.concurrent.CompletableFuture

class PalmNationCastle(
    controller: NationMonument
) : MonumentWrapper(controller), NationCastle {

    private val provider
        get() = Dependencies[TerritoryWarpProvider::class.java].get()

    override fun getBeaconLocation(): ServerLoc {
        return center
    }

    override fun getWarpLocation(): CompletableFuture<ServerLoc> {
        return provider.getWarpLocation(this)
    }

    override fun forceSetWarpLocation(loc: ServerLoc): CompletableFuture<Unit> {
        return provider.forceSetWarpLocation(this, loc)
    }

    override fun operateSetWarpLocation(commander: NationMember, loc: ServerLoc): NationOperation<Boolean> {
        return provider.operateSetWarpLocation(this, commander, loc)
    }

}