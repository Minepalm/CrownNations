package kr.rendog.nations.core.operation

import kr.rendog.nations.NationMember
import kr.rendog.nations.NationRank
import kr.rendog.nations.ResultCode
import kr.rendog.nations.event.TerritoryDecomposeEvent
import kr.rendog.nations.territory.NationCastle
import kr.rendog.nations.territory.NationTerritoryService

class OperationDecomposeCastle(
    private val service: NationTerritoryService,
    private val monument: NationCastle,
    private val reason: String,
    private val commander: NationMember,
) : AbstractNationOperation<Boolean>() {

    override fun checkOrThrow() {
        if(commander.cache.isAdmin()){
            return
        }

        if(monument.owner != null) {
            val hasPermission = monument.owner!!.cache.getRank(commander.uniqueId).hasPermissibleOf(NationRank.OWNER)
            if (!hasPermission) {
                fail(ResultCode.NO_PERMISSION, "국가 성을 해체할 권한이 없습니다.")
            }
        }
    }

    override fun process0() {
        val event = TerritoryDecomposeEvent(monument.nationId, monument.id, monument.type, monument.center)
        service.root.localEventBus.invoke(event)
        if(event.cancelled){
            fail(ResultCode.EVENT_CANCELLED, "")
        }


        service.root.network.send(event)
        success(ResultCode.SUCCESSFUL, monument.collapse().join())
    }

}