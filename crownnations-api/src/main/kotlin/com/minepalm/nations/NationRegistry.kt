package com.minepalm.nations

import java.util.concurrent.CompletableFuture

interface NationRegistry {

    val policy: Policy
    val local: Local
    val direct: Direct

    operator fun get(id: Int): Nation?

    operator fun get(name: String): Nation?

    fun load(id: Int): CompletableFuture<Unit>

    fun refresh(id: Int): CompletableFuture<Nation?>

    fun tryInvalidate(nationId: Int): Boolean

    fun forceInvalidate(nationId: Int)

    interface Local {
        operator fun get(id: Int): Nation?

        operator fun get(name: String): Nation?

        fun exists(id: Int): Boolean

        fun add(nation: Nation)

        fun remove(nationId: Int)

        fun getNations(): List<Nation>

    }

    interface Direct {

        fun exists(id: Int): CompletableFuture<Boolean>

        fun exists(name: String): CompletableFuture<Boolean>

        fun getNation(name: String): CompletableFuture<Nation?>

        fun getNation(id: Int): CompletableFuture<Nation?>

    }

    interface Policy {

        fun shouldInvalidate(nation: Nation): Boolean

    }

}