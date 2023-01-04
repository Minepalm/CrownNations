package kr.rendog.nations.core

import kr.rendog.nations.Nation
import kr.rendog.nations.NationMetadata
import kr.rendog.nations.NationService
import kr.rendog.nations.core.mysql.MySQLNationDataDatabase
import kr.rendog.nations.core.mysql.MySQLNationIdDatabase
import kr.rendog.nations.core.mysql.MySQLNationMemberDatabase
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService

class RendogNationFactory(
    private val dataDatabase: MySQLNationDataDatabase,
    private val memberDatabase: MySQLNationMemberDatabase,
    private val idDatabase: MySQLNationIdDatabase,
    private val nationService: NationService,
    private val executor : ExecutorService
) {

    fun build(nationId : Int, name : String) : CompletableFuture<Nation> {
        return CompletableFuture.supplyAsync(
            {
                RendogNation(nationId, name, buildCache(nationId), buildUnsafe(nationId),
            buildDirect(nationId), buildMetadata(nationId), nationService)
            }, executor)
    }

    private fun buildCache(nationId: Int) : Nation.Cache {
        return RendogNationCache(nationId, nationService.memberRegistry, memberDatabase)
    }

    private fun buildUnsafe(nationId : Int) : Nation.Unsafe {
        return RendogNationUnsafe(nationId, memberDatabase, idDatabase)
    }

    private fun buildDirect(nationId: Int) : Nation.Direct {
        return RendogNationDirect(nationId, nationService.memberRegistry, memberDatabase)
    }

    private fun buildMetadata(nationId: Int) : NationMetadata {
        return RendogNationMetadata(nationId, dataDatabase)
    }
}