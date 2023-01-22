package com.minepalm.nations.core

import com.minepalm.nations.Nation
import com.minepalm.nations.NationMetadata
import com.minepalm.nations.NationService
import com.minepalm.nations.core.mysql.MySQLNationDataDatabase
import com.minepalm.nations.core.mysql.MySQLNationIdDatabase
import com.minepalm.nations.core.mysql.MySQLNationMemberDatabase
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService

class PalmNationFactory(
    private val dataDatabase: MySQLNationDataDatabase,
    private val memberDatabase: MySQLNationMemberDatabase,
    private val idDatabase: MySQLNationIdDatabase,
    private val nationService: NationService,
    private val executor : ExecutorService
) {

    fun build(nationId : Int, name : String) : CompletableFuture<Nation> {
        return CompletableFuture.supplyAsync(
            {
                PalmNation(nationId, name, buildCache(nationId), buildUnsafe(nationId),
            buildDirect(nationId), buildMetadata(nationId), nationService)
            }, executor)
    }

    private fun buildCache(nationId: Int) : Nation.Cache {
        return PalmNationCache(nationId, nationService.memberRegistry, memberDatabase)
    }

    private fun buildUnsafe(nationId : Int) : Nation.Unsafe {
        return PalmNationUnsafe(nationId, memberDatabase, idDatabase)
    }

    private fun buildDirect(nationId: Int) : Nation.Direct {
        return PalmNationDirect(nationId, nationService.memberRegistry, memberDatabase)
    }

    private fun buildMetadata(nationId: Int) : NationMetadata {
        return PalmNationMetadata(nationId, dataDatabase)
    }
}