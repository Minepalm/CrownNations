package com.minepalm.nations.bukkit.listener.nation

import com.minepalm.nations.NationService
import com.minepalm.nations.bukkit.NetworkBroadcaster
import com.minepalm.nations.bukkit.PlayerCache
import com.minepalm.nations.bukkit.message.PrinterRegistry

object AlertListenerInitializer {

    fun init(service: NationService, players: PlayerCache, reg: PrinterRegistry, broadcaster: NetworkBroadcaster){
        service.localEventBus.run {
            addEventFinalizer(
                com.minepalm.nations.event.NationCreateEvent::class.java,
                AlertCreate(service, players, reg["CREATE"], broadcaster))
            addEventFinalizer(
                com.minepalm.nations.event.NationDisbandEvent::class.java,
                AlertDisband(service, players, reg["DISBAND"], broadcaster))
            addEventFinalizer(
                com.minepalm.nations.event.NationAddMemberEvent::class.java,
                AlertAddMember(service, players, reg["ADD_MEMBER"], broadcaster))
            addEventFinalizer(
                com.minepalm.nations.event.NationRemoveMemberEvent::class.java,
                AlertKick(service, players, reg["REMOVE_MEMBER"], broadcaster))
            addEventFinalizer(
                com.minepalm.nations.event.NationRemoveMemberEvent::class.java,
                AlertLeave(service, players, reg["REMOVE_MEMBER"], broadcaster))
            addEventFinalizer(
                com.minepalm.nations.event.NationSetRankEvent::class.java,
                AlertSetRank(service, players, reg["SET_RANK"], broadcaster))
            addEventFinalizer(
                com.minepalm.nations.event.NationTransferEvent::class.java,
                AlertTransfer(service, players, reg["TRANSFER"], broadcaster)
            )
        }
    }
}