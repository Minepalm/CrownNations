package kr.rendog.nations.core

import kr.rendog.nations.NationMember
import kr.rendog.nations.NationService
import kr.rendog.nations.core.mysql.MySQLNationMemberDatabase
import java.util.UUID
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService

class RendogNationMemberFactory(
    private val service: NationService,
    private val database: MySQLNationMemberDatabase,
    private val admins: NationAdmins,
    private val executor: ExecutorService
) {
    fun build(uniqueId : UUID) : CompletableFuture<NationMember> {
        return CompletableFuture.supplyAsync({
            RendogNationMember(uniqueId, service.nationRegistry, service.operationFactory, database, admins)
        }, executor)
    }
}