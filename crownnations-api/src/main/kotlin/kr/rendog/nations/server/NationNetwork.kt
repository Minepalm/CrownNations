package kr.rendog.nations.server

import kr.rendog.nations.event.NationEvent

interface NationNetwork {

    val host: NationServer

    fun getServers() : Map<String, NationServer>

    fun send(event : NationEvent)

    fun broadcast(any : Any)

    fun register(server : NationServer)

    fun getServer(name : String) : NationServer?

    fun update(name : String)

    fun shutdown()
}