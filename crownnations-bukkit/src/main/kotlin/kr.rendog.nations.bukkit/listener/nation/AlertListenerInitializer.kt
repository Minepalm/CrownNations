package kr.rendog.nations.bukkit.listener.nation

import kr.rendog.nations.NationService
import kr.rendog.nations.bukkit.NetworkBroadcaster
import kr.rendog.nations.bukkit.PlayerCache
import kr.rendog.nations.bukkit.message.PrinterRegistry
import kr.rendog.nations.event.*

object AlertListenerInitializer {

    fun init(service: NationService, players: PlayerCache, reg: PrinterRegistry, broadcaster: NetworkBroadcaster){
        service.localEventBus.run {
            addEventFinalizer(
                NationCreateEvent::class.java,
                AlertCreate(service, players, reg["CREATE"], broadcaster))
            addEventFinalizer(
                NationDisbandEvent::class.java,
                AlertDisband(service, players, reg["DISBAND"], broadcaster))
            addEventFinalizer(
                NationAddMemberEvent::class.java,
                AlertAddMember(service, players, reg["ADD_MEMBER"], broadcaster))
            addEventFinalizer(
                NationRemoveMemberEvent::class.java,
                AlertKick(service, players, reg["REMOVE_MEMBER"], broadcaster))
            addEventFinalizer(
                NationRemoveMemberEvent::class.java,
                AlertLeave(service, players, reg["REMOVE_MEMBER"], broadcaster))
            addEventFinalizer(
                NationSetRankEvent::class.java,
                AlertSetRank(service, players, reg["SET_RANK"], broadcaster))
            addEventFinalizer(
                NationTransferEvent::class.java,
                AlertTransfer(service, players, reg["TRANSFER"], broadcaster)
            )
        }
    }
}