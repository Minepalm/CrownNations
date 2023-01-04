package kr.rendog.nations.war

class WarResult(
    val type: WarType,
    val resultType: Type,
    val winner: Int,
    val loser: Int,
    val winnerRating: Int,
    val loserRating: Int,
    val ratingResult: Int
) {

    enum class Type{
        SURRENDER, FALLEN, TIMEOUT, INTERRUPT
    }

    enum class Winner{
        HOME, AWAY, DRAW
    }
}