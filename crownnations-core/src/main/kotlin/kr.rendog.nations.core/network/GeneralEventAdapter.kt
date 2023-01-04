package kr.rendog.nations.core.network

import com.minepalm.library.network.api.HelloAdapter
import io.netty.buffer.ByteBuf
import kr.rendog.nations.Nation
import kr.rendog.nations.NationRank
import kr.rendog.nations.NationService
import kr.rendog.nations.event.*

sealed class GeneralEventAdapter {

    companion object{

        internal fun ByteBuf.writeNation(nation : Nation){
            writeInt(nation.id)
        }

        internal fun ByteBuf.readNation(service : NationService) : Int{
            return readInt()
        }
    }

    class AddMember(
        private val service : NationService
    ) : HelloAdapter<NationAddMemberEvent>(NationAddMemberEvent::class.java.simpleName) {

        override fun decode(buf : ByteBuf): NationAddMemberEvent {
            val nation = buf.readNation(service)
            val commander = buf.readUUID()
            val adder = buf.readUUID()
            val cancelled = buf.readBoolean()
            val time = buf.readLong()
            return NationAddMemberEvent(nation, commander, adder, cancelled, time)
        }

        override fun encode(buf : ByteBuf, event : NationAddMemberEvent) {
            buf.writeInt(event.nationId)
            buf.writeUUID(event.commander)
            buf.writeUUID(event.userId)
            buf.writeBoolean(event.cancelled)
            buf.writeLong(event.time)
        }

    }
    class RemoveMember(
        private val service : NationService
    ) : HelloAdapter<NationRemoveMemberEvent>(NationRemoveMemberEvent::class.java.simpleName){

        override fun decode(buf : ByteBuf): NationRemoveMemberEvent {
            val nation = buf.readNation(service)
            val commander = buf.readUUID()
            val remover = buf.readUUID()
            val reason = buf.readString()
            val cancelled = buf.readBoolean()
            val time = buf.readLong()
            return NationRemoveMemberEvent(nation, commander, remover, reason, cancelled, time)
        }

        override fun encode(buf : ByteBuf, event : NationRemoveMemberEvent) {
            buf.writeInt(event.nationId)
            buf.writeUUID(event.commander)
            buf.writeUUID(event.removerId)
            buf.writeString(event.reason)
            buf.writeBoolean(event.cancelled)
            buf.writeLong(event.time)
        }

    }

    class Create(
        private val service : NationService
    ) : HelloAdapter<NationCreateEvent>(NationCreateEvent::class.java.simpleName){

        override fun decode(buf : ByteBuf): NationCreateEvent {
            val nation = buf.readNation(service)
            val founder = buf.readUUID()
            val time = buf.readLong()
            return NationCreateEvent(nation, founder, time)
        }

        override fun encode(buf : ByteBuf, event : NationCreateEvent) {
            buf.writeInt(event.nationId)
            buf.writeUUID(event.founder)
            buf.writeLong(event.time)
        }

    }

    class Disband(
        private val service : NationService
    ) : HelloAdapter<NationDisbandEvent>(NationDisbandEvent::class.java.simpleName){

        override fun decode(buf : ByteBuf): NationDisbandEvent {
            val nation = buf.readNation(service)
            val name = buf.readString()
            val commander = buf.readUUID()
            val cancelled = buf.readBoolean()
            val time = buf.readLong()
            return NationDisbandEvent(nation, name, commander, cancelled, time)
        }

        override fun encode(buf : ByteBuf, event : NationDisbandEvent) {
            buf.writeInt(event.nationId)
            buf.writeString(event.nationName)
            buf.writeUUID(event.commander)
            buf.writeBoolean(event.cancelled)
            buf.writeLong(event.time)
        }

    }

    class SetRank(
        private val service : NationService
    ) : HelloAdapter<NationSetRankEvent>(NationSetRankEvent::class.java.simpleName){

        override fun decode(buf : ByteBuf): NationSetRankEvent {
            val nation = buf.readNation(service)
            val commander = buf.readUUID()
            val user = buf.readUUID()
            val rank = NationRank.Finder.by(buf.readInt())
            val cancelled = buf.readBoolean()
            val time = buf.readLong()
            return NationSetRankEvent(nation, commander, user, rank, cancelled, time)
        }

        override fun encode(buf : ByteBuf, event : NationSetRankEvent) {
            buf.writeInt(event.nationId)
            buf.writeUUID(event.commander)
            buf.writeUUID(event.user)
            buf.writeInt(event.rank.weight)
            buf.writeBoolean(event.cancelled)
            buf.writeLong(event.time)
        }

    }

    class Transfer(
        private val service : NationService
    ) : HelloAdapter<NationTransferEvent>(NationTransferEvent::class.java.simpleName){

        override fun decode(buf : ByteBuf): NationTransferEvent {
            val nation = buf.readNation(service)
            val transferFrom = buf.readUUID()
            val transferTo = buf.readUUID()
            val cancelled = buf.readBoolean()
            val time = buf.readLong()
            return NationTransferEvent(nation, transferFrom, transferTo, cancelled, time)
        }

        override fun encode(buf : ByteBuf, event : NationTransferEvent) {
            buf.writeInt(event.nationId)
            buf.writeUUID(event.transferFrom)
            buf.writeUUID(event.transferTo)
            buf.writeBoolean(event.cancelled)
            buf.writeLong(event.time)
        }

    }

    class Update : HelloAdapter<NationUpdateEvent>("NationUpdateEvent"){

        override fun decode(buf: ByteBuf): NationUpdateEvent {
            return NationUpdateEvent(buf.readInt())
        }

        override fun encode(buf: ByteBuf, event: NationUpdateEvent) {
            buf.writeInt(event.nationId)
        }
    }
}