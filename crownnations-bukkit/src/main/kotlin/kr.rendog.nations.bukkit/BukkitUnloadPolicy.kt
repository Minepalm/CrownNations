package kr.rendog.nations.bukkit

import kr.rendog.nations.Nation
import kr.rendog.nations.NationRegistry
import org.bukkit.Bukkit

class BukkitUnloadPolicy : NationRegistry.Policy{

    override fun shouldInvalidate(nation: Nation): Boolean {
        var shouldInvalidate = true
        for (member in nation.cache.getMembers()) {
            val player = Bukkit.getPlayer(member.uniqueId)
            if(player != null && player.isOnline){
                shouldInvalidate = false
                break
            }
        }
        return shouldInvalidate
    }

}