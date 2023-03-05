package com.minepalm.nations.core.operation

import com.minepalm.nations.NationOperation
import com.minepalm.nations.OperationResult
import com.minepalm.nations.ResultCode
import com.minepalm.nations.exception.OperationFailedException
import com.minepalm.nations.exception.OperationInterruptedException
import java.util.concurrent.ConcurrentHashMap

abstract class AbstractNationOperation<T : Any>: NationOperation<T> {

    val data : MutableMap<String, Any> = ConcurrentHashMap<String, Any>()

    override fun <T : Any> get(key: String, clazz: Class<T>): T {
        if(data.containsKey(key)) {
            return (data[key] as? T)
                ?: throw IllegalArgumentException("type mismatch: " +
                        "expected: ${clazz.simpleName}, " +
                        "actual: ${data[key]!!::class.java.simpleName}")
        }else{
            throw IllegalArgumentException("value $key not found")
        }
    }

    override fun set(key: String, value: Any) {
        data[key] = value
    }

    override fun process(): OperationResult<T> {
        try {
            checkOrThrow()
            process0()
        } catch (failed: OperationFailedException) {
            setCode(failed.code)
            setException(failed)
        } catch (_: OperationInterruptedException) {
            //skip this
        } catch (unchecked: Throwable) {
            setCode(ResultCode.UNCHECKED_EXCEPTION)
            setException(unchecked)
        }
        return processComplete(getCode(), getResult(), getException())
    }

    override fun check(): OperationResult<Boolean> {
        return try {
            checkOrThrow()
            generateResult(ResultCode.SUCCESSFUL, true)
        } catch (failed: OperationFailedException) {
            generateResult(failed.code, false, failed)
        } catch (unchecked: Throwable) {
            generateResult(ResultCode.UNCHECKED_EXCEPTION, false, unchecked)
        } finally {
            data.clear()
        }
    }

    abstract fun checkOrThrow()

    abstract fun process0()

    open fun rollback() {
        //do nothing
    }

    fun setCode(code: String) {
        data["code"] = code
    }

    fun getCode(): String {
        return data["code"] as? String ?: ResultCode.PROGRESS
    }

    fun setResult(t: T) {
        data["result"] = t
    }

    fun getResult() : T?{
        return data["result"] as? T?
    }

    fun setMessage(msg : String){
        data["message"] = msg
    }

    fun getMessage() : String{
        return data["message"] as? String ?: ""
    }

    fun setException(ex : Throwable){
        data["exception"] = ex
    }

    fun getException() : Throwable?{
        return data["exception"] as? Throwable?
    }

    protected fun processComplete(code: String, result: T?, exception: Throwable? = null): OperationResult<T> {
        return try{
            setCode(code)
            OperationResult(code, result, mutableMapOf<String, Any>().apply { putAll(data) }, exception)
        }finally {
            data.clear()
        }
    }

    protected fun <R : Any> generateResult(code: String, result: R, ex: Throwable? = null): OperationResult<R> {
        setCode(code)
        return OperationResult(code, result, mutableMapOf<String, Any>().apply { putAll(data) }, ex)
    }

    protected fun fail(code: String, msg: String) {
        throw OperationFailedException(code, msg)
    }

    protected fun success(result: T) {
        setCode(ResultCode.SUCCESSFUL)
        setResult(result)
        throw OperationInterruptedException(ResultCode.SUCCESSFUL, "success")
    }
}