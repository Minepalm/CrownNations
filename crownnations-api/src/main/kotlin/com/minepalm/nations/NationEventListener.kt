package com.minepalm.nations

interface NationEventListener<T : com.minepalm.nations.event.NationEvent> {

    fun onEvent(event: T)
}