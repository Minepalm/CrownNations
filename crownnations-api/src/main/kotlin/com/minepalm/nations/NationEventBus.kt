package com.minepalm.nations

import com.minepalm.nations.event.NationEvent

interface NationEventBus {

    fun <T : NationEvent> addListener(clazz: Class<T>, listener: NationEventListener<T>)

    fun <T : NationEvent> addEventInitializer(
        clazz: Class<T>,
        listener: NationEventListener<T>
    )

    fun <T : NationEvent> addEventFinalizer(
        clazz: Class<T>,
        listener: NationEventListener<T>
    )

    fun <T : NationEvent> invoke(event: T)

    fun <T : NationEvent> invoke(event: T, func: () -> Unit)

}