package com.minepalm.nations.bukkit.message

import com.minepalm.nations.OperationResult
import com.minepalm.nations.utils.TextMetadata

data class ResultMessage(
    val messageCode: String,
    val operation: OperationResult<*>? = null,
    val data: MutableMap<String, String> = mutableMapOf()
) : TextMetadata{
    override operator fun get(key: String): String{
        return data[key] ?: ""
    }

    override operator fun set(key: String, value: String){
        data[key] = value
    }
}