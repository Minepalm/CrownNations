package com.minepalm.nations.bukkit

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.minepalm.library.network.impl.player.NetworkPlayers
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

class PlayerCache(
    private val playersModule: NetworkPlayers
) {

    private val uuidsLocal = ConcurrentHashMap<UUID, String>()
    private val namesLocal = ConcurrentHashMap<String, UUID>()
    private val namesCache = CacheBuilder.newBuilder()
        .expireAfterAccess(10, TimeUnit.MINUTES)
        .build(object : CacheLoader<String, Optional<UUID>>() {
            override fun load(key: String): Optional<UUID> {
                return playersModule.nameRegistry.getUniqueID(key).join().let { Optional.ofNullable(it) }
            }

        })
    private val uuidsCache = CacheBuilder.newBuilder()
        .expireAfterAccess(10, TimeUnit.MINUTES)
        .build(object : CacheLoader<UUID, Optional<String>>(){
            override fun load(key: UUID): Optional<String> {
                return playersModule.nameRegistry.getUsername(key).join().let { Optional.ofNullable(it) }
            }

        })

    private fun register(uuid: UUID, username: String){
        uuidsLocal[uuid] = username
        namesLocal[username] = uuid
    }

    fun invalidate(uuid: UUID, username: String){
        namesLocal.remove(username)
        uuidsLocal.remove(uuid)
    }


    fun isOnline(username : String) : Boolean{
        return uuid(username)?.let { playersModule[it].isOnline.join() } ?: false
    }

    fun exists(username : String) : Boolean{
        return uuid(username) != null
    }

    fun uuid(username : String) : UUID?{
        return namesLocal[username] ?: namesCache[username].orElse(null)
    }

    fun username(uuid: UUID): String?{
        return uuidsLocal[uuid] ?: uuidsCache[uuid].orElse(null)
    }
}