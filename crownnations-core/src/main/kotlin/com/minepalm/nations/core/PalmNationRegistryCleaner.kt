package com.minepalm.nations.core

import com.minepalm.nations.NationService
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.atomic.AtomicBoolean

class PalmNationRegistryCleaner(
    private val service : NationService,
    private val executor : ExecutorService,
    private val cleanUpPeriod : Duration,
    private val loopPeriod : Long
){

    private val run = AtomicBoolean(false)
    private val lastUpdate = ConcurrentHashMap<Int, Long>()

    @Synchronized
    fun mark(nationId : Int, time : Long){
        lastUpdate[nationId] = time
    }

    @Synchronized
    fun unmark(nationId : Int){
        lastUpdate.remove(nationId)
    }

    fun start(){
        run.set(true)
        executor.execute {
            while (run.get()){
                try{
                    val executedTime = loop()
                    if(executedTime < loopPeriod){
                        Thread.sleep(loopPeriod - executedTime)
                    }
                }catch (_: Throwable){

                }
            }
        }
    }

    fun loop() : Long{
        val time = System.currentTimeMillis()
        for (nation in service.nationRegistry.local.getNations()) {
            val lastUpdated = lastUpdate.getOrDefault(nation.id, 0L)
            if(time - lastUpdated > cleanUpPeriod.toMillis()){
                if(service.nationRegistry.tryInvalidate(nation.id)){
                    unmark(nation.id)
                }else{
                    mark(nation.id, System.currentTimeMillis())
                }
            }
        }

        return System.currentTimeMillis() - time
    }

    fun shutdown(){
        run.set(false)
    }
}