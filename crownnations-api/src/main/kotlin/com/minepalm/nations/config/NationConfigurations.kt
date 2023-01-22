package com.minepalm.nations.config

interface NationConfigurations {

    val grade: GradeConfiguration
    val member: MemberConfiguration
    val territory: TerritoryConfiguration

    fun mysql(name: String): String

}