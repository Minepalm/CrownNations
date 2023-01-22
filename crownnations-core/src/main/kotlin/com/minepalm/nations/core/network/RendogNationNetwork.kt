package com.minepalm.nations.core.network

import com.minepalm.library.network.api.PalmNetwork
import com.minepalm.nations.core.mysql.MySQLNationServerDatabase
import java.util.concurrent.ConcurrentHashMap

class RendogNationNetwork(
    val network: PalmNetwork,
    val database: MySQLNationServerDatabase
) : com.minepalm.nations.server.NationNetwork {

    private val hosted = network.name
    private val map = ConcurrentHashMap<String, com.minepalm.nations.server.NationServer>()

    override val host: com.minepalm.nations.server.NationServer
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

    override fun getServers(): Map<String, com.minepalm.nations.server.NationServer> {
        return mutableMapOf<String, com.minepalm.nations.server.NationServer>().apply { putAll(map) }
    }

    override fun send(event: com.minepalm.nations.event.NationEvent) {
        System.out.println("try send event: $event")
        broadcast(event)
    }

    override fun broadcast(any: Any) {
        map.values.forEach {
            try {
                System.out.println("try send to ${it.name}")
                it.send(any)
            } catch (ex: Throwable) {
                //todo: report bug
            }
        }
    }

    override fun register(server: com.minepalm.nations.server.NationServer) {
        map[server.name] = server
    }

    override fun getServer(name: String): com.minepalm.nations.server.NationServer? {
        return map[name]
    }

    override fun shutdown() {
        database.setOnline(hosted, false).join()
    }

    override fun update(name: String) {
        map[name] = toNationServer(name)
    }

    private fun toNationServer(name: String): com.minepalm.nations.server.NationServer {
        return RendogNationServer(name, network.sender(name), network.connections.getClient(name), database)
    }
}