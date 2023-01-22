package com.minepalm.nations.core.war

import java.util.concurrent.ConcurrentHashMap

class LocalMonumentObjectiveRegistry(
    val config: com.minepalm.nations.config.WarConfiguration,
    val service: com.minepalm.nations.territory.NationTerritoryService
) : com.minepalm.nations.war.MonumentObjectiveRegistry {

    private val map = ConcurrentHashMap<Int, com.minepalm.nations.war.MonumentObjective>()

    override fun getObjective(monument: com.minepalm.nations.territory.NationMonument): com.minepalm.nations.war.MonumentObjective {
        return map[monument.id] ?: build(monument).apply { map[monument.id] = this }
    }

    private fun build(monument: com.minepalm.nations.territory.NationMonument): com.minepalm.nations.war.MonumentObjective {
        return LocalMonumentObjective(monument.id, config, service)
    }

    override fun getObjectives(nationId: Int): List<com.minepalm.nations.war.MonumentObjective> {
        return service.territoryRegistry.getLocal(nationId)?.local?.getMonuments()?.map { getObjective(it) }
            ?: mutableListOf()
    }

    override fun resetObjectives(nationId: Int) {
        service.territoryRegistry.getLocal(nationId)?.local?.getMonuments()?.forEach { map.remove(it.id) }
    }

    override fun resetObjective(monumentId: Int) {
        map.remove(monumentId)
    }

}