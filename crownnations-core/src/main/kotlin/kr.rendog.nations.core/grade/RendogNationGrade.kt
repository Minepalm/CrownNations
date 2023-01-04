package kr.rendog.nations.core.grade

import kr.rendog.nations.Nation
import kr.rendog.nations.NationMember
import kr.rendog.nations.NationOperation
import kr.rendog.nations.NationService
import kr.rendog.nations.config.GradeConfiguration
import kr.rendog.nations.event.NationGradeUpdateEvent
import kr.rendog.nations.grade.NationGrade
import kr.rendog.nations.grade.NationGradeService
import kr.rendog.nations.grade.PromoteResult
import java.util.concurrent.CompletableFuture

class RendogNationGrade(
    private val nationId: Int,
    private val database: MySQLNationGradeDatabase,
    private val config: GradeConfiguration,
    private val service: NationGradeService
) : NationGrade {

    override val modifier = ModifierImpl(nationId, service.root, database)

    override val parent: Nation
        get() = service.root.nationRegistry[nationId]!!

    override var currentLevel: Int
        get() = cachedLevel
        set(value) {
            cachedLevel = value
            modifier.setLevel(value)
        }
    override val displayName: String
        get() = config.getDisplay(cachedLevel)

    private var cachedLevel = 0

    override fun operateLevelUp(commander: NationMember): NationOperation<PromoteResult> {
        return OperationNationUpgrade(parent, service, commander)
    }

    override fun sync(): CompletableFuture<Unit> {
        return modifier.getLevel().thenApply { cachedLevel = it }
    }

    class ModifierImpl(
        private val nationId: Int,
        private val service: NationService,
        private val database: MySQLNationGradeDatabase
    ): NationGrade.Modifier{
        override fun getLevel(): CompletableFuture<Int> {
            return database.getGrade(nationId)
        }

        override fun setLevel(level: Int): CompletableFuture<Unit> {
            val event = NationGradeUpdateEvent(nationId, level)
            service.localEventBus.invoke(event)
            service.network.send(event)
            return database.setGrade(nationId, level)
        }

    }
}