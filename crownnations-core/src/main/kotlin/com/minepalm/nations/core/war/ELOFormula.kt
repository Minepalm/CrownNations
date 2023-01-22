package com.minepalm.nations.core.war

import kotlin.math.pow

class ELOFormula(
    private val config: com.minepalm.nations.config.WarConfiguration
) {

    // default 400
    val constant = config.eloConstant

    // default 15
    val weight = config.eloWeight

    // default 1000
    val baseElo = config.eloBase

    fun calculate(winner: Int, loser: Int): Int {
        return (weight * (1 - expectedWinRate(winner, loser))).toInt()
    }

    private fun expectedWinRate(home: Int, opponent: Int): Double {
        val eloGap = 10.0.pow((opponent - home) / constant.toDouble()) + 1
        return 1 / eloGap
    }

}