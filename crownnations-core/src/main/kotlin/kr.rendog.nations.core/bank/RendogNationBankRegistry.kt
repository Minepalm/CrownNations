package kr.rendog.nations.core.bank

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.minepalm.library.database.impl.internal.MySQLDB
import kr.rendog.nations.NationService
import kr.rendog.nations.bank.NationBank
import kr.rendog.nations.bank.NationBankRegistry
import java.util.concurrent.TimeUnit

class RendogNationBankRegistry(
    service: NationService,
    mysql: MySQLDB
) : NationBankRegistry {

    private val database = MySQLBankDatabase(mysql, "rendognations_banks")

    private val cache = CacheBuilder.newBuilder()
        .expireAfterAccess(5, TimeUnit.MINUTES)
        .build(object : CacheLoader<Int, NationBank>(){

            override fun load(key: Int): NationBank {
                return RendogNationBank(key, database, service)
            }

        })
    override fun get(nationId: Int): NationBank {
        return cache[nationId]
    }
}