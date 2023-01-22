package com.minepalm.nations

import java.util.*
import java.util.concurrent.CompletableFuture

interface NationMemberRegistry {

    val cache: Cache
    val local: Local

    operator fun get(uuid: UUID): NationMember

    fun update(uuid: UUID)

    interface Cache {
        operator fun get(uuid: UUID): NationMember

    }

    interface Local {
        operator fun get(uuid: UUID): NationMember?

        fun load(uuid: UUID): CompletableFuture<NationMember>

        fun unload(uuid: UUID): CompletableFuture<Boolean>

    }

}