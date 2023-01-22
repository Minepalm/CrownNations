package com.minepalm.nations.event

data class TerritoryPreClaimEvent(
    val nationId: Int,
    val type: String,
    val location: com.minepalm.nations.utils.ServerLoc,
    override var cancelled: Boolean = false,
    val time: Long = System.currentTimeMillis()
) : NationEvent, Cancellable