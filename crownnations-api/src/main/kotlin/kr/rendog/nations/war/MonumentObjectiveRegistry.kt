package kr.rendog.nations.war

import kr.rendog.nations.territory.NationMonument

interface MonumentObjectiveRegistry {
    fun getObjective(monument: NationMonument): MonumentObjective?

    fun getObjectives(nationId: Int): List<MonumentObjective>

    fun resetObjectives(nationId: Int)

    fun resetObjective(monumentId: Int)

}