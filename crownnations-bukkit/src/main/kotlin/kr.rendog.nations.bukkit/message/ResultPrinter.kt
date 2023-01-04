package kr.rendog.nations.bukkit.message

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent

class ResultPrinter(
    val tag : String
) {

    private val map = mutableMapOf<String, List<String>>()
    private val replacements = mutableListOf<Replace>()

    fun containsMessage(code: String): Boolean{
        return map.containsKey(code)
    }

    fun registerReplacement(func : Replace){
        replacements.add(func)
    }

    fun registerText(textCode: String, text: List<String>){
        map[textCode] = text
    }

    operator fun get(key: String): List<String>{
        return map[key] ?: mutableListOf()
    }

    fun build(result : ResultMessage) : List<String>{
        val list = map[result.messageCode] ?: mutableListOf<String>().apply { add("<$tag.${result.messageCode}>") }
        return list.map {
            var str = it
            replacements.forEach { replace -> str = replace.replace(str, result) }
            str
        }
    }

    fun component(text: ResultMessage): TextComponent {
        return Component.empty().apply {
            build(text).forEach { this.append(Component.text(it)) }
        }
    }
}