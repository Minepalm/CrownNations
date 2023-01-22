package com.minepalm.nations.event

data class NationWithdrawEvent(
    val nationId: Int,
    val reason: String,
    var amount: Double,
    override var cancelled: Boolean = false,
    val time: Long = System.currentTimeMillis()
) : NationEvent, Cancellable