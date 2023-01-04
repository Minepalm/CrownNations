package kr.rendog.nations.core.listener

import kr.rendog.nations.NationEventListener
import kr.rendog.nations.NationService
import kr.rendog.nations.event.NationGradeUpdateEvent
import kr.rendog.nations.event.NationPromoteEvent
import kr.rendog.nations.grade.NationGradeService

sealed class SyncGrade{

    class Update(
        private val root: NationService
    ) : NationEventListener<NationGradeUpdateEvent> {
        override fun onEvent(event: NationGradeUpdateEvent) {
            root.gradeService.registry[event.nationId]?.sync()
        }
    }

    class Promote(
        private val root: NationService
    ) : NationEventListener<NationPromoteEvent> {
        override fun onEvent(event: NationPromoteEvent) {
            root.gradeService.registry[event.nationId]?.sync()
        }
    }
}