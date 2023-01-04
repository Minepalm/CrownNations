package kr.rendog.nations.event

import kr.rendog.nations.war.WarResult

class WarEndEvent(
    val sender: String,
    val matchId: Int,
    val result: WarResult
) : NationEvent, SendingEvent