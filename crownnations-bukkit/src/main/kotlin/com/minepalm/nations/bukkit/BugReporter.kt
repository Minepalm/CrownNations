package com.minepalm.nations.bukkit

import com.minepalm.nations.bukkit.message.ResultMessage
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class BugReporter(
    private val plugin: JavaPlugin
) {

    fun report(result: ResultMessage) {
        if (result.operation?.exception != null) {
            Bukkit.getScheduler().runTask(plugin, Runnable {
                result.operation.exception?.printStackTrace()
            })
        }
    }
}