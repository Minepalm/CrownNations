package kr.rendog.nations.territory

import kr.rendog.nations.utils.ServerLoc

open class ProtectionRange(
    var minimumLocation : ServerLoc,
    var maximumLocation : ServerLoc
) {

    fun isIn(loc: ServerLoc): Boolean{
        if(minimumLocation.server == loc.server && minimumLocation.world == loc.world){
            return minimumLocation.x <= loc.x && minimumLocation.y <= loc.y && minimumLocation.z <= loc.z &&
                    maximumLocation.x >= loc.x && maximumLocation.y >= loc.y && maximumLocation.z >= loc.z
        }
        return false
    }
}