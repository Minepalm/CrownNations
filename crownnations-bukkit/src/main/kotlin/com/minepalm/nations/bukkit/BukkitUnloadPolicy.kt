package com.minepalm.nations.bukkit

import com.minepalm.nations.Nation
import com.minepalm.nations.NationRegistry
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