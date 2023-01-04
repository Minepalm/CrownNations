package kr.rendog.nations.grade

import kr.rendog.nations.NationService
import kr.rendog.nations.config.GradeConfiguration

interface NationGradeService {

    val root: NationService
    val promotionRegistry: NationPromotionRegistry
    val registry: NationGradeRegistry
    val config: GradeConfiguration

    fun build(nationId: Int): NationGrade
}