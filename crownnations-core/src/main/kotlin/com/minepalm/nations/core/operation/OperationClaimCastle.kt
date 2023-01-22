package com.minepalm.nations.core.operation

import com.minepalm.nations.NationMember
import com.minepalm.nations.NationRank
import com.minepalm.nations.ResultCode

class OperationClaimCastle(
    private val service: com.minepalm.nations.territory.NationTerritoryService,
    private val config: com.minepalm.nations.config.TerritoryConfiguration,
    private val territory: com.minepalm.nations.territory.NationTerritory,
    private val location: com.minepalm.nations.utils.ServerLoc,
    private val commander: NationMember
) : AbstractNationOperation<com.minepalm.nations.territory.NationCastle>() {

    private val nation = territory.nation

    //
    // 1. 국가 왕만 성 생성 가능함
    // 2. 최대치에 도달하면 성 못만듬
    // 3. 성과 성 사이 거리 200블럭 이상
    //
    override fun checkOrThrow() {
        val world = service.universe.host[location]

        if (world == null) {
            fail(ResultCode.INVALID_WORLD, "국가 성을 설치할수 없는 월드입니다.")
            return
        }

        if (!commander.cache.isAdmin()) {
            val countFuture = territory.direct.getCastlesCount()
            val commanderNation = commander.direct.getNation().join()

            if (commanderNation == null || commanderNation.id != nation.id) {
                fail(ResultCode.NATION_MISMATCH, "당신은 해당 국가에 소속되어 있지 않습니다.")
            }

            if (!nation.cache.getRank(commander.uniqueId).hasPermissibleOf(NationRank.OWNER)) {
                fail(ResultCode.NO_PERMISSION, "당신은 국가장이 아닙니다.")
            }

            if (countFuture.join() >= config.maximumCastleCount) {
                fail(
                    ResultCode.REACH_MAX_CASTLE,
                    "설치 가능한 최대 성 개수를 초과했습니다. " +
                            "상한: ${config.maximumCastleCount}개, 개수: ${countFuture.join()}개"
                )
            }
        }

        if (location.y >= config.maximumHeight) {
            fail(ResultCode.TOO_HIGH, "너무 높은 곳에서 성을 설치할수 없어요.")
        }

        val distance = world.local.nearest("CASTLE", location)

        if (distance != null && distance <= config.nearestDistanceCastleToClaim) {
            fail(ResultCode.TOO_CLOSE, "주변 성과 너무 가까히 있습니다. dist: $distance")
        }
    }

    override fun process0() {
        val range = com.minepalm.nations.territory.ProtectionRange(
            location.setX(location.x - config.castleLength / 2).setZ(location.z - config.castleLength / 2),
            location.setX(location.x + config.castleLength / 2).setZ(location.z + config.castleLength / 2)
        )
        val schema = com.minepalm.nations.territory.MonumentSchema(-1, nation.id, "CASTLE", location, range)

        val event = com.minepalm.nations.event.TerritoryPreClaimEvent(nation.id, "CASTLE", location)
        service.root.localEventBus.invoke(event)

        if (event.cancelled) {
            fail(ResultCode.EVENT_CANCELLED, "")
        }

        service.root.network.send(event)
        success(ResultCode.SUCCESSFUL, service.create(schema).join() as com.minepalm.nations.territory.NationCastle)
    }

}