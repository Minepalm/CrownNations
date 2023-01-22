package com.minepalm.nations.event

import java.util.*

data class NationPromoteEvent(
    val nationId: Int,
    val commander: UUID,
    val currentLevel: Int,
    var nextLevel: Int,
    var result: com.minepalm.nations.grade.PromoteResult,
    override var cancelled: Boolean = false,
    val time: Long = System.currentTimeMillis()
) : NationEvent, Cancellable,
    SendingEvent