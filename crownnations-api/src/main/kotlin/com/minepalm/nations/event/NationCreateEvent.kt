package com.minepalm.nations.event

import java.util.*

data class NationCreateEvent(
    val nationId: Int,
    val founder: UUID,
    var time: Long = System.currentTimeMillis()
) : NationEvent, SendingEvent