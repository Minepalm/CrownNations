package com.minepalm.nations.core.grade

import com.google.common.cache.CacheBuilder
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

class PalmNationGradeRegistry(
    private val gradeService: com.minepalm.nations.grade.NationGradeService
) : com.minepalm.nations.grade.NationGradeRegistry {

    private val local = ConcurrentHashMap<Int, com.minepalm.nations.grade.NationGrade>()
    private val cache = CacheBuilder.newBuilder()
        .expireAfterAccess(5, TimeUnit.MINUTES)
        .build<Int, com.minepalm.nations.grade.NationGrade>()

    override fun get(nationId: Int): com.minepalm.nations.grade.NationGrade? {
        return local[nationId] ?: cache.getIfPresent(nationId)
    }

    override fun load(nationId: Int): CompletableFuture<Unit> {
        return gradeService.build(nationId).sync()
    }

    override fun invalidate(nationId: Int) {
        local.remove(nationId)
        cache.invalidate(nationId)
    }
}