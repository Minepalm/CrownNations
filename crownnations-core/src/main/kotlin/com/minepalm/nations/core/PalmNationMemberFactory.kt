package com.minepalm.nations.core

import com.minepalm.nations.NationMember
import com.minepalm.nations.NationService
import com.minepalm.nations.core.mysql.MySQLNationMemberDatabase
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService

class PalmNationMemberFactory(
    private val service: NationService,
    private val database: MySQLNationMemberDatabase,
    private val admins: NationAdmins,
    private val executor: ExecutorService
) {
    fun build(uniqueId : UUID) : CompletableFuture<NationMember> {
        return CompletableFuture.supplyAsync({
            PalmNationMember(uniqueId, service.nationRegistry, service.operationFactory, database, admins)
        }, executor)
    }
}