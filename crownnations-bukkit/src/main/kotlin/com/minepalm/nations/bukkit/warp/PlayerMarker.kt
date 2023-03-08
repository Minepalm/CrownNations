package com.minepalm.nations.bukkit.warp

import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class PlayerMarker {

    private val map = ConcurrentHashMap<UUID, DelayedWarpTask>()

    @Synchronized
    fun registerTask(task: DelayedWarpTask) {
        map[task.uuid] = task
    }

    @Synchronized
    fun unregisterTask(uuid: UUID) {
        map.remove(uuid)
    }

    fun unmark(uuid: UUID) {
        map[uuid]?.isCancelled = true
        map.remove(uuid)?.onCancelled?.accept(uuid)
        unregisterTask(uuid)
    }

    fun isMarked(uuid: UUID): Boolean {
        return map.containsKey(uuid)
    }

}