package com.minepalm.nations.event

data class NationUpgradeEvent(
    val nationId: Int,
    val beforeGrade: Int,
    var afterGrade: Int,
    override var cancelled: Boolean = false,
    val time: Long = System.currentTimeMillis()
) : NationEvent, Cancellable,
    SendingEvent