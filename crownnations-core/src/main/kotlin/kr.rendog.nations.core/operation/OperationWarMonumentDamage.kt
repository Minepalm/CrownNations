package kr.rendog.nations.core.operation

import kr.rendog.nations.NationMember
import kr.rendog.nations.NationService
import kr.rendog.nations.ResultCode
import kr.rendog.nations.event.WarMonumentDestroyEvent
import kr.rendog.nations.event.WarNationFallenEvent
import kr.rendog.nations.war.MonumentObjective
import java.util.concurrent.CompletableFuture

class OperationWarMonumentDamage(
    val commander: NationMember,
    val objective: MonumentObjective,
    val amount: Int,
    val service: NationService
) : AbstractNationOperation<Boolean>(){

    override fun checkOrThrow() {
        val commanderNation = commander.cache.getNation()
        val monumentNation = objective.monument.owner

        if (commanderNation == null) {
            fail(ResultCode.NO_NATION, "국가에 소속되어 있지 않습니다.")
            return
        }

        if (monumentNation.id == commander.cache.getNation()?.id) {
            fail(ResultCode.NATION_MISMATCH, "자신의 국가를 파괴할수 없습니다.")
        }

        val session = service.warService.sessionRegistry.local.getMatch(commanderNation.id)

        if (session == null) {
            fail(ResultCode.NO_WAR, "전쟁중인 국가가 아닙니다.")
            return
        }

        if (session.isAwayTeam(commander)) {
            if (session.info.homeNation != monumentNation.id) {
                fail(ResultCode.NATION_MISMATCH, "해당 국가와는 전쟁중이 아닙니다.")
            }
        }

        if (session.isHomeTeam(commander)) {
            if (session.info.awayNation != monumentNation.id) {
                fail(ResultCode.NATION_MISMATCH, "해당 국가와는 전쟁중이 아닙니다.")
            }
        }

        if (objective.isDestroyed()) {
            fail("ALREADY_DESTROYED", "이미 파괴된 오브젝트입니다.")
        }
    }

    override fun process0() {
        objective.damage(amount)
        if(objective.currentHealth == 0){
            service.localEventBus.invoke(WarMonumentDestroyEvent(objective.monument.id, commander.uniqueId))
            val nation = objective.monument.owner
            val collapseFuture = objective.destroy()
            collapseFuture.thenCompose {destroyed ->
                if(destroyed){
                    nation.territory.direct.getCastlesCount().thenApply { count -> count == 0 }
                }else{
                    CompletableFuture.completedFuture(false)
                }
            }.thenApply {  fallen ->
                if(fallen){
                    service.localEventBus.invoke(WarNationFallenEvent(nation.id, commander.cache.getNation()!!.id))
                }
            }
        }
    }
}