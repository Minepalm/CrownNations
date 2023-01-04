package kr.rendog.nations.event

class WarNationFallenEvent(
    val fallenId: Int,
    val destroyerId: Int
) : NationEvent, SendingEvent