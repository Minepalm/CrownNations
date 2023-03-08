package com.minepalm.nations.bukkit.warp

import com.minepalm.arkarangutils.bukkit.BukkitExecutor
import com.minepalm.bungeejump.api.entity.JumpLocation
import com.minepalm.bungeejump.api.entity.WarpLocation
import com.minepalm.bungeejump.impl.BungeeJump
import com.minepalm.nations.utils.ServerLoc
import org.bukkit.Bukkit
import java.util.UUID
import java.util.function.Consumer

class DelayedWarpTask(
    val uuid: UUID,
    private val serverLoc: ServerLoc,
    private val bungeeJump: BungeeJump
) : Runnable {

    var isCancelled = false
    private var yaw: Float = 0f
    private var pitch: Float = 0f
    var onCancelled : Consumer<UUID> = Consumer { }

    override fun run() {
        if(isCancelled) return
        Bukkit.getPlayer(uuid)?.let {
            yaw = it.location.yaw
            pitch = it.location.pitch
            warp(serverLoc)
        }
    }

    private fun warp(warp: ServerLoc) {
        val jumpLocation = JumpLocation(warp.world, warp.x + 0.5, warp.y + 0.5, warp.z + 0.5, yaw, pitch)
        bungeeJump.getJumper(uuid).jump(WarpLocation(warp.server, jumpLocation))

    }
}