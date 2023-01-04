package kr.rendog.nations.event

import kr.rendog.nations.war.SessionData
import kr.rendog.nations.war.WarTime

class WarPostDeclarationEvent(
    val sender: String,
    val sessionData: SessionData,
    val time: WarTime
) : NationEvent, SendingEvent{
}