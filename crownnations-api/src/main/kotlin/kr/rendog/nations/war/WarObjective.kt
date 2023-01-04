package kr.rendog.nations.war

import java.util.*

// 로그 타입:
// 1. PLAYER_KILL - user_uuid, victim_uuid // nation_id = killer's nation id
// 3. CASTLE_FALLEN - monument_id, user
// 4. NATION_FALLEN - 데이터 필요 x
// 5. WIN
// 6. LOSE
// 5번 6번 없으면 무승부
// 로그 읽고 그대로 복원해야함.
// 전쟁 종료의 경우 개별 서버에서 개별적으로 처리 ( 시간이라든지 기록 표시라든지 )
// 1. 그럼 MMR 반영은? -> SET 하는 방법?
// 2. Session DB에 있는지 확인 -> 가장 먼저 확인한 서버가 확인 후 삭제
// 3. 모든 서버가 디비를 한번 조회하는건 어쩔수 없음. local session manager 에서 end game event 수신했을때 캔슬.
// 4. 우선순위는
// - 1. war session 생성한 서버
// - 2. 수신받은 서버
data class WarObjective(
    val matchId: Int,
    val nationId: Int,
    val objectiveType: Type,
    val user: UUID,
    val time: Long = System.currentTimeMillis(),
    val data: String
) {
    enum class Type{
        PLAYER_KILL, CASTLE_FALLEN, NATION_FALLEN
    }
}