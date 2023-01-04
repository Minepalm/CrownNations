package kr.rendog.nations.core

import kr.rendog.nations.NationMetadata
import kr.rendog.nations.core.mysql.MySQLNationDataDatabase
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap

class RendogNationMetadata(
    private val nationId : Int,
    private val database : MySQLNationDataDatabase
) : NationMetadata {

    override val modifier : NationMetadata.Modifier = RendogNationMetadataModifier(nationId, database)
    private val map = ConcurrentHashMap<String, String>()

    init{
        sync().join()
    }

    override fun get(key: String): String? {
        return map[key]
    }

    override fun get(key: String, defaultValue: String): String {
        return map[key] ?: defaultValue
    }

    override fun getInt(key: String): Int? {
        return try{
            map[key]?.toInt()
        }catch (_ : Throwable){
            null
        }
    }

    override fun getInt(key: String, defaultValue: Int): Int {
        return try{
            map[key]?.toInt() ?: defaultValue
        }catch (_ : Throwable){
            defaultValue
        }
    }

    override fun getDouble(key: String): Double? {
        return try{
            map[key]?.toDouble()
        }catch (_ : Throwable){
            null
        }
    }

    override fun getDouble(key: String, defaultValue: Double): Double {
        return try{
            map[key]?.toDouble() ?: defaultValue
        }catch (_ : Throwable){
            defaultValue
        }
    }

    override fun getBoolean(key: String): Boolean? {
        return try{
            map[key]?.toBoolean()
        }catch (_ : Throwable){
            null
        }
    }

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return try{
            map[key]?.toBoolean() ?: defaultValue
        }catch (_ : Throwable){
            defaultValue
        }
    }

    override fun set(key: String, value: String?) {
        if(value != null) {
            map[key] = value
            modifier.set(key, value)
        }else{
            remove(key)
        }
    }

    override fun set(key: String, value: Int) {
        set(key, "$value")
    }

    override fun set(key: String, value: Double) {
        set(key, "$value")
    }

    override fun set(key: String, value: Boolean) {
        set(key, "$value")
    }

    override fun remove(key: String) {
        map.remove(key)
        modifier.remove(key)
    }

    override fun sync(): CompletableFuture<Unit> {
        return modifier.getAll().thenApply { map.putAll(it) }
    }

    override fun sync(key: String) : CompletableFuture<Unit>{
        return modifier.get(key).thenApply { set(key, it) }
    }
}