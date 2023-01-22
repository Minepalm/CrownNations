package com.minepalm.nations.territory

open class ProtectionRange(
    var minimumLocation: com.minepalm.nations.utils.ServerLoc,
    var maximumLocation: com.minepalm.nations.utils.ServerLoc
) {

    fun isIn(loc: com.minepalm.nations.utils.ServerLoc): Boolean {
        if (minimumLocation.server == loc.server && minimumLocation.world == loc.world) {
            return minimumLocation.x <= loc.x && minimumLocation.y <= loc.y && minimumLocation.z <= loc.z &&
                    maximumLocation.x >= loc.x && maximumLocation.y >= loc.y && maximumLocation.z >= loc.z
        }
        return false
    }
}