package kr.rendog.nations.event

import java.util.*

data class NationTransferEvent (
    val nationId : Int,
    val transferFrom : UUID,
    var transferTo : UUID,
    override var cancelled : Boolean = false,
    val time : Long = System.currentTimeMillis()
) : NationEvent, Cancellable, SendingEvent