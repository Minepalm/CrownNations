package com.minepalm.nations.core.operation

import com.minepalm.nations.NationMember
import com.minepalm.nations.NationRank
import com.minepalm.nations.NationService
import com.minepalm.nations.ResultCode
import com.minepalm.nations.territory.WarpMonument
import com.minepalm.nations.utils.ServerLoc

class OperationSetWarp(
    val monument: WarpMonument,
    private val commander: NationMember,
    val location: ServerLoc,
    val service: NationService
) : AbstractNationOperation<Boolean>() {

    //1. 국가 같아야함
    //2. 명령자가 국가장이어야함

    override fun checkOrThrow() {
        if (!commander.cache.isAdmin()) {
            if (commander.cache.getNation() == null) {
                fail(ResultCode.NO_NATION, "국가가 없습니다.")
            }

            if (commander.cache.getNation()?.id == monument.nationId) {
                fail(ResultCode.NATION_MISMATCH, "국가가 일치하지 않습니다.")
            }

            val nation = commander.cache.getNation()!!

            if (!nation.cache.getRank(commander.uniqueId).hasPermissibleOf(NationRank.OWNER)) {
                fail(ResultCode.NO_PERMISSION, "권한이 부족합니다.")
            }

            if (!monument.range.isIn(location)) {
                fail(ResultCode.OUT_OF_RANGE, "범위를 벗어났습니다.")
            }
        }
    }

    override fun process0() {
        monument.forceSetWarpLocation(location).join()
        success(true)
    }


}