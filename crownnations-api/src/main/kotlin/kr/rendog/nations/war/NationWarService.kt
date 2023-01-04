package kr.rendog.nations.war

import kr.rendog.nations.NationService
import kr.rendog.nations.config.WarConfiguration

interface NationWarService {

    val root: NationService
    val ratingRegistry: NationRatingRegistry
    val shieldRegistry: NationShieldRegistry
    val objectiveRegistry: MonumentObjectiveRegistry
    val sessionRegistry: NationWarSessionRegistry
    val matchMaker: NationMatchMaker
    val config: WarConfiguration
    val timer: NationWarTimer

}