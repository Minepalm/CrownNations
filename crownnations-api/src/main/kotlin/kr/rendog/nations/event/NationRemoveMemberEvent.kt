package kr.rendog.nations.event

import java.util.*

data class NationRemoveMemberEvent (
    val nationId : Int,
    val commander : UUID,
    val removerId : UUID,
    var reason: String,
    override var cancelled : Boolean = false,
    val time : Long = System.currentTimeMillis()
) : NationEvent, Cancellable, SendingEvent