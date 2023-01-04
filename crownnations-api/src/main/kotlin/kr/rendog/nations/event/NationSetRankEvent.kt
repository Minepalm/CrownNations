package kr.rendog.nations.event

import kr.rendog.nations.NationRank
import java.util.*

data class NationSetRankEvent (
    val nationId : Int,
    val commander : UUID,
    val user : UUID,
    var rank : NationRank,
    override var cancelled : Boolean = false,
    val time : Long = System.currentTimeMillis()
) : NationEvent, Cancellable, SendingEvent {
}