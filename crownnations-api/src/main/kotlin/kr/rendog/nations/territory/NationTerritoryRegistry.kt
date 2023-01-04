package kr.rendog.nations.territory

interface NationTerritoryRegistry {
    operator fun get(nationId: Int): NationTerritory?

    fun getCached(nationId: Int): NationTerritory?

    fun getLocal(nationId: Int): NationTerritory?

    fun load(nationId: Int): NationTerritory?

    fun unload(nationId: Int)

    fun shutdown()
}