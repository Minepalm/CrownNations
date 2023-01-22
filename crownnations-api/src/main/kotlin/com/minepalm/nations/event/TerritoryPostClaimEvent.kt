package com.minepalm.nations.event

class TerritoryPostClaimEvent(
    val nationId: Int,
    val monumentId: Int,
    val type: String,
    val location: com.minepalm.nations.utils.ServerLoc,
    override var cancelled: Boolean = false,
    val time: Long = System.currentTimeMillis()
) : NationEvent, Cancellable,
    SendingEvent