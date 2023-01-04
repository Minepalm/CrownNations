package kr.rendog.nations.grade

interface NationPromotionRegistry {

    operator fun get(level: Int): NationPromotion

    operator fun set(level: Int, promotion: NationPromotion)

    fun getMaxLevel(): Int

    fun all(): Map<Int, NationPromotion>
}