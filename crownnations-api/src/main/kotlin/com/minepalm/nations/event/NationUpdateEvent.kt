package com.minepalm.nations.event

class NationUpdateEvent(
    val nationId: Int,
) : NationEvent, SendingEvent