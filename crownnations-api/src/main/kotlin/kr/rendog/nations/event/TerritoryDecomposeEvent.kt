package kr.rendog.nations.event

import kr.rendog.nations.utils.ServerLoc

data class TerritoryDecomposeEvent(
    val nationId: Int,
    val monumentId: Int,
    val monumentType: String,
    val location: ServerLoc,
    override var cancelled: Boolean = false,
    val time : Long = System.currentTimeMillis()
) : NationEvent, Cancellable, SendingEvent