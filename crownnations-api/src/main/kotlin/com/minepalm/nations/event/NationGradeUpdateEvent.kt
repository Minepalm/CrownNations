package com.minepalm.nations.event

class NationGradeUpdateEvent(
    val nationId: Int,
    val level: Int
) : NationEvent, SendingEvent