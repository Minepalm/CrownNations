package com.minepalm.nations.core.listener

import com.minepalm.nations.NationEventListener
import com.minepalm.nations.NationService
import com.minepalm.nations.event.NationGradeUpdateEvent

sealed class SyncGrade {

    class Update(
        private val root: NationService
    ) : NationEventListener<NationGradeUpdateEvent> {
        override fun onEvent(event: NationGradeUpdateEvent) {
            root.gradeService.registry[event.nationId]?.sync()
        }
    }

    class Promote(
        private val root: NationService
    ) : NationEventListener<com.minepalm.nations.event.NationPromoteEvent> {
        override fun onEvent(event: com.minepalm.nations.event.NationPromoteEvent) {
            root.gradeService.registry[event.nationId]?.sync()
        }
    }
}