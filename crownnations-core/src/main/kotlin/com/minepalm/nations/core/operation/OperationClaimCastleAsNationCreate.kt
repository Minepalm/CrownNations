package com.minepalm.nations.core.operation

import com.minepalm.nations.NationMember
import com.minepalm.nations.ResultCode

class OperationClaimCastleAsNationCreate(
    private val service: com.minepalm.nations.territory.NationTerritoryService,
    private val config: com.minepalm.nations.config.TerritoryConfiguration,
    private val location: com.minepalm.nations.utils.ServerLoc,
    private val commander: NationMember
) : AbstractNationOperation<com.minepalm.nations.territory.NationCastle>(){

    init {
        set("nationId", -1)
    }

    private val nationId: Int
        get() = this["nationId", Int::class.java]

    //
    // 1. 국가 생성시 사용함
    // 2. create check -> castle validation check -> process creation -> process create castle
    //
    override fun checkOrThrow() {
        val world = service.universe.host[location]

        if(world == null) {
            fail(ResultCode.INVALID_WORLD, "국가 성을 설치할수 없는 월드입니다.")
            return
        }

        val distance = world.local.nearest("CASTLE", location)

        if(distance != null && distance <= config.nearestDistanceCastleToClaim){
            fail(ResultCode.TOO_CLOSE, "주변 성과 너무 가까히 있습니다.")
        }
    }

    override fun process0() {
        if(this["nationId", Int::class.java] == -1)
            fail(ResultCode.NATION_NOT_EXISTS, "올바르지 않은 국가 코드 입니다.")

        val range = com.minepalm.nations.territory.ProtectionRange(
            location.setX(location.x - config.castleLength / 2).setZ(location.z - config.castleLength / 2),
            location.setX(location.x + config.castleLength / 2).setZ(location.z + config.castleLength / 2)
        )
        val schema = com.minepalm.nations.territory.MonumentSchema(-1, nationId, "CASTLE", location, range)

        val event = com.minepalm.nations.event.TerritoryPreClaimEvent(nationId, "CASTLE", location)
        service.root.localEventBus.invoke(event)

        if(event.cancelled){
            fail(ResultCode.EVENT_CANCELLED, "")
        }

        service.root.network.send(event)
        success(ResultCode.SUCCESSFUL, service.create(schema).join() as com.minepalm.nations.territory.NationCastle)
    }

}