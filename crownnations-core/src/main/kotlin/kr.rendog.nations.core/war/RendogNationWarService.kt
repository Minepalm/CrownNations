package kr.rendog.nations.core.war

import com.minepalm.library.database.impl.internal.MySQLDB
import kr.rendog.nations.Dependencies
import kr.rendog.nations.NationService
import kr.rendog.nations.config.WarConfiguration
import kr.rendog.nations.core.mysql.*
import kr.rendog.nations.war.*
import java.util.concurrent.ExecutorService

class RendogNationWarService(
    override val config: WarConfiguration,
    private val sessionDataSource: MySQLDB,
    private val otherDataSource: MySQLDB,
    val executors: ExecutorService
) : NationWarService{

    val sessionDatabase = MySQLWarSessionDatabase(sessionDataSource, "rendognations_matches")
    val statusDatabase = MySQLWarSessionActiveDatabase(sessionDataSource, "rendognations_match_active")
    val timeDatabase = MySQLWarSessionTimeDatabase(sessionDataSource, "rendognations_match_time")
    val objectiveDatabase = MySQLWarObjectiveDatabase(otherDataSource, "rendognations_match_objectives")
    val ratingDatabase = MySQLWarRatingDatabase(otherDataSource, "rendognations_ratings", config.defaultRating)
    val shieldDatabase = MySQLWarShieldDatabase(otherDataSource, "rendognations_shields")
    val shieldForcedDatabase = MySQLWarShieldForcedDatabase(otherDataSource, "rendognations_shield_forced")

    val eloFormula = ELOFormula(config)
    val operationFactory = WarOperationFactory(this, eloFormula)
    private val sessionFactory = MatchSessionFactory(this)

    override val root: NationService
        get() = Dependencies[NationService::class.java].get()
    override val ratingRegistry = RendogNationRatingRegistry(ratingDatabase)
    override val shieldRegistry = RendogNationShieldRegistry(this, shieldDatabase, shieldForcedDatabase)
    override val sessionRegistry: NationWarSessionRegistry = RendogWarSessionRegistry(sessionFactory, sessionDatabase)
    override val matchMaker: NationMatchMaker = MySQLWarMatchMaker(sessionFactory, operationFactory, sessionDatabase)
    override val objectiveRegistry = LocalMonumentObjectiveRegistry(config, root.territoryService)

    override val timer: NationWarTimer
        get() = TODO("Not yet implemented")
}