package kr.rendog.nations.core.war

import kr.rendog.nations.Nation
import kr.rendog.nations.war.WarInfo
import kr.rendog.nations.war.WarObjective
import kr.rendog.nations.war.WarResult

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

    init{
        homeRating = home.war.rating.getRating().join()
        awayRating = away.war.rating.getRating().join()
        ratingGiven = formula.calculate(homeRating, awayRating)
    }

    fun determine(resultType: WarResult.Type, info: WarInfo, list: List<WarObjective>): WarResult{
        val winner = determineWinner(list)

        var winnerNation: Int
        var loserNation: Int
        var winnerRating: Int
        var loserRating: Int
        var eloChange: Int

        when(winner){
            WarResult.Winner.HOME -> {
                winnerNation = home.id
                loserNation = away.id
                winnerRating = homeRating
                loserRating = awayRating
            }
            WarResult.Winner.AWAY -> {
                winnerNation = away.id
                loserNation = home.id
                winnerRating = awayRating
                loserRating = homeRating
            }
            WarResult.Winner.DRAW -> {
                winnerNation = -1
                loserNation = -1
                winnerRating = 0
                loserRating = 0
            }
        }

        eloChange = formula.calculate(winnerNation, loserNation)
        return WarResult(info.warType, resultType, winnerNation, loserNation, winnerRating, loserRating, eloChange)
    }

    private fun determineWinner(list: List<WarObjective>): WarResult.Winner{
        val homeList = mutableListOf<WarObjective>()
        val awayList = mutableListOf<WarObjective>()

        list.forEach {
            when (it.nationId) {
                home.id -> homeList.add(it)
                away.id -> awayList.add(it)
            }
        }

        val homePoints = calculatePoints(homeList)
        val awayPoints = calculatePoints(awayList)

        return when{
            homePoints > awayPoints -> WarResult.Winner.HOME
            awayPoints > homePoints -> WarResult.Winner.AWAY
            else -> WarResult.Winner.DRAW
        }
    }

    private fun calculatePoints(list: List<WarObjective>): Int{
        var points = 0
        list.forEach {
            points += when(it.objectiveType){
                WarObjective.Type.PLAYER_KILL -> playerKillWeight
                WarObjective.Type.CASTLE_FALLEN -> castleDestroyedWeight
                WarObjective.Type.NATION_FALLEN -> nationFallenWeight
            }
        }
        return points
    }
}