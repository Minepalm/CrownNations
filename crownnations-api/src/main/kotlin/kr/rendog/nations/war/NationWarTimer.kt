package kr.rendog.nations.war

interface NationWarTimer {

    fun subscribe(matchId: Int, time: WarTime)

    fun unsubscribe(matchId: Int)

    fun invokeTimeout(matchId: Int)
}