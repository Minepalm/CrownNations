package kr.rendog.nations.core.grade

import com.minepalm.library.database.impl.internal.MySQLDB
import kr.rendog.nations.NationService
import kr.rendog.nations.config.GradeConfiguration
import kr.rendog.nations.grade.NationGrade
import kr.rendog.nations.grade.NationGradeRegistry
import kr.rendog.nations.grade.NationGradeService
import kr.rendog.nations.grade.NationPromotionRegistry

class RendogNationGradeService(
    override val root: NationService,
    private val mysql: MySQLDB,
    override val config: GradeConfiguration
) : NationGradeService {

    override val promotionRegistry: NationPromotionRegistry = PromotionRegistry()
    override val registry: NationGradeRegistry = RendogNationGradeRegistry(this)
    private val database = MySQLNationGradeDatabase(mysql, "rendognations_grades")

    override fun build(nationId: Int): NationGrade {
        return RendogNationGrade(nationId, database, config, this)
    }

}