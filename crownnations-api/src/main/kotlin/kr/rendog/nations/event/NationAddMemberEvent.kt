package kr.rendog.nations.event

import java.util.*

data class NationAddMemberEvent(
    val nationId : Int,
    val commander : UUID,
    val userId : UUID,
    override var cancelled : Boolean = false,
    val time : Long = System.currentTimeMillis()
    ) : NationEvent, Cancellable, SendingEvent{

}