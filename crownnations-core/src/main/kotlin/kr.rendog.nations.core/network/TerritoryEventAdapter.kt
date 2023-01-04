package kr.rendog.nations.core.network

import com.minepalm.library.network.api.HelloAdapter
import com.minepalm.library.network.server.ByteBufUtils
import io.netty.buffer.ByteBuf
import kr.rendog.nations.event.TerritoryDecomposeEvent
import kr.rendog.nations.event.TerritoryPostClaimEvent
import kr.rendog.nations.event.TerritoryWorldLoadEvent
import kr.rendog.nations.utils.ServerLoc

sealed class TerritoryEventAdapter {

    companion object{

        fun ByteBuf.writeServerLoc(loc: ServerLoc){
            ByteBufUtils.writeString(this, loc.server)
            ByteBufUtils.writeString(this, loc.world)
            writeInt(loc.x)
            writeInt(loc.y)
            writeInt(loc.z)
        }

        fun ByteBuf.readServerLoc(): ServerLoc{
            val server = ByteBufUtils.readString(this)
            val world = ByteBufUtils.readString(this)
            val x = readInt()
            val y = readInt()
            val z = readInt()
            return ServerLoc(server, world, x, y, z)
        }

    }

    class Decompose : HelloAdapter<TerritoryDecomposeEvent>("TerritoryDecomposeEvent") {

        override fun decode(buf: ByteBuf): TerritoryDecomposeEvent {
            val nationId = buf.readInt()
            val monumentId = buf.readInt()
            val monumentType = ByteBufUtils.readString(buf)
            val loc = buf.readServerLoc()
            val time = buf.readLong()
            return TerritoryDecomposeEvent(nationId, monumentId, monumentType, loc, false, time)

        }

        override fun encode(buf: ByteBuf, event: TerritoryDecomposeEvent) {
            buf.writeInt(event.nationId)
            buf.writeInt(event.monumentId)
            ByteBufUtils.writeString(buf, event.monumentType)
            buf.writeServerLoc(event.location)
            buf.writeLong(event.time)
        }
    }
    
    class PostClaim : HelloAdapter<TerritoryPostClaimEvent>("TerritoryPostClaimEvent") {

        override fun decode(buf: ByteBuf): TerritoryPostClaimEvent {
            val nationId = buf.readInt()
            val monumentId = buf.readInt()
            val monumentType = ByteBufUtils.readString(buf)
            val loc = buf.readServerLoc()
            val time = buf.readLong()
            return TerritoryPostClaimEvent(nationId, monumentId, monumentType, loc, false, time)
        }

        override fun encode(buf: ByteBuf, event: TerritoryPostClaimEvent) {
            buf.writeInt(event.nationId)
            buf.writeInt(event.monumentId)
            ByteBufUtils.writeString(buf, event.type)
            buf.writeServerLoc(event.location)
            buf.writeLong(event.time)
        }

    }
    
    class WorldLoad : HelloAdapter<TerritoryWorldLoadEvent>("TerritoryWorldLoadEvent") {

        override fun decode(buf: ByteBuf): TerritoryWorldLoadEvent {
            val server = ByteBufUtils.readString(buf)
            val world = ByteBufUtils.readString(buf)
            val time = buf.readLong()
            return TerritoryWorldLoadEvent(server, world, time)
        }

        override fun encode(buf: ByteBuf, event: TerritoryWorldLoadEvent) {
            ByteBufUtils.writeString(buf, event.server)
            ByteBufUtils.writeString(buf, event.worldName)
            buf.writeLong(event.time)
        }

    }
}