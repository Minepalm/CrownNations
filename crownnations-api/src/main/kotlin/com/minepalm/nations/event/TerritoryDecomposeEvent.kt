package com.minepalm.nations.event

data class TerritoryDecomposeEvent(
    val nationId: Int,
    val monumentId: Int,
    val monumentType: String,
    val location: com.minepalm.nations.utils.ServerLoc,
    override var cancelled: Boolean = false,
    val time: Long = System.currentTimeMillis()
) : NationEvent, Cancellable,
    SendingEvent