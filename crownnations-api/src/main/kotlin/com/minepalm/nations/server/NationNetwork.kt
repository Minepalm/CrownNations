package com.minepalm.nations.server

interface NationNetwork {

    val host: NationServer

    fun getServers(): Map<String, NationServer>

    fun send(event: com.minepalm.nations.event.NationEvent)

    fun broadcast(any: Any)

    fun register(server: NationServer)

    fun getServer(name: String): NationServer?

    fun update(name: String)

    fun shutdown()
}