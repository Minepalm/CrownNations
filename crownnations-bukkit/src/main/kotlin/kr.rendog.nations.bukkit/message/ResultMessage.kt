package kr.rendog.nations.bukkit.message

import kr.rendog.nations.OperationResult

data class ResultMessage(
    val messageCode: String,
    val operation: OperationResult<*>? = null,
    val data: MutableMap<String, String> = mutableMapOf()
){
    operator fun get(key: String): String{
        return data[key] ?: ""
    }

    operator fun set(key: String, value: String){
        data[key] = value
    }
}