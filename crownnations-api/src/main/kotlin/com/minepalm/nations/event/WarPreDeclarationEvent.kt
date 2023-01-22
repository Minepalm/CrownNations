package com.minepalm.nations.event

class WarPreDeclarationEvent(
    val info: com.minepalm.nations.war.WarInfo,
    override var cancelled: Boolean
) : NationEvent, Cancellable