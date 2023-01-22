package com.minepalm.nations.event

import com.minepalm.nations.war.WarTime

class WarPostDeclarationEvent(
    val sender: String,
    val sessionData: com.minepalm.nations.war.SessionData,
    val time: WarTime
) : NationEvent, SendingEvent