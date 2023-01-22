package com.minepalm.nations.exception

class OperationFailedException(
    val code: String, message: String, cause: Throwable? = null
) : RuntimeException(message, cause)