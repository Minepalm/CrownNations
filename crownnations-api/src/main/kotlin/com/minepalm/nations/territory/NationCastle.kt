package com.minepalm.nations.territory

import com.minepalm.nations.utils.ServerLoc

interface NationCastle : WarpMonument {
    fun getBeaconLocation(): ServerLoc

}