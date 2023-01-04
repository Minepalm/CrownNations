package kr.rendog.nations.core.network

import com.minepalm.library.network.api.PalmNetwork
import kr.rendog.nations.core.mysql.MySQLNationServerDatabase
import kr.rendog.nations.event.NationEvent
import kr.rendog.nations.server.NationNetwork
import kr.rendog.nations.server.NationServer
import java.util.concurrent.ConcurrentHashMap

class RendogNationNetwork(
    val network : PalmNetwork,
    val database : MySQLNationServerDatabase
) : NationNetwork {

    private val hosted = network.name
    private val map = ConcurrentHashMap<String, NationServer>()

    override val host: NationServer
        get() = map[hosted]!!

    init{
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

    override fun register(server: NationServer) {
        map[server.name] = server
    }

    override fun getServer(name: String): NationServer? {
        return map[name]
    }

    override fun shutdown() {
        database.setOnline(hosted, false).join()
    }

    override fun update(name : String){
        map[name] = toNationServer(name)
    }

    private fun toNationServer(name : String) : NationServer{
        return RendogNationServer(name, network.sender(name), network.connections.getClient(name), database)
    }
}