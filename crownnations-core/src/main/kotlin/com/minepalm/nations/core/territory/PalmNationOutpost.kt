package com.minepalm.nations.core.territory

import com.minepalm.nations.NationMember
import com.minepalm.nations.NationOperation
import com.minepalm.nations.territory.NationMonument
import com.minepalm.nations.territory.NationOutpost
import com.minepalm.nations.utils.ServerLoc
import java.util.concurrent.CompletableFuture

class PalmNationOutpost(
    controller: NationMonument
) : MonumentWrapper(controller), NationOutpost {
    override fun getWarpLocation(): CompletableFuture<ServerLoc> {
        return CompletableFuture.completedFuture(center.add(0, 1, 0))
    }

    override fun forceSetWarpLocation(loc: ServerLoc): CompletableFuture<Unit> {
        throw IllegalArgumentException("outpost does not support set warp location")
    }

    override fun operateSetWarpLocation(commander: NationMember, loc: ServerLoc): NationOperation<Boolean> {
        throw IllegalArgumentException("outpost does not support set warp location")
    }
}