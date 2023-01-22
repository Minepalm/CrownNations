package com.minepalm.nations.war

import com.minepalm.nations.NationService

interface NationWarService {

    val root: NationService
    val ratingRegistry: NationRatingRegistry
    val shieldRegistry: NationShieldRegistry
    val objectiveRegistry: MonumentObjectiveRegistry
    val sessionRegistry: NationWarSessionRegistry
    val matchMaker: NationMatchMaker
    val config: com.minepalm.nations.config.WarConfiguration
    val timer: NationWarTimer

}