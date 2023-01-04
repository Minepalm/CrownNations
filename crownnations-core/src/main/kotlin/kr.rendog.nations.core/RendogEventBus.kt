package kr.rendog.nations.core

import kr.rendog.nations.NationEventBus
import kr.rendog.nations.NationEventListener
import kr.rendog.nations.event.NationEvent
import kr.rendog.nations.server.NationServer
import java.util.concurrent.ConcurrentHashMap

open class RendogEventBus : NationEventBus {

    private val listeners = ConcurrentHashMap<Class<out NationEvent>, MutableList<NationEventListener<out NationEvent>>>()
    private val finalizers = ConcurrentHashMap<Class<out NationEvent>, MutableList<NationEventListener<out NationEvent>>>()
    private val initializers = ConcurrentHashMap<Class<out NationEvent>, MutableList<NationEventListener<out NationEvent>>>()

    @Synchronized
    override fun <T : NationEvent> addListener(clazz: Class<T>, listener: NationEventListener<T>) {
        if(!listeners.containsKey(clazz))
            listeners[clazz] = mutableListOf()
        listeners[clazz]!!.add(listener)
    }

    @Synchronized
    override fun <T : NationEvent> addEventInitializer(clazz: Class<T>, listener: NationEventListener<T>) {
        if(!finalizers.containsKey(clazz))
            finalizers[clazz] = mutableListOf()
        finalizers[clazz]!!.add(listener)
    }

    @Synchronized
    override fun <T : NationEvent> addEventFinalizer(clazz: Class<T>, listener: NationEventListener<T>) {
        if(!initializers.containsKey(clazz))
            initializers[clazz] = mutableListOf()
        initializers[clazz]!!.add(listener)
    }

    override fun <T : NationEvent> invoke(event: T) {
        initializers[event::class.java]?.forEach { (it as NationEventListener<T>).onEvent(event) }
        listeners[event::class.java]?.forEach { (it as NationEventListener<T>).onEvent(event) }
        finalizers[event::class.java]?.forEach { (it as NationEventListener<T>).onEvent(event) }
    }

    override fun <T : NationEvent> invoke(event: T, func: () -> Unit) {
        initializers[event::class.java]?.forEach { (it as NationEventListener<T>).onEvent(event) }
        listeners[event::class.java]?.forEach { (it as NationEventListener<T>).onEvent(event) }
        func.invoke()
        finalizers[event::class.java]?.forEach { (it as NationEventListener<T>).onEvent(event) }
    }


}