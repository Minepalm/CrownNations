package kr.rendog.nations.war

data class WarInfo(
    val warType: WarType,
    val homeNation: Int,
    val awayNation: Int
)