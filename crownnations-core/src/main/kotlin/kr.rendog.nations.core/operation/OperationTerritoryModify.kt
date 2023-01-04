package kr.rendog.nations.core.operation

import kr.rendog.nations.NationMember
import kr.rendog.nations.ResultCode
import kr.rendog.nations.territory.NationAction
import kr.rendog.nations.territory.NationMonument
import kr.rendog.nations.territory.NationTerritoryService
import kr.rendog.nations.utils.ServerLoc

class OperationTerritoryModify(
    private val service: NationTerritoryService,
    private val monument: NationMonument,
    private val action: NationAction,
    private val loc: ServerLoc,
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