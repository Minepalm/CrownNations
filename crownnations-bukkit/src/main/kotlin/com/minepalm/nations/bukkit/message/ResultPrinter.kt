package com.minepalm.nations.bukkit.message

import com.minepalm.nations.bukkit.BugReporter
import com.minepalm.palmchat.api.ChatText
import com.minepalm.palmchat.api.TextType
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags
import org.bukkit.Bukkit

class ResultPrinter(
    val tag: String,
    private val reporter: BugReporter
) {

    //todo: network broadcast 에도 kyori mini message 가 있는데, PalmLibrary 로 빼놓기.
    companion object {
        @JvmStatic
        val kyori = MiniMessage
            .builder()
            .tags(
                TagResolver.builder()
                    .resolver(StandardTags.defaults())
                    .build()
            ).build()

    }

    private val map = mutableMapOf<String, List<String>>()
    private val replacements = mutableListOf<Replace>()

    fun containsMessage(code: String): Boolean {
        return map.containsKey(code)
    }

    fun registerReplacement(func: Replace) {
        replacements.add(func)
    }

    fun registerText(textCode: String, text: List<String>) {
        map[textCode] = text
    }

    operator fun get(key: String): Component {
        return map[key]
            ?.joinToString(separator = "<newline>")
            ?.let { kyori.deserialize(it) }
            ?: Component.text(key)
    }

    //todo:
    // build 에서 exception report 까지 handling 하고 있음.
    // 이건 나중에 깔끔하게 분리해야 함.
    fun build(result: ResultMessage): Component {
        if (result.operation?.exception != null) {
            reporter.report(result)
        }
        Bukkit.getLogger().info("result message: $result")
        val list = map[result.messageCode] ?: mutableListOf<String>().apply { add("<$tag.${result.messageCode}>") }
        return list.joinToString(separator = "<newline>") {
            var str = it
            replacements.forEach { replace -> str = replace.replace(str, result) }
            str
        }.let {
            Bukkit.getLogger().info("player will receive: $it")
            kyori.deserialize(it)
        }
    }

    fun chatText(result: ResultMessage): ChatText {
        return ChatText(TextType.KYORI, build(result).let {
            kyori.serialize(it).apply {
                Bukkit.getLogger().info("serialized: $this")
            }
        })
    }
}