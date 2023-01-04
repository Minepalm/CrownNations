package kr.rendog.nations.bukkit.commands

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.Subcommand
import com.minepalm.arkarangutils.bukkit.BukkitExecutor
import kr.rendog.nations.NationService
import kr.rendog.nations.bukkit.PlayerCache
import kr.rendog.nations.bukkit.commands.admin.*
import org.bukkit.entity.Player

/**
 * /국가관리 강제양도
 * /국가관리 강제임명
 * /국가관리 강제초대 <국가> <닉넴>
 * /국가관리 강제강퇴 <국가> <닉넴>
 *
 * /국가관리 삭제 [국가] - 해당 국가에 모든 블럭과 함께 국가 삭제
 * /국가관리 복구 [국가] - 해당 국가를 스키메틱으로 국가 블럭 복원, 왕만 국가에 있는 상태로 복구
 * /국가관리 정리 - 금고가 마이너스인 국가 삭제
 * /국가관리 강화 [레벨] - 해당 레벨의 국가 강화 재료 설정
 * /국가관리 선포권 - 손에 들고 있는 아이템을 선포권으로 지정
 * /국가관리 보상 - 신호기 파괴 시 나오는 보상 설정
 * /국가관리 전초기지 [레벨] - 손에 들고 있는 아이템을 해당 레벨의 전초기지 아이템으로 설정
 *
 * */
@CommandAlias("국가관리")
class AdminCommands(
    private val service : NationService,
    private val players: PlayerCache,
    private val executor : BukkitExecutor
) : BaseCommand(){

    @Subcommand("강제양도")
    fun forceTransfer(player: Player, nationName: String, transferTo: String){
        AdminCommandTransfer(service, players, executor).whenCommand(player, nationName, transferTo)
    }

    @Subcommand("강제임명")
    fun forceSetRank(player: Player, nationName: String, username: String, rankName: String){
        AdminCommandSetRank(service, players, executor).whenCommand(player, nationName, username, rankName)
    }

    @Subcommand("강제초대")
    fun forceAddMember(player: Player, nationName: String, username: String){
        AdminCommandAddMember(service, players, executor).whenCommand(player, nationName, username)
    }

    @Subcommand("강제강퇴|강제추방")
    fun forceKick(player: Player, nationName: String, username: String){
        AdminCommandKick(service, players, executor).whenCommand(player, nationName, username)
    }

    @Subcommand("삭제")
    fun forceDisband(player: Player, nationName: String){
        AdminCommandDisband(service, executor).whenCommand(player, nationName)
    }

    @Subcommand("지급")
    fun give(player: Player, itemType: String){
        AdminCommandGive(service.config).whenCommand(player, itemType)
    }

    @Subcommand("정보")
    fun info(player: Player){
        AdminCommandInfo(service).whenCommand(player)
    }
}