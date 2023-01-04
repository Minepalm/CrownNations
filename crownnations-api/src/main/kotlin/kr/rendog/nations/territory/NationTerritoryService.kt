package kr.rendog.nations.territory

import kr.rendog.nations.NationService
import java.util.concurrent.CompletableFuture

interface NationTerritoryService{

    val root: NationService
    val universe: NationWorldUniverse
    val territoryRegistry: NationTerritoryRegistry
    val policyRegistry: ModifyPolicyRegistry
    val operationFactory: TerritoryOperationFactory
    val modifier: WorldModifier

    fun create(schema: MonumentSchema): CompletableFuture<NationMonument?>

    fun shutdown()
}