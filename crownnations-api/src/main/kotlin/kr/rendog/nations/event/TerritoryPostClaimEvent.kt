package kr.rendog.nations.event

import kr.rendog.nations.utils.ServerLoc

class TerritoryPostClaimEvent(
    val nationId : Int,
    val monumentId: Int,
    val type: String,
    val location: ServerLoc,
    override var cancelled: Boolean = false,
    val time : Long = System.currentTimeMillis()
) : NationEvent, Cancellable, SendingEvent {

}