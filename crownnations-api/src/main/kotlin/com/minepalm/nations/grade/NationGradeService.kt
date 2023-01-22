package com.minepalm.nations.grade

import com.minepalm.nations.NationService

interface NationGradeService {

    val root: NationService
    val promotionRegistry: NationPromotionRegistry
    val registry: NationGradeRegistry
    val config: com.minepalm.nations.config.GradeConfiguration

    fun build(nationId: Int): NationGrade
}