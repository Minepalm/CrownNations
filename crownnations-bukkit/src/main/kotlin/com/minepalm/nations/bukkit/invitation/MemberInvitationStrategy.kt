package com.minepalm.nations.bukkit.invitation

import com.minepalm.arkarangutils.invitation.Invitation
import com.minepalm.arkarangutils.invitation.InvitationExecuteStrategy
import com.minepalm.nations.NationService
import com.minepalm.nations.bukkit.PlayerCache
import com.minepalm.nations.bukkit.message.ResultMessage
import com.minepalm.nations.bukkit.message.ResultPrinter
import com.minepalm.palmchat.api.ChatService
import java.util.concurrent.ExecutorService

class MemberInvitationStrategy(
    private val service: NationService,
    private val players: PlayerCache,
    private val chatService: ChatService,
    private val printer: ResultPrinter,
    executor: ExecutorService
): InvitationExecuteStrategy(executor) {


    override fun onInvited(invitation: Invitation) {
        val issuerName = players.username(invitation.issuer)
        val receiverName = players.username(invitation.received)
        val nation = service.memberRegistry[invitation.issuer].direct.getNation().join() ?: return
        val issuerChannel = chatService.channelRegistry.channel("SYSTEM:${invitation.issuer}")
        val receiverChannel = chatService.channelRegistry.channel("SYSTEM:${invitation.received}")

        val map = mutableMapOf<String, String>().apply {
            set("player", issuerName!!)
            set("nation", nation.name)
            set("target", receiverName!!)
        }

        issuerChannel.session().system()
                .send(printer.chatText(ResultMessage("INVITE_SUCCESS", data = map)))

        receiverChannel.session().system()
                .send(printer.chatText(ResultMessage("INVITE_RECEIVE", data = map)))
    }

    override fun onAccept(invitation: Invitation) {
        val issuerName = players.username(invitation.issuer)
        val receiverName = players.username(invitation.received)
        val nation = service.memberRegistry[invitation.issuer].direct.getNation().join()
        val issuerChannel = chatService.channelRegistry.channel("SYSTEM:${invitation.issuer}")
        val receiverChannel = chatService.channelRegistry.channel("SYSTEM:${invitation.received}")

        val map = mutableMapOf<String, String>().apply {
            set("player", issuerName!!)
            set("target", receiverName!!)
        }

        if(nation == null){
            receiverChannel.session().system()
                    .send(printer.chatText(ResultMessage("INVALIDATE_INVITATION", data = map)))
            return
        }

        map.apply {
            set("nation", nation.name)
        }

        val result = nation.operateAddMember(service.memberRegistry[invitation.issuer], invitation.received).process()

        val messageCode = when{
            result.code == "SUCCESSFUL" -> "INVITE_ACCEPT"
            result.code == "NO_NATION" ->  "INVALIDATE_INVITATION"
            result.code == "NATION_MISMATCH" -> "INVALIDATE_INVITATION"
            printer.containsMessage(result.code) -> result.code
            result.code == "EVENT_CANCELLED" -> "EVENT_CANCELLED"
            else -> "ERROR"
        }

        if(messageCode != "EVENT_CANCELLED") {

            if (messageCode == "INVITE_ACCEPT") {
                issuerChannel.session().system()
                        .send(printer.chatText(ResultMessage("ALERT_ACCEPT", data = map)))

            }

            receiverChannel.session().system()
                    .send(printer.chatText(ResultMessage(messageCode, data = map)))

        }
    }

    override fun onDeny(invitation: Invitation) {
        val issuerName = players.username(invitation.issuer)
        val receiverName = players.username(invitation.received)
        val nation = service.memberRegistry[invitation.issuer].cache.nation
        val issuerChannel = chatService.channelRegistry.channel("SYSTEM:${invitation.issuer}")
        val receiverChannel = chatService.channelRegistry.channel("SYSTEM:${invitation.received}")

        val map = mutableMapOf<String, String>().apply {
            set("player", issuerName!!)
            set("target", receiverName!!)
        }

        if(nation == null){
            receiverChannel.also {
                it.session().system()
                    .send(printer.chatText(ResultMessage("INVALIDATE_INVITATION", data = map)))
            }
            return
        }

        map.apply {
            set("nation", nation.name)
        }

        issuerChannel.session().system().send(printer.chatText(ResultMessage("ALERT_DENY", data = map)))
        receiverChannel.session().system().send(printer.chatText(ResultMessage("INVITE_DENY", data = map)))
    }

    override fun onTimeout(invitation: Invitation) {
        val issuerName = players.username(invitation.issuer)
        val receiverName = players.username(invitation.received)
        val nation = service.memberRegistry[invitation.issuer].direct.getNation().join() ?: return
        val issuerChannel = chatService.channelRegistry.channel("SYSTEM:${invitation.issuer}")
        val receiverChannel = chatService.channelRegistry.channel("SYSTEM:${invitation.received}")

        val map = mutableMapOf<String, String>().apply {
            set("player", issuerName!!)
            set("target", receiverName!!)
            set("nation", nation.name)
        }

        issuerChannel.also {
            it.session().system().send(printer.chatText(ResultMessage("ALERT_TIMEOUT", data = map)))
        }

        receiverChannel.also {
            it.session().system().send(printer.chatText(ResultMessage("INVITE_TIMEOUT", data = map)))
        }
    }
}