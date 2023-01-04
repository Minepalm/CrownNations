package kr.rendog.nations.core.grade

import com.google.common.cache.CacheBuilder
import kr.rendog.nations.grade.NationGrade
import kr.rendog.nations.grade.NationGradeRegistry
import kr.rendog.nations.grade.NationGradeService
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

class RendogNationGradeRegistry(
    private val gradeService: NationGradeService
) : NationGradeRegistry {

    private val local = ConcurrentHashMap<Int, NationGrade>()
    private val cache = CacheBuilder.newBuilder()
        .expireAfterAccess(5, TimeUnit.MINUTES)
        .build<Int, NationGrade>()
    override fun get(nationId: Int): NationGrade? {
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