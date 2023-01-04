package kr.rendog.nations.core.war

import kr.rendog.nations.Nation
import kr.rendog.nations.NationMember
import kr.rendog.nations.NationOperation
import kr.rendog.nations.war.*
import java.util.concurrent.CompletableFuture

class RendogNationWar(
    private val nationId: Int,
    private val service: RendogNationWarService
) : NationWar {

    override val parent: Nation
        get() = service.root.nationRegistry[nationId]!!
    override val shield: NationShield
        get() = service.shieldRegistry[nationId]
    override val rating: NationRating
        get() = service.ratingRegistry[nationId]

    override val objectives: List<MonumentObjective>
        get() = service.objectiveRegistry.getObjectives(nationId)

    override fun getCurrentMatch(): CompletableFuture<WarSession?> {
        return service.sessionRegistry.direct.getMatch(nationId)
    }

    override fun isInWar(): CompletableFuture<Boolean> {
        return getCurrentMatch().thenCompose {
                it?.getStatus()?.thenApply { status -> status == WarStatus.IN_GAME || status == WarStatus.PREPARE }
                    ?: CompletableFuture.completedFuture(false)
        }
    }

    override fun getOpponent(): CompletableFuture<Nation?> {
        return getCurrentMatch().thenApply {
            it?.let { session ->
                if(session.info.awayNation == nationId)
                    session.home
                else if(session.info.homeNation == nationId)
                    session.away
                else
                    null
            }
        }
    }

    override fun getRecentOpponents(count: Int): CompletableFuture<List<Nation>> {
        //todo: 로그 구현할때 이 부분 같이 구현하기
        throw UnsupportedOperationException("아직 미구현 ( 전쟁 로그와 연계되어야 함 ) ")
    }

    override fun operateDeclareWar(
        commander: NationMember,
        type: WarType,
        opponent: Nation
    ): NationOperation<WarSession> {
        return service.operationFactory
            .buildWarDeclaration(commander, WarInfo(type, nationId, opponent.id))
    }

    override fun operateSurrender(
        commander: NationMember
    ): NationOperation<WarResult> {
        return service.operationFactory
            .buildWarSurrender(commander, this.parent)
    }
}