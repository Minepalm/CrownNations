package com.minepalm.nations.territory

interface NationCastle : NationMonument {
    fun getBeaconLocation(): com.minepalm.nations.utils.ServerLoc

    fun getWarpLocation(): com.minepalm.nations.utils.ServerLoc

}