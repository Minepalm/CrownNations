package com.minepalm.nations.war

import java.util.concurrent.CompletableFuture

interface NationRating {
    fun getRating(): CompletableFuture<Int>

    fun addRating(rating: Int): CompletableFuture<Int>

    fun subtractRating(rating: Int): CompletableFuture<Int>

    fun setRating(rating: Int): CompletableFuture<Int>

}