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
        return ServerLoc(server, world, x, newY, z).fixY()
    }

    fun setZ(newZ: Int): ServerLoc {
        return ServerLoc(server, world, x, y, newZ)
    }

    fun add(x: Int, y: Int, z: Int): ServerLoc {
        return ServerLoc(server, world, this.x + x, this.y + y, this.z + z).fixY()
    }

    private fun fixY(): ServerLoc {
        return ServerLoc(server, world, x, y.coerceAtMost(384).coerceAtLeast(-64), z)
    }
}