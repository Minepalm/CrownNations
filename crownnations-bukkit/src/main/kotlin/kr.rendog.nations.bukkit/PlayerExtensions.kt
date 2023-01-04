package kr.rendog.nations.bukkit

import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

object UserCommandExtensions

fun Player.sendMessage(list: List<String>){
    val component = Component.empty().apply { list.forEach { append(Component.text(it)) } }
    sendMessage(component)
}

fun Player.sendMessage(args: Array<String>){
    val component = Component.empty().apply { args.forEach { append(Component.text(it)) } }
    sendMessage(component)
}
