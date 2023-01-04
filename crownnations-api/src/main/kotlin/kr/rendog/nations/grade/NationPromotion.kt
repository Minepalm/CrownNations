package kr.rendog.nations.grade

import kr.rendog.nations.Nation

interface NationPromotion {

    val targetLevel: Int
    val nextLevel: Int

    fun test(nation: Nation): PromoteResult

}