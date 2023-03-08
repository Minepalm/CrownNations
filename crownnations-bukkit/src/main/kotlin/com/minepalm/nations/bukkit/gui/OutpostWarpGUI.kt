package com.minepalm.nations.bukkit.gui

import com.minepalm.arkarangutils.bukkit.ArkarangGUI
import com.minepalm.arkarangutils.bukkit.BukkitExecutor
import com.minepalm.nations.Dependencies
import com.minepalm.nations.bukkit.gui.data.MonumentProfile
import com.minepalm.nations.bukkit.warp.WarpExecutor
import com.minepalm.nations.utils.ServerLoc
import org.bukkit.Material
import java.util.*
import java.util.function.Consumer

class OutpostWarpGUI(
    private val uuid: UUID,
    private val list: List<MonumentProfile>,
    private val factory: IconFactory,
    private val warpExecutor: WarpExecutor
) : ArkarangGUI(3, "국가 워프"){

    val executor by Dependencies[BukkitExecutor::class]

    companion object {
        private val map = mutableMapOf<Int, Int>().apply {
            put(0, 10)
            put(1, 12)
            put(2, 14)
            put(3, 16)
            put(4, 18)
        }
    }

    init {
        executor.sync {
            list.forEachIndexed { i, profile ->
                val index = map[i] ?: return@forEachIndexed
                inv.setItem(index, factory.buildIcon(profile.icon))
                funcs[index] = Consumer { event ->
                    event.whoClicked.closeInventory()
                    warp(profile.serverLoc)
                }
            }
        }
    }

    private fun warp(serverLoc: ServerLoc) {
        if (!warpExecutor.marker.isMarked(uuid)) {
            warpExecutor.runWarp(uuid, serverLoc)
        }
    }

}