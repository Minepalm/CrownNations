package kr.rendog.nations.core.territory

import kr.rendog.nations.territory.NationCastle
import kr.rendog.nations.territory.NationMonument
import kr.rendog.nations.utils.ServerLoc

class RendogNationCastle(
    controller: NationMonument
) : MonumentWrapper(controller), NationCastle {
    override fun getBeaconLocation(): ServerLoc {
        return center
    }

    override fun getWarpLocation(): ServerLoc {
        //todo: WarpModule 만들어서 관리
        return center
    }

}