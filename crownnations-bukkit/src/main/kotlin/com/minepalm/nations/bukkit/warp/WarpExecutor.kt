package com.minepalm.nations.bukkit.warp

import com.minepalm.arkarangutils.bukkit.BukkitExecutor
import com.minepalm.bungeejump.impl.BungeeJump
import com.minepalm.nations.utils.ServerLoc
import org.bukkit.Server
import org.bukkit.plugin.Plugin
import java.util.UUID

class WarpExecutor(
    private val bungeeJump: BungeeJump,
    private val executor: BukkitExecutor
) {

    val marker = PlayerMarker()

    fun registerListener(plugin: Plugin, server: Server) {
        server.pluginManager.registerEvents(PlayerMarkingListener(marker), plugin)
    }

    fun runWarp(uuid: UUID, serverLoc: ServerLoc, delay: Int = 0) {
        val task = newTask(uuid, serverLoc)
        marker.registerTask(task)
        if(delay == 0) {
            executor.sync {
                try {
                    task.run()
                }finally {
                    marker.unregisterTask(uuid)
                }
            }
        } else {
            executor.sync(delay * 20 ) {
                try {
                    task.run()
                }finally {
                    marker.unregisterTask(uuid)
                }
            }
        }
    }

    private fun newTask(uuid: UUID, serverLoc: ServerLoc): DelayedWarpTask {
        return DelayedWarpTask(uuid, serverLoc, bungeeJump)
    }
}