package com.minepalm.nations.core.network

import com.minepalm.library.network.api.HelloExecutor
import com.minepalm.nations.event.*

sealed class EventExecutor {

    class AddMember(
        val gateway: EventBusGateway
    ) : HelloExecutor<com.minepalm.nations.event.NationAddMemberEvent> {

        override val identifier: String = com.minepalm.nations.event.NationAddMemberEvent::class.java.simpleName

        override fun executeReceived(event: com.minepalm.nations.event.NationAddMemberEvent) {
            gateway.handle(event)
        }

    }

    class Create(
        val gateway: EventBusGateway
    ) : HelloExecutor<NationCreateEvent> {
        override val identifier: String = NationCreateEvent::class.java.simpleName

        override fun executeReceived(event: NationCreateEvent) {
            gateway.handle(event)
        }

    }

    class Disband(
        val gateway: EventBusGateway
    ) : HelloExecutor<NationDisbandEvent> {
        override val identifier: String = NationDisbandEvent::class.java.simpleName

        override fun executeReceived(event: NationDisbandEvent) {
            gateway.handle(event)
        }

    }

    class MetadataChange(
        val gateway: EventBusGateway
    ) : HelloExecutor<NationMetadataChangeEvent> {
        override val identifier: String = NationMetadataChangeEvent::class.java.simpleName


        override fun executeReceived(event: NationMetadataChangeEvent) {
            gateway.handle(event)
        }

    }

    class RemoveMember(
        val gateway: EventBusGateway
    ) : HelloExecutor<NationRemoveMemberEvent> {

        override val identifier: String = NationRemoveMemberEvent::class.java.simpleName

        override fun executeReceived(event: NationRemoveMemberEvent) {
            gateway.handle(event)
        }

    }

    class SetRank(
        val gateway: EventBusGateway
    ) : HelloExecutor<NationSetRankEvent> {
        override val identifier: String = NationSetRankEvent::class.java.simpleName

        override fun executeReceived(event: NationSetRankEvent) {
            gateway.handle(event)
        }

    }

    class Update(
        val gateway: EventBusGateway
    ) : HelloExecutor<NationUpdateEvent> {
        override val identifier: String = "NationUpdateEvent"

        override fun executeReceived(event: NationUpdateEvent) {
            gateway.handle(event)
        }

    }

    class Transfer(
        val gateway: EventBusGateway
    ) : HelloExecutor<NationTransferEvent> {
        override val identifier: String = NationTransferEvent::class.java.simpleName

        override fun executeReceived(event: NationTransferEvent) {
            gateway.handle(event)
        }

    }

    class GradeUpdate(
        val gateway: EventBusGateway
    ) : HelloExecutor<NationGradeUpdateEvent> {
        override val identifier: String = "NationGradeUpdateEvent"

        override fun executeReceived(p0: NationGradeUpdateEvent) {
            gateway.handle(p0)
        }

    }

    class Promote(
        val gateway: EventBusGateway
    ) : HelloExecutor<NationPromoteEvent> {
        override val identifier: String = "NationPromoteEvent"

        override fun executeReceived(p0: NationPromoteEvent) {
            gateway.handle(p0)
        }

    }

    class Claim(
        val gateway: EventBusGateway
    ) : HelloExecutor<TerritoryPostClaimEvent> {
        override val identifier: String = "TerritoryPostClaimEvent"

        override fun executeReceived(p0: TerritoryPostClaimEvent) {
            gateway.handle(p0)
        }

    }

    class Decompose(
        val gateway: EventBusGateway
    ) : HelloExecutor<TerritoryDecomposeEvent> {
        override val identifier: String = "TerritoryDecomposeEvent"

        override fun executeReceived(p0: TerritoryDecomposeEvent) {
            gateway.handle(p0)
        }

    }

}