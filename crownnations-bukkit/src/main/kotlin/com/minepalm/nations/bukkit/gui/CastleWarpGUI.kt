package com.minepalm.nations.bukkit.gui

import com.minepalm.arkarangutils.bukkit.ArkarangGUI
import com.minepalm.arkarangutils.bukkit.BukkitExecutor
import com.minepalm.nations.Dependencies
import com.minepalm.nations.bukkit.gui.data.MonumentProfile
import com.minepalm.nations.bukkit.warp.WarpExecutor
import com.minepalm.nations.utils.ServerLoc
import org.bukkit.Material
import java.util.UUID
import java.util.function.Consumer

class CastleWarpGUI(
    private val uuid: UUID,
    private val list: List<MonumentProfile>,
    private val factory: IconFactory,
    private val warpExecutor: WarpExecutor
) : ArkarangGUI(3, "국가 워프"){

    private val executor by Dependencies[BukkitExecutor::class]
    companion object {
        //&6&l|&f 왕: Cosine_A
        val icon = Material.BEACON
        private val map = mutableMapOf<Int, Int>().apply {
            put(0, 10)
            put(1, 13)
            put(2, 16)
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
