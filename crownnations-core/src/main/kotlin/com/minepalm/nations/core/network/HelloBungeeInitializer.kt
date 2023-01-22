package com.minepalm.nations.core.network

import com.minepalm.library.network.api.PalmNetwork
import com.minepalm.nations.NationService
import com.minepalm.nations.core.NationAdmins
import com.minepalm.nations.core.PalmNationsService

object HelloBungeeInitializer {

    fun apply(network: PalmNetwork, service: NationService) {
        val gateway = EventBusGateway(service.remoteEventBus, service.network)
        network.gateway.apply {
            registerAdapter(GeneralEventAdapter.Create(service))
            registerAdapter(GeneralEventAdapter.Disband(service))
            registerAdapter(GeneralEventAdapter.AddMember(service))
            registerAdapter(GeneralEventAdapter.RemoveMember(service))
            registerAdapter(GeneralEventAdapter.SetRank(service))
            registerAdapter(GeneralEventAdapter.Transfer(service))
            registerAdapter(GeneralEventAdapter.Update())
            registerAdapter(ServerRegistrationUpdate.Adapter)
            registerAdapter(MemberUpdate.Adapter())
            registerAdapter(GradeEventAdapter.PromoteEvent())
            registerAdapter(GradeEventAdapter.UpdateEvent())
            registerAdapter(TerritoryEventAdapter.PostClaim())
            registerAdapter(TerritoryEventAdapter.Decompose())
            registerAdapter(TerritoryEventAdapter.WorldLoad())
            registerAdapter(NationAdmins.UpdateSignal.Adapter)
        }
        network.handler.apply {
            registerExecutor(EventExecutor.AddMember(gateway))
            registerExecutor(EventExecutor.RemoveMember(gateway))
            registerExecutor(EventExecutor.Create(gateway))
            registerExecutor(EventExecutor.Disband(gateway))
            registerExecutor(EventExecutor.SetRank(gateway))
            registerExecutor(EventExecutor.MetadataChange(gateway))
            registerExecutor(EventExecutor.Transfer(gateway))
            registerExecutor(EventExecutor.Update(gateway))
            registerExecutor(ServerRegistrationUpdate.Executor(service.network))
            registerExecutor(MemberUpdate.Executor(service))
            registerExecutor(EventExecutor.Claim(gateway))
            registerExecutor(EventExecutor.Decompose(gateway))
            registerExecutor(EventExecutor.Promote(gateway))
            registerExecutor(EventExecutor.GradeUpdate(gateway))
            registerExecutor(NationAdmins.UpdateSignal.Executor((service as PalmNationsService).admins))
        }
    }
}