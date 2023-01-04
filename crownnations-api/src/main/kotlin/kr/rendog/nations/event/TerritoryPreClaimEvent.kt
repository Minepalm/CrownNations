package kr.rendog.nations.event

import kr.rendog.nations.utils.ServerLoc

data class TerritoryPreClaimEvent(
    val nationId : Int,
    val type: String,
    val location: ServerLoc,
    override var cancelled: Boolean = false,
    val time : Long = System.currentTimeMillis()
) : NationEvent, Cancellable{


}