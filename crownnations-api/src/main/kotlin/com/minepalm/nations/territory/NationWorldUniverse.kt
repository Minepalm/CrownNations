package com.minepalm.nations.territory

interface NationWorldUniverse {

    val host: Host
    val cache: Cache

    operator fun get(server: String, name: String): NationWorld?

    fun update(loc: com.minepalm.nations.utils.ServerLoc, monumentId: Int)

    interface Host {

        operator fun get(monumentId: Int): NationMonument?

        operator fun get(worldName: String): NationWorld?

        operator fun get(loc: com.minepalm.nations.utils.ServerLoc): NationWorld?

        fun getWorlds(): List<NationWorld>

        fun isInNationWorld(loc: com.minepalm.nations.utils.ServerLoc): Boolean

        fun add(world: NationWorld)

        fun remove(worldName: String)

        fun deleteInvalidateMonuments(): List<NationMonument>

    }

    interface Cache {

        fun getWorld(server: String, name: String): NationWorld

        fun getWorldUnchecked(server: String, name: String): NationWorld?
    }


}