package com.minepalm.nations

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantLock
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
class Dependencies {

    companion object {
        private val hub: Dependencies = Dependencies()

        fun <T : Any> register(clazz: Class<out T>, any: T) {
            hub.register(clazz, any)
        }

        operator fun <T : Any> get(clazz: Class<T>): Depend<T> {
            return hub[clazz]
        }

        operator fun <T : Any> get(clazz: KClass<T>): Depend<T> {
            return hub[clazz.java]
        }
    }

    private val locks = ConcurrentHashMap<Class<*>, ReentrantLock>()
    private val dependencies = ConcurrentHashMap<Class<*>, Depend<*>>()

    fun <T : Any> register(clazz: Class<out T>, value: T) {

        if (!dependencies.containsKey(clazz)) {
            assign(clazz)
        }

        val lock = locks[clazz] ?: ReentrantLock().apply { locks[clazz] = this }

        synchronized(lock) {
            if (dependencies.containsKey(clazz)) {
                synchronized(dependencies[clazz]!!) {
                    if (dependencies.containsKey(clazz)) {
                        (dependencies[clazz]!! as Depend<T>).apply {
                            set(value)
                        }
                    } else {
                        Depend<T>().apply { set(value) }.let { dependencies[clazz] = it }
                    }
                }
            }
        }

    }

    private fun <T : Any> assign(clazz: Class<T>): Depend<T> {
        if (!locks.containsKey(clazz)) {
            locks[clazz] = ReentrantLock()
        }

        val lock = locks[clazz] ?: ReentrantLock().apply { locks[clazz] = this }

        return synchronized(lock) {
            if (!dependencies.containsKey(clazz)) {
                Depend<T>().apply { dependencies[clazz] = this }
            } else {
                dependencies[clazz] as Depend<T>
            }
        }
    }

    operator fun <T : Any> get(clazz: Class<T>): Depend<T> {
        return dependencies[clazz] as? Depend<T> ?: assign(clazz)
    }

    class Depend<T : Any> {

        @Volatile
        private lateinit var delegate: T

        fun get() = delegate

        fun getOrNull() = if (::delegate.isInitialized) delegate else null

        @Synchronized
        internal fun set(value: T) {
            delegate = value
        }

        operator fun getValue(thisRef: Any, property: Any): T {
            return get()
        }
    }

}

inline infix fun <reified T: Any> T.initAs(clazz: KClass<T>): T {
    Dependencies.register(clazz.java, this)
    return this
}