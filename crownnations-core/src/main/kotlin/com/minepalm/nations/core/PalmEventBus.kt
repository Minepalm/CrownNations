package com.minepalm.nations.core

import com.minepalm.nations.NationEventBus
import com.minepalm.nations.NationEventListener
import java.util.concurrent.ConcurrentHashMap

open class PalmEventBus : NationEventBus {

    private val listeners = ConcurrentHashMap<Class<out com.minepalm.nations.event.NationEvent>, MutableList<NationEventListener<out com.minepalm.nations.event.NationEvent>>>()
    private val finalizers = ConcurrentHashMap<Class<out com.minepalm.nations.event.NationEvent>, MutableList<NationEventListener<out com.minepalm.nations.event.NationEvent>>>()
    private val initializers = ConcurrentHashMap<Class<out com.minepalm.nations.event.NationEvent>, MutableList<NationEventListener<out com.minepalm.nations.event.NationEvent>>>()

    @Synchronized
    override fun <T : com.minepalm.nations.event.NationEvent> addListener(clazz: Class<T>, listener: NationEventListener<T>) {
        if(!listeners.containsKey(clazz))
            listeners[clazz] = mutableListOf()
        listeners[clazz]!!.add(listener)
    }

    @Synchronized
    override fun <T : com.minepalm.nations.event.NationEvent> addEventInitializer(clazz: Class<T>, listener: NationEventListener<T>) {
        if(!finalizers.containsKey(clazz))
            finalizers[clazz] = mutableListOf()
        finalizers[clazz]!!.add(listener)
    }

    @Synchronized
    override fun <T : com.minepalm.nations.event.NationEvent> addEventFinalizer(clazz: Class<T>, listener: NationEventListener<T>) {
        if(!initializers.containsKey(clazz))
            initializers[clazz] = mutableListOf()
        initializers[clazz]!!.add(listener)
    }

    override fun <T : com.minepalm.nations.event.NationEvent> invoke(event: T) {
        initializers[event::class.java]?.forEach { (it as NationEventListener<T>).onEvent(event) }
        listeners[event::class.java]?.forEach { (it as NationEventListener<T>).onEvent(event) }
        finalizers[event::class.java]?.forEach { (it as NationEventListener<T>).onEvent(event) }
    }

    override fun <T : com.minepalm.nations.event.NationEvent> invoke(event: T, func: () -> Unit) {
        initializers[event::class.java]?.forEach { (it as NationEventListener<T>).onEvent(event) }
        listeners[event::class.java]?.forEach { (it as NationEventListener<T>).onEvent(event) }
        func.invoke()
        finalizers[event::class.java]?.forEach { (it as NationEventListener<T>).onEvent(event) }
    }


}