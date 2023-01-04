package kr.rendog.nations.bukkit.commands.user

import com.minepalm.arkarangutils.bukkit.BukkitExecutor
import com.minepalm.arkarangutils.invitation.InvitationService
import kr.rendog.nations.Nation
import kr.rendog.nations.NationService
import kr.rendog.nations.OperationResult
import kr.rendog.nations.ResultCode
import kr.rendog.nations.bukkit.PlayerCache
import kr.rendog.nations.bukkit.invitation.MySQLNationInvitationDatabase
import kr.rendog.nations.bukkit.message.ResultMessage
import kr.rendog.nations.bukkit.message.ResultPrinter
import kr.rendog.nations.bukkit.sendMessage
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.CompletableFuture

//todo: invite 부분 컨피그 수정하기
sealed class UserCommandInvite {

    class Request(
        private val service: NationService,
        private val players: PlayerCache,
        private val invitations: InvitationService,
        private val printer: ResultPrinter,
        private val database: MySQLNationInvitationDatabase,
        private val executor: BukkitExecutor
    ){

        fun whenCommand(player: Player, username: String){
            executor.async{
                val uuid = players.uuid(username)
                val nationFuture = service.memberRegistry[player.uniqueId].direct.getNation()

                val result = execute(player, username, uuid, nationFuture )
                val nation = nationFuture.join()

                val map = mutableMapOf<String, String>().apply {
                    set("player", player.name)
                    set("nation", nation?.name ?: "")
                    set("target", username)
                }

                if( result.result == true ) {
                    database.addInvitedNation(uuid!!, nation!!.name, player.uniqueId,
                        System.currentTimeMillis()+30000L)
                    invitations.sender(player.uniqueId).invite(uuid)
                }else{
                    val msgCode = when{
                        printer.containsMessage(result.code) -> result.code
                        else -> "ERROR"
                    }
                    printer.build(ResultMessage(msgCode, result, map))
                }

            }
        }

        private fun execute(player: Player, username: String, uuid: UUID?, nationFuture: CompletableFuture<Nation?>)
        : OperationResult<Boolean>{
            if(uuid == null){
                return OperationResult(ResultCode.PLAYER_NOT_EXISTS, false)
            }

            if(players.isOnline(username)){
                return OperationResult(ResultCode.PLAYER_NOT_ONLINE, false)
            }

            val nation = nationFuture.join() ?: return OperationResult(ResultCode.NO_NATION, false)

            return nation.operateAddMember(service.memberRegistry[player.uniqueId], uuid).check()

        }
    }

    class Accept(
        private val invitations: InvitationService,
        private val database: MySQLNationInvitationDatabase,
        private val printer: ResultPrinter,
        private val executor: BukkitExecutor
    ){
        fun whenCommand(player: Player, nationName: String){
            executor.async {
                val map = database.getInvitedNations(player.uniqueId).join()

                if(map.containsKey(nationName)) {
                    val successful = invitations.receiver(player.uniqueId).accept(map[nationName]!!).join()
                }else{
                    player.sendMessage(printer["NO_INVITATION"].toTypedArray())
                }
            }
        }

    }

    class Reject(
        private val invitations: InvitationService,
        private val database: MySQLNationInvitationDatabase,
        private val printer: ResultPrinter,
        private val executor: BukkitExecutor
    ){
        fun whenCommand(player: Player, nationName: String){
            executor.async {
                val map = database.getInvitedNations(player.uniqueId).join()
                if(map.containsKey(nationName)) {
                    val successful = invitations.receiver(player.uniqueId).deny(map[nationName]!!).join()
                }else{
                    player.sendMessage(printer["NO_INVITATION"].toTypedArray())
                }
            }
        }

    }
}