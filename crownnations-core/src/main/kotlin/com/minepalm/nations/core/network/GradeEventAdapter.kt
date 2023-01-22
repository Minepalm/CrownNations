package com.minepalm.nations.core.network

import com.minepalm.library.network.api.HelloAdapter
import io.netty.buffer.ByteBuf

sealed class GradeEventAdapter {

    class PromoteEvent : HelloAdapter<com.minepalm.nations.event.NationPromoteEvent>("NationPromoteEvent") {

        override fun decode(buf: ByteBuf): com.minepalm.nations.event.NationPromoteEvent {
            val nationId = buf.readInt()
            val currentLevel = buf.readInt()
            val nextLevel = buf.readInt()
            val successful = buf.readBoolean()
            val message = buf.readString()
            val commander = buf.readUUID()
            val time = buf.readLong()
            return com.minepalm.nations.event.NationPromoteEvent(
                nationId,
                commander,
                currentLevel,
                nextLevel,
                com.minepalm.nations.grade.PromoteResult(successful, message),
                false,
                time
            )
        }

        override fun encode(buf: ByteBuf, event: com.minepalm.nations.event.NationPromoteEvent) {
            buf.writeInt(event.nationId)
            buf.writeInt(event.currentLevel)
            buf.writeInt(event.nextLevel)
            buf.writeBoolean(event.result.successful)
            buf.writeString(event.result.message)
            buf.writeUUID(event.commander)
            buf.writeLong(event.time)
        }

    }

    class UpdateEvent : HelloAdapter<com.minepalm.nations.event.NationGradeUpdateEvent>("NationGradeUpdateEvent") {

        override fun decode(buf: ByteBuf): com.minepalm.nations.event.NationGradeUpdateEvent {
            val nationId = buf.readInt()
            val level = buf.readInt()
            return com.minepalm.nations.event.NationGradeUpdateEvent(nationId, level)
        }

        override fun encode(buf: ByteBuf, event: com.minepalm.nations.event.NationGradeUpdateEvent) {
            buf.writeInt(event.nationId)
            buf.writeInt(event.level)
        }

    }
}