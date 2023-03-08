package com.minepalm.nations.territory

import com.minepalm.nations.NationMember
import com.minepalm.nations.NationOperation
import com.minepalm.nations.utils.ServerLoc
import java.util.concurrent.CompletableFuture

interface WarpMonument: NationMonument {

    fun getWarpLocation(): CompletableFuture<ServerLoc>

    fun forceSetWarpLocation(loc: ServerLoc): CompletableFuture<Unit>

    fun operateSetWarpLocation(commander: NationMember, loc: ServerLoc): NationOperation<Boolean>

}