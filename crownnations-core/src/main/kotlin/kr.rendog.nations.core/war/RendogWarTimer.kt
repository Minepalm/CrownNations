package kr.rendog.nations.core.war

import kr.rendog.nations.NationEventBus
import kr.rendog.nations.event.WarTimeoutEvent
import kr.rendog.nations.war.NationWarTimer
import kr.rendog.nations.war.WarTime
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.atomic.AtomicBoolean

class RendogWarTimer(
    private val eventBus: NationEventBus,
    private val executor: ExecutorService,
    private val worker: ExecutorService,
    private val period: Long
) : NationWarTimer {

    private val start = AtomicBoolean(false)
    private val subscriptions = ConcurrentHashMap<Int, WarTime>()

    init{
        start.set(true)
        executor.execute { loop() }
    }

    @Synchronized
    private fun loop(){
        while (start.get()){
            val map = mutableMapOf<Int, WarTime>().apply { putAll(subscriptions) }
            map.forEach { (matchId, time) ->
                if(time.isTimeout()){
                    worker.execute{ invokeTimeout(matchId) }
                    subscriptions.remove(matchId)
                }
            }
            Thread.sleep(period)
        }
    }

    override fun subscribe(matchId: Int, time: WarTime) {
        subscriptions[matchId] = time
    }

    override fun unsubscribe(matchId: Int) {
        subscriptions.remove(matchId)
    }

    override fun invokeTimeout(matchId: Int) {
        eventBus.invoke(WarTimeoutEvent(matchId))
    }
}