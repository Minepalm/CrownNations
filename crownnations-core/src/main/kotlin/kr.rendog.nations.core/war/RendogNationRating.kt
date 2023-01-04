package kr.rendog.nations.core.war

import kr.rendog.nations.core.mysql.MySQLWarRatingDatabase
import kr.rendog.nations.war.NationRating
import java.util.concurrent.CompletableFuture

class RendogNationRating(
    private val nationId: Int,
    private val ratingDatabase: MySQLWarRatingDatabase
) : NationRating {
    override fun getRating(): CompletableFuture<Int> {
        return ratingDatabase.getRating(nationId)
    }

    override fun addRating(rating: Int): CompletableFuture<Int> {
        return ratingDatabase.addRating(nationId, rating)
    }

    override fun subtractRating(rating: Int): CompletableFuture<Int> {
        return ratingDatabase.subtractRating(nationId, rating)
    }

    override fun setRating(rating: Int): CompletableFuture<Int> {
        return ratingDatabase.setRating(nationId, rating)
    }
}