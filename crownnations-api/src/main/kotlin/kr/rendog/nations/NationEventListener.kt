package kr.rendog.nations

import kr.rendog.nations.event.NationEvent

interface NationEventListener<T : NationEvent> {

    fun onEvent(event: T)
}