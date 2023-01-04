package kr.rendog.nations.event

import kr.rendog.nations.war.WarInfo

class WarPreDeclarationEvent(
    val info: WarInfo,
    override var cancelled: Boolean
) : NationEvent, Cancellable{
}