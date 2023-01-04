package kr.rendog.nations.core.war

import kr.rendog.nations.config.WarConfiguration
import kr.rendog.nations.territory.NationMonument
import kr.rendog.nations.territory.NationTerritoryService
import kr.rendog.nations.war.MonumentObjective
import kr.rendog.nations.war.MonumentObjectiveRegistry
import java.util.concurrent.ConcurrentHashMap

class LocalMonumentObjectiveRegistry(
    val config: WarConfiguration,
    val service: NationTerritoryService
) : MonumentObjectiveRegistry {

    private val map = ConcurrentHashMap<Int, MonumentObjective>()

    override fun getObjective(monument: NationMonument): MonumentObjective {
        return map[monument.id] ?: build(monument).apply { map[monument.id] = this }
    }

    private fun build(monument: NationMonument): MonumentObjective{
        return LocalMonumentObjective(monument.id, config, service)
    }

    override fun getObjectives(nationId: Int): List<MonumentObjective> {
        return service.territoryRegistry.getLocal(nationId)?.local?.getMonuments()?.map { getObjective(it) } ?: mutableListOf()
    }

    override fun resetObjectives(nationId: Int) {
        service.territoryRegistry.getLocal(nationId)?.local?.getMonuments()?.forEach { map.remove(it.id) }
    }

    override fun resetObjective(monumentId: Int) {
        map.remove(monumentId)
    }

}