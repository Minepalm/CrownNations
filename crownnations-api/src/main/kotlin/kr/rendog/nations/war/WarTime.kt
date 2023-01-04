package kr.rendog.nations.war

data class WarTime(
    val timeBegin: Long,
    val timePrepare: Long,
    val timeEnd: Long
){

    fun isPrepareTime(): Boolean{
        val time = System.currentTimeMillis()
        return time in timeBegin until timePrepare
    }

    fun isInGame(): Boolean{
        val time = System.currentTimeMillis()
        return time in timePrepare until timeEnd
    }

    fun isStart(): Boolean{
        val time = System.currentTimeMillis()
        return time >= timeBegin
    }

    fun isTimeout(): Boolean{
        val time = System.currentTimeMillis()
        return time > timeEnd
    }

    fun status(): WarStatus{
        return when{
            isPrepareTime() -> WarStatus.PREPARE
            isInGame() -> WarStatus.IN_GAME
            isTimeout() -> WarStatus.END
            else -> WarStatus.IDLE
        }
    }
}