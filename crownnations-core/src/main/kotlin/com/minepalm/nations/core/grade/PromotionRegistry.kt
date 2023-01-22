package com.minepalm.nations.core.grade

import com.minepalm.nations.Nation
import com.minepalm.nations.ResultCode
import com.minepalm.nations.grade.NationPromotion
import com.minepalm.nations.grade.NationPromotionRegistry
import com.minepalm.nations.grade.PromoteResult
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

    private fun provideDefaultPromotion(lv: Int): NationPromotion {
        return object : NationPromotion {
            override val targetLevel: Int = lv
            override val nextLevel: Int = targetLevel + 1

            override fun test(nation: Nation): PromoteResult {
                return PromoteResult(false, ResultCode.REQUIREMENTS_NOT_EXIST)
            }

        }
    }

}