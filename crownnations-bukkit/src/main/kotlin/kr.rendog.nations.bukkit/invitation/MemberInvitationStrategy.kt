package kr.rendog.nations.bukkit.invitation

import com.minepalm.arkarangutils.invitation.Invitation
import com.minepalm.arkarangutils.invitation.InvitationExecuteStrategy
import com.minepalm.palmchat.api.ChatPlayer
import com.minepalm.palmchat.api.ChatService
import kr.rendog.nations.NationService
import kr.rendog.nations.bukkit.PlayerCache
import kr.rendog.nations.bukkit.message.ResultMessage
import kr.rendog.nations.bukkit.message.ResultPrinter
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

        issuerChannel?.also {
            it.context(ChatPlayer.SYSTEM, "")
                .send(printer.build(ResultMessage("INVITE_SUCCESS", data = map)).joinToString("\n"))
        }

        receiverChannel?.also {
            it.context(ChatPlayer.SYSTEM, "")
                .send(printer.build(ResultMessage("INVITE_RECEIVE", data = map)).joinToString("\n"))
        }
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
            receiverChannel?.also {
                it.context(ChatPlayer.SYSTEM, "")
                    .send(printer.build(ResultMessage("INVALIDATE_INVITATION", data = map)).joinToString("\n"))
            }
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
                issuerChannel?.also {
                    it.context(ChatPlayer.SYSTEM, "")
                        .send(printer.build(ResultMessage("ALERT_ACCEPT", data = map)).joinToString("\n"))
                }

            }

            receiverChannel?.also {
                it.context(ChatPlayer.SYSTEM, "")
                    .send(printer.build(ResultMessage(messageCode, data = map)).joinToString("\n"))
            }

        }
    }

    override fun onDeny(invitation: Invitation) {
        val issuerName = players.username(invitation.issuer)
        val receiverName = players.username(invitation.received)
        val nation = service.memberRegistry[invitation.issuer].cache.getNation()
        val issuerChannel = chatService.channelRegistry.channel("SYSTEM:${invitation.issuer}")
        val receiverChannel = chatService.channelRegistry.channel("SYSTEM:${invitation.received}")

        val map = mutableMapOf<String, String>().apply {
            set("player", issuerName!!)
            set("target", receiverName!!)
        }

        if(nation == null){
            receiverChannel?.also {
                it.context(ChatPlayer.SYSTEM, "")
                    .send(printer.build(ResultMessage("INVALIDATE_INVITATION", data = map)).joinToString("\n"))
            }
            return
        }

        map.apply {
            set("nation", nation.name)
        }

        issuerChannel?.also {
            it.context(ChatPlayer.SYSTEM, "").send(printer.build(ResultMessage("ALERT_DENY", data = map)).joinToString("\n"))
        }

        receiverChannel?.also {
            it.context(ChatPlayer.SYSTEM, "").send(printer.build(ResultMessage("INVITE_DENY", data = map)).joinToString("\n"))
        }
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

        issuerChannel?.also {
            it.context(ChatPlayer.SYSTEM, "").send(printer.build(ResultMessage("ALERT_TIMEOUT", data = map)).joinToString("\n"))
        }

        receiverChannel?.also {
            it.context(ChatPlayer.SYSTEM, "").send(printer.build(ResultMessage("INVITE_TIMEOUT", data = map)).joinToString("\n"))
        }
    }
}