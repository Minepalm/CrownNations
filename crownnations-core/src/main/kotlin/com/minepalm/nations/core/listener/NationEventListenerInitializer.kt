package com.minepalm.nations.core.listener

import com.minepalm.nations.NationService
import java.util.concurrent.ExecutorService

object NationEventListenerInitializer {

    fun init(service: NationService, executor: ExecutorService) {
        service.remoteEventBus.run {
            addEventInitializer(com.minepalm.nations.event.NationCreateEvent::class.java, SyncCreate(service, executor))
            addEventInitializer(
                com.minepalm.nations.event.NationDisbandEvent::class.java,
                SyncDisband(service, executor)
            )
            addEventInitializer(
                com.minepalm.nations.event.NationMetadataChangeEvent::class.java,
                SyncMetadataChange(service, executor)
            )
            addEventInitializer(
                com.minepalm.nations.event.NationAddMemberEvent::class.java,
                SyncAddMember(service, executor)
            )
            addEventInitializer(
                com.minepalm.nations.event.NationRemoveMemberEvent::class.java,
                SyncRemoveMember(service, executor)
            )
            addEventInitializer(
                com.minepalm.nations.event.NationSetRankEvent::class.java,
                SyncSetRank(service, executor)
            )
            addEventInitializer(
                com.minepalm.nations.event.NationTransferEvent::class.java,
                SyncTransfer(service, executor)
            )
            addEventInitializer(com.minepalm.nations.event.NationUpdateEvent::class.java, SyncUpdate(service, executor))
            addEventInitializer(
                com.minepalm.nations.event.NationGradeUpdateEvent::class.java,
                SyncGrade.Update(service)
            )
            addEventInitializer(com.minepalm.nations.event.NationPromoteEvent::class.java, SyncGrade.Promote(service))

            addEventInitializer(
                com.minepalm.nations.event.TerritoryPostClaimEvent::class.java,
                SyncTerritory.Claim(service)
            )
            addEventInitializer(
                com.minepalm.nations.event.TerritoryDecomposeEvent::class.java,
                SyncTerritory.Decompose(service)
            )

            //todo: 전쟁 구현하고 주석 풀기
            //addListener(WarEndEvent::class.java, WarSessionSubscribeListener.WarEnd(service.warService))
            //addListener(WarPostDeclarationEvent::class.java, WarSessionSubscribeListener.PostWar(service.warService))
            //addListener(WarEndEvent::class.java, WarSessionHandleListener.WarEnd(service.warService))
            //addListener(WarPostDeclarationEvent::class.java, WarSessionHandleListener.PostWar(service.warService))
        }
        service.localEventBus.run {
            //todo: 전쟁 구현하고 주석 풀기
            //addListener(WarMonumentDestroyEvent::class.java, WarMonumentDestroyListener(service))
            //addListener(WarTimeoutEvent::class.java, WarSessionHandleListener.WarTimeout(service.warService))
            //addListener(WarTimeoutEvent::class.java, WarSessionSubscribeListener.WarTimeout(service.warService))
        }
    }
}