package com.minepalm.nations.grade

import com.minepalm.nations.Nation

interface NationPromotion {

    val targetLevel: Int
    val nextLevel: Int

    fun test(nation: Nation): PromoteResult

}