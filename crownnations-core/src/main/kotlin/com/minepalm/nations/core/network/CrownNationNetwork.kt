package com.minepalm.nations.core.network

import com.minepalm.library.network.api.PalmNetwork
import com.minepalm.nations.core.mysql.MySQLNationServerDatabase
import com.minepalm.nations.event.NationEvent
import com.minepalm.nations.server.NationNetwork
import com.minepalm.nations.server.NationServer
import java.util.concurrent.ConcurrentHashMap

class CrownNationNetwork(
    val network: PalmNetwork,
    val database: MySQLNationServerDatabase
) : NationNetwork {

    private val hosted = network.name
    private val map = ConcurrentHashMap<String, NationServer>()

    override val host: NationServer
        get() = map[hosted]!!

    init {
        database.setOnline(hosted, true).join()
        database.getServers().join().let {
            it.forEach { name ->
                System.out.println("register nation server: $name")
                register(toNationServer(name))
            }
        }
    }

    override fun getServers(): Map<String, NationServer> {
        return mutableMapOf<String, NationServer>().apply { putAll(map) }
    }

    override fun send(event: NationEvent) {
        broadcast(event)
    }

    override fun broadcast(any: Any) {
        map.values.forEach { it.send(any) }
    }

    override fun register(server: NationServer) {
        map[server.name] = server
    }

    override fun getServer(name: String): NationServer? {
        return map[name]
    }

    override fun shutdown() {
        database.setOnline(hosted, false).join()
    }

    override fun update(name: String) {
        map[name] = toNationServer(name)
    }

    private fun toNationServer(name: String): NationServer {
        return CrownNationServer(name, network.sender(name), network.connections.getClient(name), database)
    }
}