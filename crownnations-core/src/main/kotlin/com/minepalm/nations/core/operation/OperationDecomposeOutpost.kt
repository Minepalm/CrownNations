package com.minepalm.nations.core.operation

import com.minepalm.nations.NationMember
import com.minepalm.nations.NationRank
import com.minepalm.nations.ResultCode

class OperationDecomposeOutpost(
    private val service: com.minepalm.nations.territory.NationTerritoryService,
    private val monument: com.minepalm.nations.territory.NationMonument,
    private val commander: NationMember
) : AbstractNationOperation<Boolean>() {

    override fun checkOrThrow() {
        if(commander.cache.isAdmin()){
            return
        }

        if(monument.owner != null) {
            val hasPermission = monument.owner.cache.getRank(commander.uniqueId).hasPermissibleOf(NationRank.OWNER)
            if (!hasPermission) {
                fail(ResultCode.NO_PERMISSION, "국가 성을 해체할 권한이 없습니다.")
            }
        }
    }


    override fun process0() {
        val event = com.minepalm.nations.event.TerritoryDecomposeEvent(
            monument.nationId,
            monument.id,
            monument.type,
            monument.center
        )
        service.root.localEventBus.invoke(event)
        if(event.cancelled){
            fail(ResultCode.EVENT_CANCELLED, "")
        }


        service.root.network.send(event)
        success(ResultCode.SUCCESSFUL, monument.collapse().join())
    }

}