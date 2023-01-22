package com.minepalm.nations.event

data class NationDepositEvent(
    val nationId: Int,
    val reason: String,
    var amount: Double,
    override var cancelled: Boolean = false,
    val time: Long = System.currentTimeMillis()
) : NationEvent, Cancellable