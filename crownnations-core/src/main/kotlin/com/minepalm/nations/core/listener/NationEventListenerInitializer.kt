package com.minepalm.nations.core.listener

import com.minepalm.nations.NationService
import com.minepalm.nations.event.*
import java.util.concurrent.ExecutorService

object NationEventListenerInitializer {

    fun init(service: NationService, executor: ExecutorService) {
        service.remoteEventBus.run {
            addEventInitializer(NationCreateEvent::class.java, SyncCreate(service, executor))
            addEventInitializer(NationDisbandEvent::class.java, SyncDisband(service, executor))
            addEventInitializer(NationMetadataChangeEvent::class.java, SyncMetadataChange(service, executor))
            addEventInitializer(
                NationAddMemberEvent::class.java,
                SyncAddMember(service, executor)
            )
            addEventInitializer(NationRemoveMemberEvent::class.java, SyncRemoveMember(service, executor))
            addEventInitializer(
                NationSetRankEvent::class.java,
                SyncSetRank(service, executor)
            )
            addEventInitializer(
                NationTransferEvent::class.java,
                SyncTransfer(service, executor)
            )
            addEventInitializer(NationUpdateEvent::class.java, SyncUpdate(service, executor))
            addEventInitializer(
                NationGradeUpdateEvent::class.java,
                SyncGrade.Update(service)
            )
            addEventInitializer(NationPromoteEvent::class.java, SyncGrade.Promote(service))

            addEventInitializer(
                TerritoryPostClaimEvent::class.java,
                SyncTerritory.Claim(service)
            )
            addEventInitializer(
                TerritoryDecomposeEvent::class.java,
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