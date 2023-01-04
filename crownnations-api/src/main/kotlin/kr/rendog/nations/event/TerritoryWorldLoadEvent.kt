package kr.rendog.nations.event

data class TerritoryWorldLoadEvent(
    val server: String,
    val worldName: String,
    val time: Long = System.currentTimeMillis()
) : NationEvent, SendingEvent{
}