package com.minepalm.nations.war

interface MonumentObjectiveRegistry {
    fun getObjective(monument: com.minepalm.nations.territory.NationMonument): MonumentObjective?

    fun getObjectives(nationId: Int): List<MonumentObjective>

    fun resetObjectives(nationId: Int)

    fun resetObjective(monumentId: Int)

}