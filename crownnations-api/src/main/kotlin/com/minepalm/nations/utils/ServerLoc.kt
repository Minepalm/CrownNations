package com.minepalm.nations.utils

data class ServerLoc(
    val server: String,
    val world: String,
    val x: Int,
    val y: Int,
    val z: Int
) {

    fun setX(newX: Int): ServerLoc {
        return ServerLoc(server, world, newX, y, z)
    }

    fun setY(newY: Int): ServerLoc {
        return ServerLoc(server, world, x, newY, z)
    }

    fun setZ(newZ: Int): ServerLoc {
        return ServerLoc(server, world, x, y, newZ)
    }

}