package com.minepalm.nations.bukkit.commands

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.Default
import co.aikar.commands.annotation.Subcommand
import com.minepalm.arkarangutils.bukkit.BukkitExecutor
import com.minepalm.arkarangutils.invitation.InvitationService
import com.minepalm.nations.NationService
import com.minepalm.nations.bukkit.CreationSessionRegistry
import com.minepalm.nations.bukkit.PlayerCache
import com.minepalm.nations.bukkit.commands.user.*
import com.minepalm.nations.bukkit.invitation.MySQLNationInvitationDatabase
import com.minepalm.nations.bukkit.message.PrinterRegistry
import org.bukkit.entity.Player

/**
 *
 * /국가 생성 [이름] - 국가 대기열에 진입
 * /국가 초대 [닉네임] - 초대 메시지를 보냄. 초대를 받은 상대는 30초 동안 수락/거절 가능
 * /국가 추방 [닉네임] - 해당 플레이어를 추방
 * /국가 양도 [닉네임] - 해당 플레이어에게 국가를 양도, 국가원 시민에게만 양도 가능, 쿨타임 3일
 * /국가 직급 [닉네임] [직급] - 플레이어를 해당 직급으로 변경
 * /국가 금고 [입금, 출금] [골드] - 골드 만큼 금고에서 입출금
 * /국가 정보 [이름] - 이름 칸을 비워둘 시 본인 국가 정보 확인, 이름 적을 시 타 국가 정보 확인
 * /국가 초대수락 - 초대 수락
 * /국가 초대거절 - 초대 거절
 * /국가 탈퇴 - 국가 탈퇴, 탈퇴 할 시 하루 동안 국가에 못들어감
 * /국가 채팅 - 국가 채팅으로 전환, 한번 더 입력시 전체 채팅
 * /국가 이동 - 신호기의 위치로 3초 후에 이동, 전쟁 중인 국가는 10초 후에 이동
 * /국가 세금 - 시스템에서 다음 세금 빠져나갈 시간과 돈을 확인
 * /국가 강화 - 보류
 * /국가 양도쿨타임 - 국가 양도 쿨타임 확인
 * /국가 재가입쿨타임 - 언제 국가를 다시 가입할 수 있는지 확인
 * /국가 뉴비보호제거 - 국가 뉴비보호를 제거, 전쟁 가능
 * /국가 목록 - 국가들의 정보를 GUI화
 * /국가 랭킹 - 내림차순으로 채팅에 출력
 *
 */
@CommandAlias("국가|n|국")
class UserCommands(
    val service: NationService,
    val players: PlayerCache,
    val reg: PrinterRegistry,
    val sessions: CreationSessionRegistry,
    val invitationService: InvitationService,
    val inviteDatabase: MySQLNationInvitationDatabase,
    val executor: BukkitExecutor
) : BaseCommand() {

    @Default
    @Subcommand("도움말")
    fun help(player: Player, @Default("1") page: Int) {
        UserCommandHelp(reg["HELP"]).whenCommand(player, page)
    }

    @Subcommand("생성")
    fun create(player: Player, name: String) {
        UserCommandCreate(service, sessions, reg["CREATE"], executor).whenCommand(player, name)
    }

    @Subcommand("해산|해체")
    fun disband(player: Player) {
        UserCommandDisband(service, reg["DISBAND"], executor).whenCommand(player)
    }

    @Subcommand("초대")
    fun invite(player: Player, username: String) {
        UserCommandInvite.Request(service, players, invitationService, reg["ADD_MEMBER"], inviteDatabase, executor)
            .whenCommand(player, username)
    }

    @Subcommand("추방|강퇴")
    fun kick(player: Player, username: String) {
        UserCommandKick(service, players, reg["REMOVE_MEMBER"], executor).whenCommand(player, username)
    }

    @Subcommand("양도|위임")
    fun transfer(player: Player, username: String) {
        UserCommandTransfer(service, players, reg["TRANSFER"], executor).whenCommand(player, username)
    }

    @Subcommand("직위|직급")
    fun setRank(player: Player, username: String, rankIn: String) {
        UserCommandSetRank(service, players, reg["SET_RANK"], executor).whenCommand(player, username, rankIn)
    }

    @Subcommand("입금")
    fun deposit(player: Player, value: Double) {
        UserCommandDeposit(service, reg["DEPOSIT"], executor).whenCommand(player, value)
    }

    fun withdraw(player: Player, value: Double) {
        UserCommandWithdraw(service, reg["WITHDRAW"], executor).whenCommand(player, value)
    }

    @Subcommand("정보")
    fun info(player: Player, @Default("!self!") nationName: String) {
        if (nationName == "!self!")
            UserCommandInfo(service, executor, reg["INFO"], players).whenCommand(player)
        else
            UserCommandInfo(service, executor, reg["INFO"], players).whenCommand(player, nationName)
    }

    @Subcommand("수락")
    fun inviteAccept(player: Player, nationName: String) {
        UserCommandInvite.Accept(invitationService, inviteDatabase, reg["ADD_MEMBER"], executor)
            .whenCommand(player, nationName)
    }

    @Subcommand("거절")
    fun inviteReject(player: Player, nationName: String) {
        UserCommandInvite.Reject(invitationService, inviteDatabase, reg["ADD_MEMBER"], executor)
            .whenCommand(player, nationName)
    }

    @Subcommand("탈퇴|떠나기")
    fun leave(player: Player) {
        UserCommandLeave(service, reg["REMOVE_MEMBER"], executor).whenCommand(player)
    }

    fun toggleChat(player: Player) {

    }

    fun tax(player: Player) {

    }

    fun teleport(player: Player) {

    }
}