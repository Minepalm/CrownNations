package kr.rendog.nations.core.grade

import kr.rendog.nations.Nation
import kr.rendog.nations.grade.NationPromotion

abstract class PromoteRequirement(
    override val targetLevel: Int,
    override val nextLevel: Int
    ) : NationPromotion