package kr.rendog.nations.core.network

import com.minepalm.library.network.api.HelloClient
import com.minepalm.library.network.api.HelloSender
import kr.rendog.nations.core.mysql.MySQLNationServerDatabase
import kr.rendog.nations.server.NationServer
import java.util.concurrent.CompletableFuture

class RendogNationServer(
    override val name: String,
    private val sender : HelloSender,
    private val client : HelloClient,
    private val database : MySQLNationServerDatabase
    ) : NationServer {

    override fun isOnline(): CompletableFuture<Boolean> {
        return database.isOnline(name)
    }

    override fun isConnected(): Boolean {
        return client.isConnected
    }

    override fun send(obj: Any) {
        sender.send(obj)
    }

    override fun <T> callback(obj: Any, returnType: Class<T>): CompletableFuture<T> {
        //todo: Throw NPE
        return sender.callback(obj, returnType)?.async() ?: CompletableFuture.supplyAsync(null)
    }

    override fun setOnline(online: Boolean): CompletableFuture<Unit> {
        return database.setOnline(name, online)
    }
}