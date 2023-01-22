package com.minepalm.nations.core.bank

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.minepalm.library.database.impl.internal.MySQLDB
import com.minepalm.nations.NationService
import java.util.concurrent.TimeUnit

class PalmNationBankRegistry(
    service: NationService,
    mysql: MySQLDB
) : com.minepalm.nations.bank.NationBankRegistry {

    private val database = MySQLBankDatabase(mysql, "rendognations_banks")

    private val cache = CacheBuilder.newBuilder()
        .expireAfterAccess(5, TimeUnit.MINUTES)
        .build(object : CacheLoader<Int, com.minepalm.nations.bank.NationBank>() {

            override fun load(key: Int): com.minepalm.nations.bank.NationBank {
                return PalmNationBank(key, database, service)
            }

        })

    override fun get(nationId: Int): com.minepalm.nations.bank.NationBank {
        return cache[nationId]
    }
}