package com.minepalm.nations.core.network

import com.minepalm.library.network.api.HelloExecutor

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
    ) : HelloExecutor<com.minepalm.nations.event.NationCreateEvent> {
        override val identifier: String = com.minepalm.nations.event.NationCreateEvent::class.java.simpleName

        override fun executeReceived(event: com.minepalm.nations.event.NationCreateEvent) {
            gateway.handle(event)
        }

    }

    class Disband(
        val gateway: EventBusGateway
    ) : HelloExecutor<com.minepalm.nations.event.NationDisbandEvent> {
        override val identifier: String = com.minepalm.nations.event.NationDisbandEvent::class.java.simpleName

        override fun executeReceived(event: com.minepalm.nations.event.NationDisbandEvent) {
            gateway.handle(event)
        }

    }

    class MetadataChange(
        val gateway: EventBusGateway
    ) : HelloExecutor<com.minepalm.nations.event.NationMetadataChangeEvent> {
        override val identifier: String = com.minepalm.nations.event.NationMetadataChangeEvent::class.java.simpleName


        override fun executeReceived(event: com.minepalm.nations.event.NationMetadataChangeEvent) {
            gateway.handle(event)
        }

    }

    class RemoveMember(
        val gateway: EventBusGateway
    ) : HelloExecutor<com.minepalm.nations.event.NationRemoveMemberEvent> {

        override val identifier: String = com.minepalm.nations.event.NationRemoveMemberEvent::class.java.simpleName

        override fun executeReceived(event: com.minepalm.nations.event.NationRemoveMemberEvent) {
            gateway.handle(event)
        }

    }

    class SetRank(
        val gateway: EventBusGateway
    ) : HelloExecutor<com.minepalm.nations.event.NationSetRankEvent> {
        override val identifier: String = com.minepalm.nations.event.NationSetRankEvent::class.java.simpleName

        override fun executeReceived(event: com.minepalm.nations.event.NationSetRankEvent) {
            gateway.handle(event)
        }

    }

    class Update(
        val gateway: EventBusGateway
    ) : HelloExecutor<com.minepalm.nations.event.NationUpdateEvent> {
        override val identifier: String = "NationUpdateEvent"

        override fun executeReceived(event: com.minepalm.nations.event.NationUpdateEvent) {
            gateway.handle(event)
        }

    }

    class Transfer(
        val gateway: EventBusGateway
    ) : HelloExecutor<com.minepalm.nations.event.NationTransferEvent> {
        override val identifier: String = com.minepalm.nations.event.NationTransferEvent::class.java.simpleName

        override fun executeReceived(event: com.minepalm.nations.event.NationTransferEvent) {
            gateway.handle(event)
        }

    }

    class GradeUpdate(
        val gateway: EventBusGateway
    ) : HelloExecutor<com.minepalm.nations.event.NationGradeUpdateEvent> {
        override val identifier: String = "NationGradeUpdateEvent"

        override fun executeReceived(p0: com.minepalm.nations.event.NationGradeUpdateEvent) {
            gateway.handle(p0)
        }

    }

    class Promote(
        val gateway: EventBusGateway
    ) : HelloExecutor<com.minepalm.nations.event.NationPromoteEvent> {
        override val identifier: String = "NationPromoteEvent"

        override fun executeReceived(p0: com.minepalm.nations.event.NationPromoteEvent) {
            gateway.handle(p0)
        }

    }

    class Claim(
        val gateway: EventBusGateway
    ) : HelloExecutor<com.minepalm.nations.event.TerritoryPostClaimEvent> {
        override val identifier: String = "TerritoryPostClaimEvent"

        override fun executeReceived(p0: com.minepalm.nations.event.TerritoryPostClaimEvent) {
            gateway.handle(p0)
        }

    }

    class Decompose(
        val gateway: EventBusGateway
    ) : HelloExecutor<com.minepalm.nations.event.TerritoryDecomposeEvent> {
        override val identifier: String = "TerritoryDecomposeEvent"

        override fun executeReceived(p0: com.minepalm.nations.event.TerritoryDecomposeEvent) {
            gateway.handle(p0)
        }

    }

}