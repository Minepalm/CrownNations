package com.minepalm.nations.core.territory

class PalmNationCastle(
    controller: com.minepalm.nations.territory.NationMonument
) : MonumentWrapper(controller), com.minepalm.nations.territory.NationCastle {
    override fun getBeaconLocation(): com.minepalm.nations.utils.ServerLoc {
        return center
    }

    override fun getWarpLocation(): com.minepalm.nations.utils.ServerLoc {
        //todo: WarpModule 만들어서 관리
        return center
    }

}