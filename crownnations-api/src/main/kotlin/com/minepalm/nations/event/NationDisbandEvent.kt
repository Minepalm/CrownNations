package com.minepalm.nations.event

import java.util.*

data class NationDisbandEvent(
    val nationId: Int,
    val nationName: String,
    val commander: UUID,
    override var cancelled: Boolean = false,
    val time: Long = System.currentTimeMillis()
) : NationEvent, Cancellable,
    SendingEvent