package com.minepalm.nations.bukkit

import com.minepalm.nations.utils.ServerLoc
import org.bukkit.Bukkit
import org.bukkit.Location

internal object U /* tils */ {

    val serverName: String
        get() = CrownNationsBukkit.inst.network.host.name

}

fun Location.convert(): ServerLoc {
    return ServerLoc(U.serverName, this.world.name, this.blockX, this.blockY, this.blockZ)
}

fun ServerLoc.convert(): Location {
    return Location(Bukkit.getWorld(this.world), this.x.toDouble(), this.y.toDouble(), this.z.toDouble())
}