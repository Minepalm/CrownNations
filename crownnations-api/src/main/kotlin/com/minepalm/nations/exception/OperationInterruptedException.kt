package com.minepalm.nations.exception

class OperationInterruptedException(
    val code: String, message: String, cause: Exception? = null
) : RuntimeException(message)