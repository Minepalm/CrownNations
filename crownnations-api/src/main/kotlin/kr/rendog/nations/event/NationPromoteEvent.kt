package kr.rendog.nations.event

import kr.rendog.nations.grade.PromoteResult
import java.util.*

data class NationPromoteEvent(
    val nationId: Int,
    val commander: UUID,
    val currentLevel: Int,
    var nextLevel: Int,
    var result: PromoteResult,
    override var cancelled : Boolean = false,
    val time : Long = System.currentTimeMillis()
) : NationEvent, Cancellable, SendingEvent