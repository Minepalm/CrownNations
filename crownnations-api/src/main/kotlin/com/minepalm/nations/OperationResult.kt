package com.minepalm.nations

class OperationResult<T : Any>(
    val code: String,
    val result: T?,
    val data: Map<String, Any> = mutableMapOf(),
    val exception: Throwable? = null
)