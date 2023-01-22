package com.minepalm.nations.event

import java.util.*

class WarMonumentDestroyEvent(
    val monumentId: Int,
    val destroyer: UUID
) : NationEvent, SendingEvent