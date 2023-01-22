package com.minepalm.nations.config

import com.minepalm.nations.NationRank

interface MemberConfiguration {

    val regex: Regex

    val displayMaxLength: Int

    val maximumMember: Int

    val bannedKeywords: List<String>

    fun getMaximumMember(rank: NationRank): Int

    fun getRankDisplay(rank: NationRank): String

    fun getRankByDisplay(str: String): NationRank?
}