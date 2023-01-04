package kr.rendog.nations.core.network

import com.minepalm.library.network.api.HelloAdapter
import io.netty.buffer.ByteBuf
import kr.rendog.nations.event.NationGradeUpdateEvent
import kr.rendog.nations.event.NationPromoteEvent
import kr.rendog.nations.grade.PromoteResult

sealed class GradeEventAdapter {

    class PromoteEvent : HelloAdapter<NationPromoteEvent>("NationPromoteEvent") {

        override fun decode(buf: ByteBuf): NationPromoteEvent {
            val nationId = buf.readInt()
            val currentLevel = buf.readInt()
            val nextLevel = buf.readInt()
            val successful = buf.readBoolean()
            val message = buf.readString()
            val commander = buf.readUUID()
            val time = buf.readLong()
            return NationPromoteEvent(nationId, commander, currentLevel, nextLevel, PromoteResult(successful, message), false, time)
        }

        override fun encode(buf: ByteBuf, event: NationPromoteEvent) {
            buf.writeInt(event.nationId)
            buf.writeInt(event.currentLevel)
            buf.writeInt(event.nextLevel)
            buf.writeBoolean(event.result.successful)
            buf.writeString(event.result.message)
            buf.writeUUID(event.commander)
            buf.writeLong(event.time)
        }

    }

    class UpdateEvent : HelloAdapter<NationGradeUpdateEvent>("NationGradeUpdateEvent") {

        override fun decode(buf: ByteBuf): NationGradeUpdateEvent {
            val nationId = buf.readInt()
            val level = buf.readInt()
            return NationGradeUpdateEvent(nationId, level)
        }

        override fun encode(buf: ByteBuf, event: NationGradeUpdateEvent) {
            buf.writeInt(event.nationId)
            buf.writeInt(event.level)
        }

    }
}