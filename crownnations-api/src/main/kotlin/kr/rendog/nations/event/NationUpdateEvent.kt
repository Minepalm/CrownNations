package kr.rendog.nations.event

class NationUpdateEvent(
    val nationId: Int,
): NationEvent, SendingEvent