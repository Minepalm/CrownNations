package com.minepalm.nations.territory

import com.minepalm.nations.utils.ServerLoc

data class ProtectionRange(
    var minimumLocation: ServerLoc,
    var maximumLocation: ServerLoc
) {

    init {
        val minX = minimumLocation.x.coerceAtMost(maximumLocation.x)
        val minY = minimumLocation.y.coerceAtMost(maximumLocation.y)
        val minZ = minimumLocation.z.coerceAtMost(maximumLocation.z)
        val maxX = minimumLocation.x.coerceAtLeast(maximumLocation.x)
        val maxY = minimumLocation.y.coerceAtLeast(maximumLocation.y)
        val maxZ = minimumLocation.z.coerceAtLeast(maximumLocation.z)
        minimumLocation = ServerLoc(minimumLocation.server, minimumLocation.world, minX, minY, minZ)
        maximumLocation = ServerLoc(maximumLocation.server, maximumLocation.world, maxX, maxY, maxZ)
    }

    fun isIn(loc: ServerLoc): Boolean {
        if (minimumLocation.server == loc.server && minimumLocation.world == loc.world) {
            return loc.x in minimumLocation.x..maximumLocation.x &&
                    loc.y in minimumLocation.y..maximumLocation.y &&
                    loc.z in minimumLocation.z..maximumLocation.z
        }
        return false
    }

    fun center(): ServerLoc {
        return ServerLoc(
            minimumLocation.server,
            minimumLocation.world,
            (minimumLocation.x + maximumLocation.x) / 2,
            (minimumLocation.y + maximumLocation.y) / 2,
            (minimumLocation.z + maximumLocation.z) / 2
        )
    }

    fun center(y: Int): ServerLoc {
        return ServerLoc(
            minimumLocation.server,
            minimumLocation.world,
            (minimumLocation.x + maximumLocation.x) / 2,
            y,
            (minimumLocation.z + maximumLocation.z) / 2
        )
    }
}