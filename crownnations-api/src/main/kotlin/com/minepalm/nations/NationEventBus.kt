package com.minepalm.nations

interface NationEventBus {

    fun <T : com.minepalm.nations.event.NationEvent> addListener(clazz: Class<T>, listener: NationEventListener<T>)

    fun <T : com.minepalm.nations.event.NationEvent> addEventInitializer(
        clazz: Class<T>,
        listener: NationEventListener<T>
    )

    fun <T : com.minepalm.nations.event.NationEvent> addEventFinalizer(
        clazz: Class<T>,
        listener: NationEventListener<T>
    )

    fun <T : com.minepalm.nations.event.NationEvent> invoke(event: T)

    fun <T : com.minepalm.nations.event.NationEvent> invoke(event: T, func: () -> Unit)

}