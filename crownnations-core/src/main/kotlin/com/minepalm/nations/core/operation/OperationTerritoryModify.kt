package com.minepalm.nations.core.operation

import com.minepalm.nations.NationMember
import com.minepalm.nations.ResultCode

class OperationTerritoryModify(
    private val service: com.minepalm.nations.territory.NationTerritoryService,
    private val monument: com.minepalm.nations.territory.NationMonument,
    private val action: com.minepalm.nations.territory.NationAction,
    private val loc: com.minepalm.nations.utils.ServerLoc,
    private val commander: NationMember
) : AbstractNationOperation<Boolean>() {

    override fun checkOrThrow() {
        if(!service.policyRegistry.test(commander, action, loc, monument)){
            setResult(false)
            fail(ResultCode.NO_PERMISSION, "권한이 없습니다.")
        }
    }

    override fun process0() {
        success(ResultCode.SUCCESSFUL, true)
    }

}