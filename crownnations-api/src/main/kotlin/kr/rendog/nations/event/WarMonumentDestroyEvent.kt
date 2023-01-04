package kr.rendog.nations.event

import java.util.*

class WarMonumentDestroyEvent(
    val monumentId: Int,
    val destroyer: UUID
) : NationEvent, SendingEvent