package kr.rendog.nations.event

import java.util.*

data class NationMetadataChangeEvent (
    val nationId : Int,
    var key : String,
    var valueBefore : String?,
    var valueTo : String?,
    val commander : UUID?,
    override var cancelled : Boolean = false,
    val time : Long = System.currentTimeMillis()
) : NationEvent, Cancellable, SendingEvent