package com.minepalm.nations.core.operation

import com.minepalm.nations.NationMember
import com.minepalm.nations.ResultCode
import com.minepalm.nations.config.TerritoryConfiguration
import com.minepalm.nations.event.TerritoryPreClaimEvent
import com.minepalm.nations.territory.MonumentSchema
import com.minepalm.nations.territory.NationCastle
import com.minepalm.nations.territory.NationTerritoryService
import com.minepalm.nations.territory.ProtectionRange
import com.minepalm.nations.utils.ServerLoc

class OperationClaimCastleAsNationCreate(
    private val service: NationTerritoryService,
    private val config: TerritoryConfiguration,
    private val location: ServerLoc,
    private val commander: NationMember
) : AbstractNationOperation<NationCastle>() {

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
        if (this["nationId", Int::class.java] == -1)
            fail(ResultCode.NATION_NOT_EXISTS, "올바르지 않은 국가 코드 입니다.")

        val range = ProtectionRange(
            location.setX(location.x - config.castleLength / 2)
                .setZ(location.z - config.castleLength / 2)
                .setY(-64),
            location.setX(location.x + config.castleLength / 2).setZ(location.z + config.castleLength / 2)
                .setY(config.maximumHeight)
        )
        val schema = MonumentSchema(-1, nationId, "CASTLE", location, range)

        val event = TerritoryPreClaimEvent(nationId, "CASTLE", location)
        service.root.localEventBus.invoke(event)

        if (event.cancelled) {
            fail(ResultCode.EVENT_CANCELLED, "")
        }

        service.root.network.send(event)
        success(service.create(schema).join() as NationCastle)
    }

    override fun rollback() {
        service.root.forceDelete(nationId).join()
    }
}