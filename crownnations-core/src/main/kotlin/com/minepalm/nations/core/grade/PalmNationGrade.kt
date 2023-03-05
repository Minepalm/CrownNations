package com.minepalm.nations.core.grade

import com.minepalm.nations.Nation
import com.minepalm.nations.NationMember
import com.minepalm.nations.NationOperation
import com.minepalm.nations.NationService
import com.minepalm.nations.event.NationGradeUpdateEvent
import java.util.concurrent.CompletableFuture

class PalmNationGrade(
    private val nationId: Int,
    private val database: MySQLNationGradeDatabase,
    private val config: com.minepalm.nations.config.GradeConfiguration,
    private val service: com.minepalm.nations.grade.NationGradeService
) : com.minepalm.nations.grade.NationGrade {

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

    override fun operateLevelUp(commander: NationMember): NationOperation<com.minepalm.nations.grade.PromoteResult> {
        return OperationNationUpgrade(parent, service, commander)
    }

    override fun sync(): CompletableFuture<Unit> {
        return modifier.getLevel().thenApply { cachedLevel = it }
    }

    class ModifierImpl(
        private val nationId: Int,
        private val service: NationService,
        private val database: MySQLNationGradeDatabase
    ) : com.minepalm.nations.grade.NationGrade.Modifier {
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