package com.minepalm.nations.core.network

import com.minepalm.library.network.api.HelloAdapter
import com.minepalm.library.network.impl.server.ByteBufUtils
import io.netty.buffer.ByteBuf

sealed class TerritoryEventAdapter {

    companion object {

        fun ByteBuf.writeServerLoc(loc: com.minepalm.nations.utils.ServerLoc) {
            ByteBufUtils.writeString(this, loc.server)
            ByteBufUtils.writeString(this, loc.world)
            writeInt(loc.x)
            writeInt(loc.y)
            writeInt(loc.z)
        }

        fun ByteBuf.readServerLoc(): com.minepalm.nations.utils.ServerLoc {
            val server = ByteBufUtils.readString(this)
            val world = ByteBufUtils.readString(this)
            val x = readInt()
            val y = readInt()
            val z = readInt()
            return com.minepalm.nations.utils.ServerLoc(server, world, x, y, z)
        }

    }

    class Decompose : HelloAdapter<com.minepalm.nations.event.TerritoryDecomposeEvent>("TerritoryDecomposeEvent") {

        override fun decode(buf: ByteBuf): com.minepalm.nations.event.TerritoryDecomposeEvent {
            val nationId = buf.readInt()
            val monumentId = buf.readInt()
            val monumentType = ByteBufUtils.readString(buf)
            val loc = buf.readServerLoc()
            val time = buf.readLong()
            return com.minepalm.nations.event.TerritoryDecomposeEvent(
                nationId,
                monumentId,
                monumentType,
                loc,
                false,
                time
            )

        }

        override fun encode(buf: ByteBuf, event: com.minepalm.nations.event.TerritoryDecomposeEvent) {
            buf.writeInt(event.nationId)
            buf.writeInt(event.monumentId)
            ByteBufUtils.writeString(buf, event.monumentType)
            buf.writeServerLoc(event.location)
            buf.writeLong(event.time)
        }
    }

    class PostClaim : HelloAdapter<com.minepalm.nations.event.TerritoryPostClaimEvent>("TerritoryPostClaimEvent") {

        override fun decode(buf: ByteBuf): com.minepalm.nations.event.TerritoryPostClaimEvent {
            val nationId = buf.readInt()
            val monumentId = buf.readInt()
            val monumentType = ByteBufUtils.readString(buf)
            val loc = buf.readServerLoc()
            val time = buf.readLong()
            return com.minepalm.nations.event.TerritoryPostClaimEvent(
                nationId,
                monumentId,
                monumentType,
                loc,
                false,
                time
            )
        }

        override fun encode(buf: ByteBuf, event: com.minepalm.nations.event.TerritoryPostClaimEvent) {
            buf.writeInt(event.nationId)
            buf.writeInt(event.monumentId)
            ByteBufUtils.writeString(buf, event.type)
            buf.writeServerLoc(event.location)
            buf.writeLong(event.time)
        }

    }

    class WorldLoad : HelloAdapter<com.minepalm.nations.event.TerritoryWorldLoadEvent>("TerritoryWorldLoadEvent") {

        override fun decode(buf: ByteBuf): com.minepalm.nations.event.TerritoryWorldLoadEvent {
            val server = ByteBufUtils.readString(buf)
            val world = ByteBufUtils.readString(buf)
            val time = buf.readLong()
            return com.minepalm.nations.event.TerritoryWorldLoadEvent(server, world, time)
        }

        override fun encode(buf: ByteBuf, event: com.minepalm.nations.event.TerritoryWorldLoadEvent) {
            ByteBufUtils.writeString(buf, event.server)
            ByteBufUtils.writeString(buf, event.worldName)
            buf.writeLong(event.time)
        }

    }
}