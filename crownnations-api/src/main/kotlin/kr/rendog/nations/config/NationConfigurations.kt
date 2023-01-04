package kr.rendog.nations.config

interface NationConfigurations {

    val grade: GradeConfiguration
    val member: MemberConfiguration
    val territory: TerritoryConfiguration

    fun mysql(name: String): String

}