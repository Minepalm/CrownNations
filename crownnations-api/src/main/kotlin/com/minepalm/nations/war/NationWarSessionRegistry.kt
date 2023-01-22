package com.minepalm.nations.war

import java.util.concurrent.CompletableFuture

interface NationWarSessionRegistry {

    val local: Local
    val direct: Direct

    operator fun get(matchId: Int): WarSession?

    fun getMatch(nation: Int): WarSession?

    interface Local {

        operator fun get(matchId: Int): WarSession?

        fun getMatch(nation: Int): WarSession?

        fun add(session: WarSession)

        fun remove(matchId: Int)

        fun clear()

    }

    interface Direct {

        operator fun get(matchId: Int): CompletableFuture<WarSession?>

        fun getMatch(nation: Int): CompletableFuture<WarSession?>

        fun getCurrentSessions(): CompletableFuture<List<WarSession>>

        fun delete(matchId: Int): CompletableFuture<Unit>

    }
}