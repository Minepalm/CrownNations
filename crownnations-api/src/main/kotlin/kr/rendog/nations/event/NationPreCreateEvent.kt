package kr.rendog.nations.event

import java.util.*

data class NationPreCreateEvent(
    var name : String,
    val founder : UUID,
    override var cancelled: Boolean = false,
    var time : Long = System.currentTimeMillis()
) : NationEvent, Cancellable{
}