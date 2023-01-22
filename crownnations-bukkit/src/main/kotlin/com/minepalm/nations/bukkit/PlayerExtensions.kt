package com.minepalm.nations.bukkit

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.entity.Player

object UserCommandExtensions

fun Player.sendMessage(list: List<String>){
    sendMessage(list.toTypedArray())
}

fun Player.sendMessage(args: Array<String>){
    val component = Component.empty().apply { args.forEach { append(Component.text(it)) } }
    Bukkit.getLogger().info(component.content())
    Bukkit.getLogger().info("-------------")
    Bukkit.getLogger().info(component.toString())
    this.sendMessage(component)
}
