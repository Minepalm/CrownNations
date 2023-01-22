package com.minepalm.nations.core.grade

abstract class PromoteRequirement(
    override val targetLevel: Int,
    override val nextLevel: Int
) : com.minepalm.nations.grade.NationPromotion