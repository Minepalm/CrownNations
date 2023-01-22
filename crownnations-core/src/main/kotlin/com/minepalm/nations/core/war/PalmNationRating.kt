package com.minepalm.nations.core.war

import com.minepalm.nations.core.mysql.MySQLWarRatingDatabase
import java.util.concurrent.CompletableFuture

class PalmNationRating(
    private val nationId: Int,
    private val ratingDatabase: MySQLWarRatingDatabase
) : com.minepalm.nations.war.NationRating {
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