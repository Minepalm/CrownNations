package com.minepalm.nations.core.war

import com.minepalm.library.database.impl.internal.MySQLDB
import com.minepalm.nations.Dependencies
import com.minepalm.nations.NationService
import com.minepalm.nations.core.mysql.*
import java.util.concurrent.ExecutorService

class PalmNationWarService(
    override val config: com.minepalm.nations.config.WarConfiguration,
    private val sessionDataSource: MySQLDB,
    private val otherDataSource: MySQLDB,
    val executors: ExecutorService
) : com.minepalm.nations.war.NationWarService {

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
    override val ratingRegistry = PalmNationRatingRegistry(ratingDatabase)
    override val shieldRegistry = PalmNationShieldRegistry(this, shieldDatabase, shieldForcedDatabase)
    override val sessionRegistry: com.minepalm.nations.war.NationWarSessionRegistry =
        PalmWarSessionRegistry(sessionFactory, sessionDatabase)
    override val matchMaker: com.minepalm.nations.war.NationMatchMaker =
        MySQLWarMatchMaker(sessionFactory, operationFactory, sessionDatabase)
    override val objectiveRegistry = LocalMonumentObjectiveRegistry(config, root.territoryService)

    override val timer: com.minepalm.nations.war.NationWarTimer
        get() = TODO("Not yet implemented")
}