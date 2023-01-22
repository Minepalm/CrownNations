package com.minepalm.nations.core.grade

import com.minepalm.library.database.impl.internal.MySQLDB
import com.minepalm.nations.NationService

class PalmNationGradeService(
    override val root: NationService,
    private val mysql: MySQLDB,
    override val config: com.minepalm.nations.config.GradeConfiguration
) : com.minepalm.nations.grade.NationGradeService {

    override val promotionRegistry: com.minepalm.nations.grade.NationPromotionRegistry = PromotionRegistry()
    override val registry: com.minepalm.nations.grade.NationGradeRegistry = PalmNationGradeRegistry(this)
    private val database = MySQLNationGradeDatabase(mysql, "rendognations_grades")

    override fun build(nationId: Int): com.minepalm.nations.grade.NationGrade {
        return PalmNationGrade(nationId, database, config, this)
    }

}