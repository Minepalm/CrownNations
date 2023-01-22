package com.minepalm.nations.bungee

import com.minepalm.nations.Nation
import com.minepalm.nations.NationRegistry
import net.md_5.bungee.api.ProxyServer

class BungeeUnloadPolicy : NationRegistry.Policy{

    override fun shouldInvalidate(nation: Nation): Boolean {
        var shouldInvalidate = true
        for (member in nation.cache.getMembers()) {
            val player = ProxyServer.getInstance().getPlayer(member.uniqueId)
            if(player != null){
                shouldInvalidate = false
                break
            }
        }
        return shouldInvalidate
    }

}