package kr.rendog.nations.core.network

import com.minepalm.library.network.api.HelloAdapter
import com.minepalm.library.network.api.HelloExecutor
import io.netty.buffer.ByteBuf
import kr.rendog.nations.NationService
import java.util.UUID

data class MemberUpdate(val uuid: UUID) {

    class Adapter : HelloAdapter<MemberUpdate>(MemberUpdate::class.java.simpleName) {

        override fun decode(buf: ByteBuf): MemberUpdate {
            return MemberUpdate(buf.readUUID())
        }

        override fun encode(buf: ByteBuf, t: MemberUpdate) {
            buf.writeUUID(t.uuid)
        }

    }

    class Executor(val service: NationService): HelloExecutor<MemberUpdate> {

        override val identifier: String = MemberUpdate::class.java.simpleName

        override fun executeReceived(t: MemberUpdate) {
            service.memberRegistry.update(t.uuid)
        }

    }
}