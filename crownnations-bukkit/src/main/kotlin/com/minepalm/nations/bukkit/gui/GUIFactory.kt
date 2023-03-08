package com.minepalm.nations.bukkit.gui

import com.minepalm.arkarangutils.bukkit.BukkitExecutor
import com.minepalm.nations.Dependencies
import com.minepalm.nations.Nation
import com.minepalm.nations.NationMember
import com.minepalm.nations.bukkit.config.IconRepository
import com.minepalm.nations.bukkit.gui.data.Icon
import com.minepalm.nations.bukkit.gui.data.MonumentProfile
import com.minepalm.nations.bukkit.warp.WarpExecutor
import java.util.concurrent.CompletableFuture

class GUIFactory {
    private val repo by Dependencies[IconRepository::class]
    private val factory by Dependencies[IconFactory::class]
    private val warpExecutor by Dependencies[WarpExecutor::class]
    private val executor by Dependencies[BukkitExecutor::class]

    fun buildCastleWarpGUI(nation: Nation, member: NationMember): CompletableFuture<CastleWarpGUI> {
        val data = repo.getIcon("castle")!!
        return executor.async <List<MonumentProfile>> {
            return@async nation.territory.direct.getCastles().join()
                .sortedBy { it.id }
                .map { it.getWarpLocation().join() }
                .mapIndexed { index, serverLoc ->
                    MonumentProfile(data.toIcon().apply { set("index", "$index") }, serverLoc)
                }
        }.thenApply {
            return@thenApply CastleWarpGUI(member.uniqueId, it, factory, warpExecutor)
        }
    }

    fun buildOutpostWarpGUI(nation: Nation, member: NationMember): CompletableFuture<OutpostWarpGUI> {
        val data = repo.getIcon("outpost")!!
        return executor.async <List<MonumentProfile>> {
            return@async nation.territory.direct.getOutposts().join()
                .sortedBy { it.id }
                .map { it.getWarpLocation().join() }
                .mapIndexed { index, serverLoc ->
                    MonumentProfile(data.toIcon().apply { set("index", "$index") }, serverLoc)
                }
        }.thenApply {
            return@thenApply OutpostWarpGUI(member.uniqueId, it, factory, warpExecutor)
        }
    }

}