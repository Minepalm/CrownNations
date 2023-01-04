package kr.rendog.nations

interface NationOperation<T : Any> {

    fun check(): OperationResult<Boolean>

    fun process() : OperationResult<T>

    operator fun set(key: String, value: Any)

    operator fun <T: Any> get(key: String, clazz: Class<T>): T
}