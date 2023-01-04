package kr.rendog.nations.core.grade

import kr.rendog.nations.Nation
import kr.rendog.nations.ResultCode
import kr.rendog.nations.grade.NationPromotion
import kr.rendog.nations.grade.NationPromotionRegistry
import kr.rendog.nations.grade.PromoteResult
import java.util.concurrent.ConcurrentHashMap

class PromotionRegistry : NationPromotionRegistry {

    private val promotions = ConcurrentHashMap<Int, NationPromotion>()

    override fun get(level: Int): NationPromotion {
        return promotions[level] ?: provideDefaultPromotion(level)
    }

    override fun set(level: Int, promotion: NationPromotion) {
        promotions[level] = promotion
    }

    override fun getMaxLevel(): Int {
        return promotions.keys().asSequence().maxOf { it }
    }

    override fun all(): Map<Int, NationPromotion> {
        return mutableMapOf<Int, NationPromotion>().apply { putAll(promotions) }
    }

    private fun provideDefaultPromotion(lv: Int): NationPromotion{
        return object : NationPromotion{
            override val targetLevel: Int = lv
            override val nextLevel: Int = targetLevel + 1

            override fun test(nation: Nation): PromoteResult {
                return PromoteResult(false, ResultCode.REQUIREMENTS_NOT_EXIST)
            }

        }
    }

}