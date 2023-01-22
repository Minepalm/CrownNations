package com.minepalm.nations.core.operation

import com.minepalm.nations.NationMember
import com.minepalm.nations.NationRank
import com.minepalm.nations.ResultCode

class OperationClaimOutpost(
    private val service: com.minepalm.nations.territory.NationTerritoryService,
    private val config: com.minepalm.nations.config.TerritoryConfiguration,
    private val territory: com.minepalm.nations.territory.NationTerritory,
    private val location: com.minepalm.nations.utils.ServerLoc,
    private val commander: NationMember
) : AbstractNationOperation<com.minepalm.nations.territory.NationOutpost>() {

    private val nation = territory.nation

    //
    // 1. 국가
    //
    override fun checkOrThrow() {
        val world = service.universe.host[location]

        if (world == null) {
            fail(ResultCode.INVALID_WORLD, "국가 성을 설치할수 없는 월드입니다.")
            return
        }

        if (!commander.cache.isAdmin()) {
            val countFuture = territory.direct.getOutpostCount()
            val commanderNation = commander.direct.getNation().join()

            if (commanderNation == null || commanderNation.id != nation.id) {
                fail(ResultCode.NATION_MISMATCH, "당신은 해당 국가에 소속되어 있지 않습니다.")
            }

            if (!nation.cache.getRank(commander.uniqueId).hasPermissibleOf(NationRank.OWNER)) {
                fail(ResultCode.NO_PERMISSION, "당신은 국가장이 아닙니다.")
            }

            if (countFuture.join() >= config.maximumOutpostCount) {
                fail(
                    ResultCode.REACH_MAX_OUTPOST,
                    "설치 가능한 최대 전초기지 개수를 초과했습니다. " +
                            "상한: ${config.maximumOutpostCount}개, 개수: ${countFuture.join()}개"
                )
            }
        }

        val distanceCastle = world.local.nearest("CASTLE", location)
        val distanceOutpost = world.local.nearest("OUTPOST", location)

        if (distanceCastle != null && distanceCastle <= config.nearestDistanceOutpostToClaim) {
            fail(ResultCode.TOO_CLOSE, "주변 성과 너무 가까히 있습니다.")
        }

        if (distanceOutpost != null && distanceOutpost <= config.nearestDistanceOutpostToClaim) {
            fail(ResultCode.TOO_CLOSE, "주변 전초기지와 너무 가까히 있습니다.")
        }
    }

    override fun process0() {
        val range = com.minepalm.nations.territory.ProtectionRange(
            location.setX(location.x - config.outpostLength / 2).setZ(location.z - config.outpostLength / 2),
            location.setX(location.x + config.outpostLength / 2).setZ(location.z + config.outpostLength / 2)
        )
        val schema = com.minepalm.nations.territory.MonumentSchema(-1, nation.id, "OUTPOST", location, range)


        val event = com.minepalm.nations.event.TerritoryPreClaimEvent(nation.id, "OUTPOST", location)
        service.root.localEventBus.invoke(event)

        if (event.cancelled) {
            fail(ResultCode.EVENT_CANCELLED, "")
        }

        service.root.network.send(event)
        success(ResultCode.SUCCESSFUL, service.create(schema).join() as com.minepalm.nations.territory.NationOutpost)
    }


}