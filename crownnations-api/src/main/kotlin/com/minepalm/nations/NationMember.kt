package com.minepalm.nations

import java.util.*
import java.util.concurrent.CompletableFuture

interface NationMember {

    val uniqueId: UUID
    val cache: Cache
    val direct: Direct

    fun operateLeaveNation(): NationOperation<Boolean>

    interface Cache {

        val nation: Nation?

        fun hasNation(): Boolean

        fun update(nation: Nation)

        fun update()

        fun invalidate()

        fun isAdmin(): Boolean

    }

    interface Direct {
        fun getNation(): CompletableFuture<Nation?>

        fun hasNation(): CompletableFuture<Boolean>

        fun isAdmin(): CompletableFuture<Boolean>

    }
}