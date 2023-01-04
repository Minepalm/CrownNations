package kr.rendog.nations.exception

class OperationInterruptedException(
    val code: String, message: String, cause: Exception? = null): RuntimeException(message) {
}