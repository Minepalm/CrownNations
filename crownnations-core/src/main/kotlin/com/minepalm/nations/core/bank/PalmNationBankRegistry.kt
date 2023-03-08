package com.minepalm.nations.core.bank

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.minepalm.library.database.impl.internal.MySQLDB
import com.minepalm.nations.NationService
import com.minepalm.nations.bank.NationBank
import com.minepalm.nations.bank.NationBankRegistry
import java.util.concurrent.TimeUnit

class PalmNationBankRegistry(
    service: NationService,
    mysql: MySQLDB
) : NationBankRegistry {

    private val database = MySQLBankDatabase(mysql, "crownnations_banks")

    private val cache = CacheBuilder.newBuilder()
        .expireAfterAccess(5, TimeUnit.MINUTES)
        .build(object : CacheLoader<Int, NationBank>() {

            override fun load(key: Int): NationBank {
                return PalmNationBank(key, database, service)
            }

        })

    override fun get(nationId: Int): NationBank {
        return cache[nationId]
    }
}