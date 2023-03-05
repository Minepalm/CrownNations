package com.minepalm.nations.core.territory

import com.minepalm.nations.territory.NationCastle
import com.minepalm.nations.territory.NationMonument

class PalmNationCastle(
    controller: NationMonument
) : MonumentWrapper(controller), NationCastle {
    override fun getBeaconLocation(): com.minepalm.nations.utils.ServerLoc {
        return center
    }

    override fun getWarpLocation(): com.minepalm.nations.utils.ServerLoc {
        //todo: WarpModule 만들어서 관리
        return center
    }

}