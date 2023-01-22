package com.minepalm.nations.event

class WarEndEvent(
    val sender: String,
    val matchId: Int,
    val result: com.minepalm.nations.war.WarResult
) : NationEvent, SendingEvent