package com.minepalm.nations.core.network

import com.minepalm.library.network.api.HelloAdapter
import com.minepalm.nations.Nation
import com.minepalm.nations.NationRank
import com.minepalm.nations.NationService
import io.netty.buffer.ByteBuf

sealed class GeneralEventAdapter {

    companion object {

        internal fun ByteBuf.writeNation(nation: Nation) {
            writeInt(nation.id)
        }

        internal fun ByteBuf.readNation(service: NationService): Int {
            return readInt()
        }
    }

    class AddMember(
        private val service: NationService
    ) : HelloAdapter<com.minepalm.nations.event.NationAddMemberEvent>(com.minepalm.nations.event.NationAddMemberEvent::class.java.simpleName) {

        override fun decode(buf: ByteBuf): com.minepalm.nations.event.NationAddMemberEvent {
            val nation = buf.readNation(service)
            val commander = buf.readUUID()
            val adder = buf.readUUID()
            val cancelled = buf.readBoolean()
            val time = buf.readLong()
            return com.minepalm.nations.event.NationAddMemberEvent(nation, commander, adder, cancelled, time)
        }

        override fun encode(buf: ByteBuf, event: com.minepalm.nations.event.NationAddMemberEvent) {
            buf.writeInt(event.nationId)
            buf.writeUUID(event.commander)
            buf.writeUUID(event.userId)
            buf.writeBoolean(event.cancelled)
            buf.writeLong(event.time)
        }

    }

    class RemoveMember(
        private val service: NationService
    ) : HelloAdapter<com.minepalm.nations.event.NationRemoveMemberEvent>(com.minepalm.nations.event.NationRemoveMemberEvent::class.java.simpleName) {

        override fun decode(buf: ByteBuf): com.minepalm.nations.event.NationRemoveMemberEvent {
            val nation = buf.readNation(service)
            val commander = buf.readUUID()
            val remover = buf.readUUID()
            val reason = buf.readString()
            val cancelled = buf.readBoolean()
            val time = buf.readLong()
            return com.minepalm.nations.event.NationRemoveMemberEvent(
                nation,
                commander,
                remover,
                reason,
                cancelled,
                time
            )
        }

        override fun encode(buf: ByteBuf, event: com.minepalm.nations.event.NationRemoveMemberEvent) {
            buf.writeInt(event.nationId)
            buf.writeUUID(event.commander)
            buf.writeUUID(event.removerId)
            buf.writeString(event.reason)
            buf.writeBoolean(event.cancelled)
            buf.writeLong(event.time)
        }

    }

    class Create(
        private val service: NationService
    ) : HelloAdapter<com.minepalm.nations.event.NationCreateEvent>(com.minepalm.nations.event.NationCreateEvent::class.java.simpleName) {

        override fun decode(buf: ByteBuf): com.minepalm.nations.event.NationCreateEvent {
            val nation = buf.readNation(service)
            val founder = buf.readUUID()
            val time = buf.readLong()
            return com.minepalm.nations.event.NationCreateEvent(nation, founder, time)
        }

        override fun encode(buf: ByteBuf, event: com.minepalm.nations.event.NationCreateEvent) {
            buf.writeInt(event.nationId)
            buf.writeUUID(event.founder)
            buf.writeLong(event.time)
        }

    }

    class Disband(
        private val service: NationService
    ) : HelloAdapter<com.minepalm.nations.event.NationDisbandEvent>(com.minepalm.nations.event.NationDisbandEvent::class.java.simpleName) {

        override fun decode(buf: ByteBuf): com.minepalm.nations.event.NationDisbandEvent {
            val nation = buf.readNation(service)
            val name = buf.readString()
            val commander = buf.readUUID()
            val cancelled = buf.readBoolean()
            val time = buf.readLong()
            return com.minepalm.nations.event.NationDisbandEvent(nation, name, commander, cancelled, time)
        }

        override fun encode(buf: ByteBuf, event: com.minepalm.nations.event.NationDisbandEvent) {
            buf.writeInt(event.nationId)
            buf.writeString(event.nationName)
            buf.writeUUID(event.commander)
            buf.writeBoolean(event.cancelled)
            buf.writeLong(event.time)
        }

    }

    class SetRank(
        private val service: NationService
    ) : HelloAdapter<com.minepalm.nations.event.NationSetRankEvent>(com.minepalm.nations.event.NationSetRankEvent::class.java.simpleName) {

        override fun decode(buf: ByteBuf): com.minepalm.nations.event.NationSetRankEvent {
            val nation = buf.readNation(service)
            val commander = buf.readUUID()
            val user = buf.readUUID()
            val rank = NationRank.Finder.by(buf.readInt())
            val cancelled = buf.readBoolean()
            val time = buf.readLong()
            return com.minepalm.nations.event.NationSetRankEvent(nation, commander, user, rank, cancelled, time)
        }

        override fun encode(buf: ByteBuf, event: com.minepalm.nations.event.NationSetRankEvent) {
            buf.writeInt(event.nationId)
            buf.writeUUID(event.commander)
            buf.writeUUID(event.user)
            buf.writeInt(event.rank.weight)
            buf.writeBoolean(event.cancelled)
            buf.writeLong(event.time)
        }

    }

    class Transfer(
        private val service: NationService
    ) : HelloAdapter<com.minepalm.nations.event.NationTransferEvent>(com.minepalm.nations.event.NationTransferEvent::class.java.simpleName) {

        override fun decode(buf: ByteBuf): com.minepalm.nations.event.NationTransferEvent {
            val nation = buf.readNation(service)
            val transferFrom = buf.readUUID()
            val transferTo = buf.readUUID()
            val cancelled = buf.readBoolean()
            val time = buf.readLong()
            return com.minepalm.nations.event.NationTransferEvent(nation, transferFrom, transferTo, cancelled, time)
        }

        override fun encode(buf: ByteBuf, event: com.minepalm.nations.event.NationTransferEvent) {
            buf.writeInt(event.nationId)
            buf.writeUUID(event.transferFrom)
            buf.writeUUID(event.transferTo)
            buf.writeBoolean(event.cancelled)
            buf.writeLong(event.time)
        }

    }

    class Update : HelloAdapter<com.minepalm.nations.event.NationUpdateEvent>("NationUpdateEvent") {

        override fun decode(buf: ByteBuf): com.minepalm.nations.event.NationUpdateEvent {
            return com.minepalm.nations.event.NationUpdateEvent(buf.readInt())
        }

        override fun encode(buf: ByteBuf, event: com.minepalm.nations.event.NationUpdateEvent) {
            buf.writeInt(event.nationId)
        }
    }
}