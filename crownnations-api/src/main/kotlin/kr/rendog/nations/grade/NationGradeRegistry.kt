package kr.rendog.nations.grade

import java.util.concurrent.CompletableFuture

interface NationGradeRegistry {

    operator fun get(nationId : Int) : NationGrade?

    fun load(nationId: Int): CompletableFuture<Unit>

    fun invalidate(nationId: Int)
}