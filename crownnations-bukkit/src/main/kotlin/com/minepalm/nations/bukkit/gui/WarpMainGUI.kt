package com.minepalm.nations.bukkit.gui

import com.minepalm.arkarangutils.bukkit.ArkarangGUI
import com.minepalm.nations.Dependencies
import com.minepalm.nations.NationService
import com.minepalm.nations.bukkit.item
import com.minepalm.nations.bukkit.openSync
import org.bukkit.Material
import org.bukkit.entity.Player
import java.util.UUID
import java.util.function.Consumer

class WarpMainGUI(private val uuid: UUID) : ArkarangGUI(3, "워프") {

    private val factory by Dependencies[GUIFactory::class]
    private val service by Dependencies[NationService::class]

    companion object {
        const val CASTLE = 11
        const val OUTPOST = 15
    }
    private val member
        get() = service.memberRegistry[uuid]

    init {
        //
        // 9 10 [11] 4 5 6 [15] 16 17
        inv.setItem(CASTLE, item {
            material = Material.BEACON
            name = "<gold><bold>|<white> 성"
            lore = listOf(
                """
                <white><bold>|<white> 성 워프 목록으로 이동합니다.
                        
                    """.trimIndent()
            )
        })
        inv.setItem(OUTPOST, item {
            material = Material.NETHERRACK
            name = "<gold><bold>|<white> 전초기지"
            lore = listOf(
                """
                <white><bold>|<white> 전초기지 워프 목록으로 이동합니다.
                        
                    """.trimIndent()
            )
        })

        funcs[CASTLE] = Consumer { event ->
            event.whoClicked.closeInventory()
            member.cache.nation?.let { nation ->
                factory.buildCastleWarpGUI(nation, member).thenAccept {
                    it.openSync(event.whoClicked as Player)
                }
            }
        }

        funcs[OUTPOST] = Consumer { event ->
            event.whoClicked.closeInventory()
            member.cache.nation?.let { nation ->
                factory.buildOutpostWarpGUI(nation, member).thenAccept {
                    it.openSync(event.whoClicked as Player)
                }
            }
        }

    }


}