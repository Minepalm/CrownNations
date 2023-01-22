package com.minepalm.nations.event

class WarNationFallenEvent(
    val fallenId: Int,
    val destroyerId: Int
) : NationEvent, SendingEvent