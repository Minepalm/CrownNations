package com.minepalm.nations.core.war

import com.minepalm.nations.Nation

class WarResultDeterminer(
    private val home: Nation,
    private val away: Nation,
    private val formula: ELOFormula
) {

    private val nationFallenWeight = 100000
    private val castleDestroyedWeight = 1
    private val playerKillWeight = 0

    private val homeRating: Int
    private val awayRating: Int
    private val ratingGiven: Int

    init {
        homeRating = home.war.rating.getRating().join()
        awayRating = away.war.rating.getRating().join()
        ratingGiven = formula.calculate(homeRating, awayRating)
    }

    fun determine(
        resultType: com.minepalm.nations.war.WarResult.Type,
        info: com.minepalm.nations.war.WarInfo,
        list: List<com.minepalm.nations.war.WarObjective>
    ): com.minepalm.nations.war.WarResult {
        val winner = determineWinner(list)

        var winnerNation: Int
        var loserNation: Int
        var winnerRating: Int
        var loserRating: Int
        var eloChange: Int

        when (winner) {
            com.minepalm.nations.war.WarResult.Winner.HOME -> {
                winnerNation = home.id
                loserNation = away.id
                winnerRating = homeRating
                loserRating = awayRating
            }

            com.minepalm.nations.war.WarResult.Winner.AWAY -> {
                winnerNation = away.id
                loserNation = home.id
                winnerRating = awayRating
                loserRating = homeRating
            }

            com.minepalm.nations.war.WarResult.Winner.DRAW -> {
                winnerNation = -1
                loserNation = -1
                winnerRating = 0
                loserRating = 0
            }
        }

        eloChange = formula.calculate(winnerNation, loserNation)
        return com.minepalm.nations.war.WarResult(
            info.warType,
            resultType,
            winnerNation,
            loserNation,
            winnerRating,
            loserRating,
            eloChange
        )
    }

    private fun determineWinner(list: List<com.minepalm.nations.war.WarObjective>): com.minepalm.nations.war.WarResult.Winner {
        val homeList = mutableListOf<com.minepalm.nations.war.WarObjective>()
        val awayList = mutableListOf<com.minepalm.nations.war.WarObjective>()

        list.forEach {
            when (it.nationId) {
                home.id -> homeList.add(it)
                away.id -> awayList.add(it)
            }
        }

        val homePoints = calculatePoints(homeList)
        val awayPoints = calculatePoints(awayList)

        return when {
            homePoints > awayPoints -> com.minepalm.nations.war.WarResult.Winner.HOME
            awayPoints > homePoints -> com.minepalm.nations.war.WarResult.Winner.AWAY
            else -> com.minepalm.nations.war.WarResult.Winner.DRAW
        }
    }

    private fun calculatePoints(list: List<com.minepalm.nations.war.WarObjective>): Int {
        var points = 0
        list.forEach {
            points += when (it.objectiveType) {
                com.minepalm.nations.war.WarObjective.Type.PLAYER_KILL -> playerKillWeight
                com.minepalm.nations.war.WarObjective.Type.CASTLE_FALLEN -> castleDestroyedWeight
                com.minepalm.nations.war.WarObjective.Type.NATION_FALLEN -> nationFallenWeight
            }
        }
        return points
    }
}