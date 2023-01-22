package com.minepalm.nations.bukkit.message

import com.minepalm.palmchat.api.ChatText
import com.minepalm.palmchat.api.TextType
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags

class ResultPrinter(
    val tag : String
) {

    //todo: network broadcast 에도 kyori mini message 가 있는데, PalmLibrary 로 빼놓기.
    companion object{
        @JvmStatic
        val kyori = MiniMessage
            .builder()
            .tags(
                TagResolver.builder()
                    .resolver(StandardTags.color())
                    .resolver(StandardTags.decorations())
                    .build()
            ).build()

    }

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

    operator fun get(key: String): Component{
        return map[key]
            ?.joinToString(separator = "<newline>")
            ?.let { kyori.deserialize(it) }
            ?: Component.text(key)
    }

    fun build(result : ResultMessage) : Component{
        return map[result.messageCode]
            ?.joinToString(separator = "<newline>")
            ?.let { kyori.deserialize(it) }
            ?: Component.text(result.messageCode)
    }

    fun chatText(result: ResultMessage): ChatText{
        return ChatText(TextType.KYORI, build(result).let { kyori.serialize(it) })
    }
}