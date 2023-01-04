package kr.rendog.nations.territory

import kr.rendog.nations.utils.ServerLoc

interface NationCastle : NationMonument {
    fun getBeaconLocation(): ServerLoc

    fun getWarpLocation(): ServerLoc

}