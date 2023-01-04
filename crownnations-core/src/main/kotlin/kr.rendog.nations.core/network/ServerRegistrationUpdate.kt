package kr.rendog.nations.core.network

import com.minepalm.library.network.api.HelloAdapter
import com.minepalm.library.network.api.HelloExecutor
import io.netty.buffer.ByteBuf
import kr.rendog.nations.server.NationNetwork

class ServerRegistrationUpdate(
    val name : String
) {

    object Adapter : HelloAdapter<ServerRegistrationUpdate>(ServerRegistrationUpdate::class.java.simpleName) {

        override fun decode(buf : ByteBuf): ServerRegistrationUpdate {
            val name = buf.readString()
            return ServerRegistrationUpdate(name)
        }

        override fun encode(buf : ByteBuf, update: ServerRegistrationUpdate) {
            buf.writeString(update.name)
        }

    }

    class Executor(private val network : NationNetwork) : HelloExecutor<ServerRegistrationUpdate> {
        override val identifier: String = ServerRegistrationUpdate::class.java.simpleName

        override fun executeReceived(update : ServerRegistrationUpdate) {
            network.update(update.name)
        }

    }
}